#include <vector>
#include "Customer.h"
#include "Workout.h"

typedef std::pair<int, Workout> OrderPair;


Trainer::Trainer(int t_capacity) capacity(t_capacity), open(false){}
int Trainer::getCapacity(){
    return capacity;
}
void Trainer::addCustomer(Customer* customer){
    costumerList.push_back(customer);
}
void Trainer::removeCustomer(int id){
    for(int i = 0; i<costumersList.size(); i++){
        if(costumersList[i]->getId() == id) {
            costumersList.erase(customersList.begin() + i);
            i--;
            //TODO check on move costumer
            for(int j = 0; j<orderList.size(); j++)
                if(orderList[j].first == id) {
                    orderList.erase(orderList.begin() + j);
                    j--;
                }
            break;
        }
    }
}
Customer* Trainer::getCustomer(int id){
    for(int i = 0; i < costumersList.size(); i++){
        if(costumersList[i]->getId() == id)
            return costumersList[i];
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
    int salary = 0;
    for(const auto& curr_order : orderList)
        salary += curr_order.second.getPrice();
    return salary;
}
bool Trainer::isOpen(){
    return open;
}



