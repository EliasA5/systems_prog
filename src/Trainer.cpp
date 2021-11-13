#include "Trainer.h"
#include <algorithm>

typedef std::pair<int, Workout> OrderPair;


Trainer::Trainer(int t_capacity): capacity(t_capacity), open(false), salary(0){}
int Trainer::getCapacity() const{
    return capacity;
}
void Trainer::addCustomer(Customer* customer){
    customersList.push_back(customer);
}
void Trainer::removeCustomer(int id){
    for(int i = 0; i<customersList.size(); i++)
        if (customersList[i]->getId() == id) {
            customersList.erase(customersList.begin() + i);
            i--;
            //TODO check on move customer
            break;
        }
    std::vector<OrderPair> newList;
    for(int i = 0; i<orderList.size(); i++) {
        if (orderList[i].first != id) {
            newList.push_back(orderList[i]);
        }
    }
    orderList = std::move(newList);
//        //FIXME
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
                orderList.push_back(std::make_pair(customer_id, work_out));
                break;
            }
        }
    }
}

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
//deconstructor
Trainer::~Trainer() {deleteCustomers();}
//copy constructor
Trainer::Trainer(const Trainer &other): capacity(other.capacity), open(other.open), salary(other.salary){
    removeOrders();
    for(int i = 0; i< other.orderList.size(); i++)
        orderList.push_back(other.orderList[i]);
    for(int i = 0; i<other.customersList.size(); i++){
        customersList[i] = other.customersList[i]->copy();
    }
}
//move constructor
Trainer::Trainer(Trainer &&other): capacity(other.capacity), open(other.open), salary(other.salary){
    customersList = std::move(other.customersList);
    orderList = std::move(other.orderList);
    other.salary = 0;
    other.capacity = 0;
    other.open = false;
}
//copy assignment
Trainer& Trainer::operator=(const Trainer &other){
    if(this != &other){
        deleteCustomers();
        removeOrders();
        capacity = other.capacity;
        open = other.open;
        salary = other.salary;
        for(int i = 0; i<other.customersList.size(); i++){
            customersList[i] = other.customersList[i]->copy();
        }
        for(int i = 0; i< other.orderList.size(); i++)
            orderList.push_back(other.orderList[i]);
    }
    return *this;
}
//move assignment
Trainer& Trainer::operator=(Trainer &&other){
    if(this != &other){
        deleteCustomers();
        removeOrders();
        capacity = other.capacity;
        open = other.open;
        salary = other.salary;
        customersList = std::move(other.customersList);
        orderList = std::move(other.orderList);
        other.capacity = 0;
        other.open = false;
        other.salary = 0;
    }
    return *this;
}


