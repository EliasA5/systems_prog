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
//    if(trainer->getCustomers().size() + customers.size() > (unsigned)trainer->getCapacity()){
//        error("Not enough spaces.\n");
//        std::cout << getErrorMsg();
//        return;
//    }
//    for(const auto& customer : customers)
//        trainer->addCustomer(customer->copy());
    int startSize = trainer->getCustomers().size();
    for(int i = startSize; i < trainer->getCapacity() && (i - startSize) < customers.size(); i++)
        trainer->addCustomer(customers[i-startSize]->copy());
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
    OpenTrainer *other = new OpenTrainer(trainerId, customersList);
    if(getStatus() == ERROR)
        other->error(getErrorMsg());
    else
        other->complete();
    return other;
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
    std::stringstream ss;
    ss << "order " << trainerId << " ";
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
    Order *other = new Order(trainerId);
    if(getStatus() == ERROR)
        other->error(getErrorMsg());
    else
        other->complete();
    return other;
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
    std::stringstream ss;
    ss << "move " << srcTrainer << " " << dstTrainer << " " << id << " ";
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
    MoveCustomer *other = new MoveCustomer(srcTrainer, dstTrainer, id);
    if(getStatus() == ERROR)
        other->error(getErrorMsg());
    else
        other->complete();
    return other;
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
    std::stringstream ss;
    ss << "close " << trainerId << " ";
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
    Close *other = new Close(trainerId);
    if(getStatus() == ERROR)
        other->error(getErrorMsg());
    else
        other->complete();
    return other;
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
    CloseAll *other = new CloseAll();
    other->complete();
    return other;
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
    PrintWorkoutOptions *other = new PrintWorkoutOptions();
    other->complete();
    return other;
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
    PrintTrainerStatus *other = new PrintTrainerStatus(trainerId);
    other->complete();
    return other;
}

PrintActionsLog::PrintActionsLog() {}
void PrintActionsLog::act(Studio &studio) {
    for(const auto& action : studio.getActionsLog()) {
        std::string s = action->toString();
        std::cout << s;
    }
    complete();
}
std::string PrintActionsLog::toString() const {
    return "PrintActionsLog Completed\n";
}
BaseAction* PrintActionsLog::copy() const{
    PrintActionsLog *other = new PrintActionsLog();
    other->complete();
    return other;
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
    return "backup Completed\n";
}
BaseAction* BackupStudio::copy() const{
    BackupStudio *other = new BackupStudio();
    other->complete();
    return other;
}

RestoreStudio::RestoreStudio() {}
void RestoreStudio::act(Studio &studio) {
    if(backup == nullptr){
        error("No backup available\n");
        std::cout << getErrorMsg();
        return;
    }
    studio = (const Studio) *backup;
    complete();
}
std::string RestoreStudio::toString() const {
    std::stringstream ss;
    ss << "restore ";
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
    RestoreStudio *other = new RestoreStudio();
    if(getStatus() == ERROR)
        other->error(getErrorMsg());
    else
        other->complete();
    return other;
}




