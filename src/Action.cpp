#include "Action.h"
#include "Studio.h"
#include <sstream>

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
        return;
    }
    if(trainer->getCustomers().size() + customers.size() > trainer->getCapacity()){
        error("Not enough spaces.\n");
        return;
    }
    for(const auto& customer : customers)
        trainer->addCustomer(customer);
    trainer->openTrainer();
    complete();
}
std::string OpenTrainer::toString() const{
    std::stringstream ss;
    ss <<"open " << trainerId << " ";
    for(const auto& customer : customers){
        ss << customer->getName() << "," << customer->toString() << " ";
    }
    if(getStatus() == ERROR){
        ss << getErrorMsg();
        return ss.str();
    }
    else{
        ss << "Completed\n";
        return ss.str();
    }
}

//order
Order::Order(int id): trainerId(id){}
void Order::act(Studio &studio) {
    Trainer* trainer = studio.getTrainer(trainerId);
    std::vector<Workout> workout_options = studio.getWorkoutOptions();
    if(trainer == nullptr || !trainer->isOpen()){
        error("Trainer does not exist or is not open\n");
        return;
    }
    std::vector<Customer*> customersList = trainer->getCustomers();
    for(const auto& customer : customersList){
        std::vector<int> workout_ids = customer->order(workout_options);
        trainer->order(customer->getId(), workout_ids, workout_options);
    }
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

MoveCustomer::MoveCustomer(int src, int dst, int customerId): srcTrainer(src), dstTrainer(dst), id(customerId) {}
void MoveCustomer::act(Studio &studio) {
    Trainer* src_trainer = studio.getTrainer(srcTrainer);
    Trainer* dst_trainer = studio.getTrainer(dstTrainer);
    if(src_trainer == nullptr || dst_trainer == nullptr){
        error("Cannot move customer\n");
        return;
    }
    Customer* customer = src_trainer->getCustomer(id);
    if(customer == nullptr || dst_trainer->getCustomers().size()+1 > dst_trainer->getCapacity()){
        error("Cannot move customer\n");
        return;
    }
    int salaryForCustomer = src_trainer->calSalaryForCustomer(id);
    src_trainer->removeCustomer(id);
    if(src_trainer->getCustomers().size() == 0){
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
//closeALL
CloseAll::CloseAll() {}
void CloseAll::act(Studio &studio) {
    for(int i = 0; i < studio.getNumOfTrainers(); i++){
        Close(i).act(studio);
        delete studio.getTrainer(i);
    }
    complete();
}
std::string CloseAll::toString() const {
    return "CloseAll Completed\n";
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
    std::cout << "Current Trainer’s Salary: " << trainer->getSalary() << "\n";
    complete();
}
std::string PrintTrainerStatus::toString() const{
    return "PrintTrainerStatus Completed\n";
}

PrintActionsLog::PrintActionsLog() {}
void PrintActionsLog::act(Studio &studio) {
    for(const auto& action : studio.getActionsLog())
        std::cout << action->toString();
}
std::string PrintActionsLog::toString() const {
    return "PrintActionsLog Completed\n";
}




