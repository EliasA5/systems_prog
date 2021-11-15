#ifndef TRAINER_H_
#define TRAINER_H_

#include <vector>
#include "Customer.h"
#include "Workout.h"
#include <algorithm>

typedef std::pair<int, Workout> OrderPair;

class Trainer{
public:
    Trainer(int t_capacity);
    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options);
    void openTrainer();
    void closeTrainer();
    int getSalary();
    int getCurrentSalary();
    bool isOpen();
    void incSalary();
    void incSalary(int val);
    int calSalaryForCustomer(int id);
    void deleteCustomers();
    void removeOrders();
    //destructor
    virtual ~Trainer();
    //copy constructor
    Trainer(const Trainer &other);
    //move constructor
    Trainer(Trainer &&other);
    //copy assignment
    Trainer& operator=(const Trainer &other);
    //move assignment
    Trainer& operator=(Trainer &&other);
private:
    int capacity;
    bool open;
    int salary;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order for the trainer - (customer_id, Workout)
};


#endif