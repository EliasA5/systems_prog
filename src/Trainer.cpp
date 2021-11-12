#include "Trainer.h"

typedef std::pair<int, Workout> OrderPair;


Trainer::Trainer(int t_capacity): capacity(t_capacity), open(false){}
int Trainer::getCapacity() const{
    return capacity;
}
void Trainer::addCustomer(Customer* customer){
    customersList.push_back(customer);
}
void Trainer::removeCustomer(int id){
    for(int i = 0; i<customersList.size(); i++){
        if(customersList[i]->getId() == id) {
            customersList.erase(customersList.begin() + i);
            i--;
            //TODO check on move customer
            break;
        }
    }
    //FIXME
//    for(int j = 0; j<orderList.size(); j++)
//        if(orderList[j].first == id) {
//            orderList.erase(orderList.begin() + j);
//            j--;
//        }
}
Customer* Trainer::getCustomer(int id){
    for(int i = 0; i < customersList.size(); i++){
        if(customersList[i]->getId() == id)
            return customersList[i];
    }
    return nullptr;
}
std::vector<Customer*>& Trainer::getCustomers(){
    return customersList;
}
std::vector<OrderPair>& Trainer::getOrders(){
    return orderList;
}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options){
    for(const auto& work_id: workout_ids){
        for(const auto& work_out: workout_options){
            if(work_out.getId() == work_id) {
                orderList.push_back(OrderPair{customer_id, work_out});
                break;
            }
        }
    }
}
//TODO add printing
void Trainer::openTrainer(){
    open = true;
}
void Trainer::closeTrainer(){
    open = false;
}
int Trainer::getSalary(){
    return salary;
}
bool Trainer::isOpen(){
    return open;
}
void Trainer::incSalary() {
    for(const auto& curr_order : orderList)
        salary += curr_order.second.getPrice();
}
void Trainer::incSalary(int val) {
    salary += val;
}
int Trainer::calSalaryForCustomer(int id) {
    int sal = 0;
    for(const auto& order : orderList)
        if(order.first == id)
            sal += order.second.getPrice();
    return sal;
}
void Trainer::deleteCustomers() {
    for(int i = 0; i < customersList.size(); i++){
        delete customersList[i];
        customersList[i] = nullptr;
    }
    customersList.clear();
}
void Trainer::removeOrders() {
    orderList.clear();
}


