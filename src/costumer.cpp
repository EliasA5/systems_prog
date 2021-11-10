#include "Customer.h"
#include "Workout.h"

#include <map>



//Customer constructor
Customer::Customer(std::string c_name, int c_id):name(c_name),id(c_id){};
//Customer name getter
std::string Customer::getName() const {
    return name;
}
//Customer Id getter
int Customer::getId() const {
    return id;
}


//class SweatyCustomer
SweatyCustomer::SweatyCustomer(std::string name, int id):Customer(name,id){};
std::vector<int> SweatyCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> ordersId;
    for(const auto& currentWorkout : workout_options){
        if(currentWorkout.getType() == CARDIO)
            ordersId.push_back(currentWorkout.getId());
    }
    return ordersId;
}
//TODO add to string
std::string SweatyCustomer::toString() {
    return " ";
}



//class CheapCustomer
CheapCustomer::CheapCustomer(std::string name, int id):Customer(name,id){}
std::vector<int> CheapCustomer::order(const std::vector<Workout> &workout_options) {
    int min = workout_options.at(0).getPrice();
    int minId = workout_options.at(0).getId();
    std::vector<int> ordersId;
    for(const auto& currentWorkout : workout_options){
        if(currentWorkout.getPrice() < min){
            min = currentWorkout.getPrice();
            minId = currentWorkout.getId();
        }
    }
    ordersId.push_back(minId);
    return ordersId;
}
std::string CheapCustomer::toString() {
    return " ";
}



//class HeavyMuscleCustomer
HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int id):Customer(name,id){}
std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> ordersId;
    std::vector<int> orders;
    std::map<int, int> m;
    for(const auto& currentWorkout : workout_options){
        m.insert(std::make_pair(currentWorkout.getPrice(),currentWorkout.getId()));
    }
    for (std::map<int, int>::iterator itr = m.begin(); itr != m.end(); itr++) {
        orders.push_back(itr->second);
    }
    for (int i = orders.size()-1 ; i >= 0; i--) {
        ordersId.push_back(orders.at(i));
    }
    return ordersId;
}
std::string HeavyMuscleCustomer::toString() {
    return " ";
}



//class FullBodyCustomer
FullBodyCustomer::FullBodyCustomer(std::string name, int id):Customer(name,id){}
std::vector<int> FullBodyCustomer::order(const std::vector<Workout> &workout_options) {
    std::vector<int> ordersId;
    int minA,maxM,minC,idA,idM,idC;
    bool firstA=true,firstM= true,firstC=true;
    WorkoutType type;

    for(const auto& currentWorkout : workout_options){
        type = currentWorkout.getType();
        //ANAEROBIC
        if(firstA && type == ANAEROBIC){
            firstA = false;
            minA = currentWorkout.getPrice();
            idA = currentWorkout.getId();
        }
        if (!firstA && type == ANAEROBIC && currentWorkout.getPrice() < minA){
            minA = currentWorkout.getPrice();
            idA = currentWorkout.getId();
        }
        //CARDIO
        if(firstC && type == CARDIO){
            firstC = false;
            minC = currentWorkout.getPrice();
            idC = currentWorkout.getId();
        }
        if (!firstC && type == CARDIO && currentWorkout.getPrice() < minC){
            minC = currentWorkout.getPrice();
            idC = currentWorkout.getId();

        }
        //MIXED
        if(firstM && type == MIXED){
            firstM = false;
            maxM = currentWorkout.getPrice();
            idM = currentWorkout.getId();
        }
        if (!firstM && type == MIXED && currentWorkout.getPrice() > maxM){
            maxM = currentWorkout.getPrice();
            idM = currentWorkout.getId();
        }
    }
    if (!firstC)
        ordersId.push_back(idC);
    if (!firstM)
        ordersId.push_back(idM);
    if (!firstA)
        ordersId.push_back(idA);
    return ordersId;
}
std::string FullBodyCustomer::toString() {
    return " ";
}