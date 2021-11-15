#include "Action.h"
#include "Studio.h"

extern Studio* backup;
BaseAction::BaseAction() {}

ActionStatus BaseAction::getStatus() const {
    return status;
}
void BaseAction::complete() {
    status = COMPLETED;
}

void BaseAction::error(std::string errorMsg) {
    this->errorMsg = errorMsg;
    status = ERROR;
}

std::string BaseAction::getErrorMsg() const{
    return errorMsg;
}

//OpenTrainer
OpenTrainer::OpenTrainer(int id, std::vector<Customer *> &customersList): trainerId(id), customers(customersList) {}
void OpenTrainer::act(Studio &studio) {
    Trainer* trainer = studio.getTrainer(trainerId);
    if(trainer == nullptr || trainer->isOpen()) {
        error("Workout session does not exist or is already open.\n");
        std::cout << getErrorMsg();
        return;
    }
    if(trainer->getCustomers().size() + customers.size() > (unsigned)trainer->getCapacity()){
        error("Not enough spaces.\n");
        std::cout << getErrorMsg();
        return;
    }
    for(const auto& customer : customers)
        trainer->addCustomer(customer->copy());
    trainer->openTrainer();

    std::stringstream ss;
    ss << "open " << trainerId << " ";
    for(const auto& customer : customers){
        ss << customer->getName() << "," << customer->toString() << " ";
    }
    toStr = ss.str();
    complete();
}
std::string OpenTrainer::toString() const{
    std::stringstream ss;
    ss << toStr;
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}
BaseAction* OpenTrainer::copy() const{
    std::vector<Customer *> customersList;
    for(unsigned int i = 0; i<customers.size(); i++){
        customersList.push_back(customers[i]->copy());
    }
    return new OpenTrainer(trainerId, customersList);
}
OpenTrainer::~OpenTrainer() {
    for(unsigned int i = 0; i<customers.size(); i++)
        if(customers[i] != nullptr){
            delete customers[i];
            customers[i] = nullptr;
        }
    customers.clear();
}
OpenTrainer::OpenTrainer(const OpenTrainer &other): trainerId(other.trainerId){
    toStr = other.toStr;
    for(unsigned int i = 0; i < other.customers.size(); i++)
        customers.push_back(other.customers[i]->copy());
    if(other.getStatus() == ERROR)
        error(other.getErrorMsg());
    else
        complete();
}
OpenTrainer::OpenTrainer(OpenTrainer&& other): trainerId(other.trainerId){
    toStr = other.toStr;
    customers = std::move(other.customers);
    if(other.getStatus() == ERROR)
        error(other.getErrorMsg());
    else
        complete();
}

//order
Order::Order(int id): trainerId(id){}
void Order::act(Studio &studio) {
    Trainer* trainer = studio.getTrainer(trainerId);
    std::vector<Workout> workout_options = studio.getWorkoutOptions();
    if(trainer == nullptr || !trainer->isOpen()){
        error("Trainer does not exist or is not open\n");
        std::cout << getErrorMsg();
        return;
    }
    std::vector<Customer*> customersList = trainer->getCustomers();
    for(const auto& customer : customersList){
        std::vector<int> workout_ids = customer->order(workout_options);
        trainer->order(customer->getId(), workout_ids, workout_options);
    }
    std::stringstream strm;
    for(const auto& order: trainer->getOrders())
        strm << trainer->getCustomer(order.first)->getName() << " Is Doing " << order.second.getName() << "\n";
    std::cout << strm.str();
    complete();
}
std::string Order::toString() const {
    std::stringstream ss("order ");
    ss << trainerId << " ";
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}
BaseAction* Order::copy() const{
    return new Order(trainerId);
}

MoveCustomer::MoveCustomer(int src, int dst, int customerId): srcTrainer(src), dstTrainer(dst), id(customerId) {}
void MoveCustomer::act(Studio &studio) {
    Trainer* src_trainer = studio.getTrainer(srcTrainer);
    Trainer* dst_trainer = studio.getTrainer(dstTrainer);
    if(src_trainer == nullptr || dst_trainer == nullptr){
        error("Cannot move customer\n");
        std::cout << getErrorMsg();
        return;
    }
    Customer* customer = src_trainer->getCustomer(id);
    if(customer == nullptr || dst_trainer->getCustomers().size()+1 > (unsigned)dst_trainer->getCapacity()){
        error("Cannot move customer\n");
        std::cout << getErrorMsg();
        return;
    }
    int salaryForCustomer = src_trainer->calSalaryForCustomer(id);
    src_trainer->removeCustomer(id);
    if(src_trainer->getCustomers().empty()){
        Close(srcTrainer).act(studio);
    }
    dst_trainer->addCustomer(customer);
    dst_trainer->incSalary(salaryForCustomer);
    complete();
}
std::string MoveCustomer::toString() const {
    std::stringstream ss("move ");
    ss << srcTrainer << " " << dstTrainer << " " << id << " ";
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}
BaseAction* MoveCustomer::copy() const{
    return new MoveCustomer(srcTrainer, dstTrainer, id);
}

Close::Close(int id): trainerId(id){}
void Close::act(Studio &studio) {
    Trainer* trainer = studio.getTrainer(trainerId);
    if(trainer == nullptr || !trainer->isOpen()){
        error("Trainer does not exist or is not open\n");
        std::cout << getErrorMsg();
        return;
    }
    trainer->incSalary();
    trainer->removeOrders();
    trainer->closeTrainer();
    std::cout << "Trainer " << trainerId << " closed. " << "Salary " << trainer->getSalary() << "NIS\n";
    trainer->deleteCustomers();
    complete();
}
std::string Close::toString() const {
    std::stringstream ss("close ");
    ss << trainerId << " ";
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}
BaseAction* Close::copy() const{
    return new Close(trainerId);
}
//closeALL
CloseAll::CloseAll() {}
void CloseAll::act(Studio &studio) {
    for(int i = 0; i < studio.getNumOfTrainers(); i++){
        Trainer* trainer = studio.getTrainer(i);
        if(trainer == nullptr){
            continue;
        }
        else if(!trainer->isOpen()){
            std::cout << "Trainer " << i << " closed. " << "Salary " << trainer->getSalary() << "NIS\n";
            continue;
        }
        trainer->incSalary();
        trainer->removeOrders();
        trainer->closeTrainer();
        std::cout << "Trainer " << i << " closed. " << "Salary " << trainer->getSalary() << "NIS\n";
        trainer->deleteCustomers();
    }
    studio.deleteTrainers();
    studio.deleteActionsLog();
    complete();
}
std::string CloseAll::toString() const {
    return "CloseAll Completed\n";
}
BaseAction* CloseAll::copy() const{
    return new CloseAll();
}
//PrintWorkoutOptions
PrintWorkoutOptions::PrintWorkoutOptions() {}
void PrintWorkoutOptions::act(Studio &studio) {
    for(const auto& workout : studio.getWorkoutOptions()){
        std::string type = workout.getType() == ANAEROBIC ? "Anaerobic" : workout.getType() == MIXED ? "Mixed" : "Cardio";
        std::cout << workout.getName() << ", " << type << ", " << workout.getPrice() << "\n";
    }
    complete();
}
std::string PrintWorkoutOptions::toString() const{
    return "PrintWorkoutOptions Completed\n";
}
BaseAction* PrintWorkoutOptions::copy() const{
    return new PrintWorkoutOptions();
}
//PrintTrainerStatus
PrintTrainerStatus::PrintTrainerStatus(int id): trainerId(id) {}
void PrintTrainerStatus::act(Studio &studio) {
    Trainer* trainer = studio.getTrainer(trainerId);
    if(trainer == nullptr){
        std::cout << "Trainer does not exist\n";
        return;
    }
    if(!trainer->isOpen()){
        std::cout << "Trainer " << trainerId << " status: closed\n";
        return;
    }
    std::cout << "Trainer " << trainerId << " status: open\n";
    std::cout << "Customers:\n";
    for(const auto& customer : trainer->getCustomers())
        std::cout << customer->getId() << " " << customer->getName() << "\n";
    std::cout << "Orders:\n";
    for(const auto& order : trainer->getOrders())
        std::cout << order.second.getName() << " " << order.second.getPrice() << "NIS " << order.first << "\n";
    std::cout << "Current Trainer’s Salary: " << trainer->getCurrentSalary() << "NIS\n";
    complete();
}
std::string PrintTrainerStatus::toString() const{
    return "PrintTrainerStatus Completed\n";
}
BaseAction* PrintTrainerStatus::copy() const{
    return new PrintTrainerStatus(trainerId);
}

PrintActionsLog::PrintActionsLog() {}
void PrintActionsLog::act(Studio &studio) {
    for(const auto& action : studio.getActionsLog()) {
        std::string s = action->toString();
        std::cout << s;
    }
}
std::string PrintActionsLog::toString() const {
    return "PrintActionsLog Completed\n";
}
BaseAction* PrintActionsLog::copy() const{
    return new PrintActionsLog();
}
BackupStudio::BackupStudio() {}
void BackupStudio::act(Studio &studio) {
    if(backup!= nullptr) {
        delete backup;
        backup = nullptr;
    }
    backup = new Studio(studio);
    complete();
}
std::string BackupStudio::toString() const {
    return "Studio Backed Up Completed\n";
}
BaseAction* BackupStudio::copy() const{
    return new BackupStudio();
}

RestoreStudio::RestoreStudio() {}
void RestoreStudio::act(Studio &studio) {
    if(backup == nullptr){
        error("No backup available\n");
        std::cout << getErrorMsg();
        return;
    }
    studio = *backup;
    complete();
}
std::string RestoreStudio::toString() const {
    std::stringstream ss("restore ");
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}
BaseAction* RestoreStudio::copy() const{
    return new RestoreStudio();
}




