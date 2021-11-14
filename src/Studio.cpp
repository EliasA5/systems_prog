#include "Studio.h"
#include <iostream>
#include <fstream>
#include <sstream>

Studio::Studio(): open(false){}

Studio::Studio(const std::string &configFilePath): open(false), num_of_trainers(0){
    std::ifstream configFile(configFilePath);
    std::string line;
    enum readType{numOfTrainers, actualTrainers, actualWorkouts};
    readType type = numOfTrainers;
    int workoutID = 0;
    while(getline(configFile, line)){
        //remove spaces
        //line.erase(std::remove_if(line.start(), line.end(), isspace), line.end());
        //check is the line is a comment or an empty line, if so skip
        if(line[0] == '#' || line.empty())
            continue;
        switch(type) {
            case numOfTrainers: {
                //numOfTrainers Parser
                num_of_trainers = stoi(line);
                trainers.reserve(num_of_trainers);
                type = actualTrainers;
                break;
            }
            case actualTrainers: {
                //Trainers Parser
                std::stringstream ss(line);
                std::vector<int> trainersCapacity;
                while(ss.good()){
                    std::string parsed;
                    getline(ss, parsed, ',');
                    trainersCapacity.push_back(stoi(parsed));
                }
                for (const auto &capacity: trainersCapacity) {
                    //TODO remove trainers in studio deconstuctor
                    trainers.push_back(new Trainer(capacity));
                }
                type = actualWorkouts;
                break;
            }
            case actualWorkouts: {
                //Workout Parser
                std::vector <std::string> vect;
                std::stringstream ss(line);
                bool first = true;
                while (ss.good()) {
                    std::string parsed;
                    getline(ss, parsed, ',');
                    if (first) {
                        vect.push_back(parsed);
                        first = false;
                    } else
                        vect.push_back(parsed.substr(1));
                }
                int workoutPrice = stoi(vect[2]);
                WorkoutType workType = vect[1] == "Anaerobic" ? ANAEROBIC : vect[1] == "Mixed" ? MIXED : CARDIO;
                workout_options.push_back(Workout(workoutID, vect[0], workoutPrice, workType));
                workoutID++;
                break;
            }
        }
    }
}

void Studio::start() {
    if(open)
        return;
    open = true;
    std::cout << "Studio is now open!\n";
//    for(const auto& trainer : trainers) {
//        std::cout << trainer->getCapacity() << "\n";
//    }
//    for(const auto& work : workout_options)
//        std::cout << "name:" << work.getName() << ", type: " << work.getType() << ", price:" << work.getPrice() << "\n";
    int customerId = 0;
    std::string line;
    //FIXME on windows \r gets added to end of line, check on linux
    while(getline(std::cin, line)){
        if(line[0] == '#' || line.empty())
            continue;
        std::vector<std::string> args;
        std::stringstream ss(line);
        while (ss.good()) {
            std::string parsed;
            getline(ss, parsed, ' ');
            args.push_back(parsed);
        }
        std::string command = args[0];

        if(command == "open"){
            int trainerId = std::stoi(args[1]);
            std::vector<Customer *> customersList;
            for(int i = 2; i<args.size(); i++){
                int delim = args[i].find(',');
                std::string name = args[i].substr(0, delim);
                std::string type_name = args[i].substr(delim+1);
                Customer* cus;
                if(type_name == "swt"){
                    cus = new SweatyCustomer(name, customerId);
                }
                else if(type_name == "chp"){
                    cus = new CheapCustomer(name, customerId);
                }
                else if(type_name == "mcl"){
                    cus = new HeavyMuscleCustomer(name, customerId);
                }
                else{
                    cus = new FullBodyCustomer(name, customerId);
                }
                customersList.push_back(cus);
                customerId++;
            }
            OpenTrainer *action = new OpenTrainer(trainerId, customersList);
            action->act(*this);
            if(action->getStatus() == ERROR)
                customerId -= customersList.size();
            actionsLog.push_back(action);
        }
        else if(command == "order"){
            int trainerId = std::stoi(args[1]);
            Order *action = new Order(trainerId);
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "move"){
            int trainerId = std::stoi(args[1]);
            int trainer_dst = std::stoi(args[2]);
            int cus_id = std::stoi(args[3]);
            MoveCustomer *action = new MoveCustomer(trainerId, trainer_dst, cus_id);
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "close"){
            int trainerId = std::stoi(args[1]);
            Close *action = new Close(trainerId);
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "closeall"){
            break;
        }
        else if(command == "workout_options"){
            PrintWorkoutOptions *action = new PrintWorkoutOptions();
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "status"){
            int trainerId = std::stoi(args[1]);
            PrintTrainerStatus *action = new PrintTrainerStatus(trainerId);
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "log"){
            PrintActionsLog *action = new PrintActionsLog();
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "backup"){
            BackupStudio *action = new BackupStudio();
            action->act(*this);
            actionsLog.push_back(action);
        }
        else if(command == "restore"){
            RestoreStudio *action = new RestoreStudio();
            action->act(*this);
            actionsLog.push_back(action);
        }
        else{
            std::cout << "not a valid command\n";
        }
    }
    CloseAll().act(*this);
    open = false;
}

int Studio::getNumOfTrainers() const{
    return num_of_trainers;
}

Trainer* Studio::getTrainer(int tid){
    if(tid > getNumOfTrainers() || tid < 0)
        return nullptr;
    return trainers[tid];
}
const std::vector<BaseAction*>& Studio::getActionsLog() const{
    return actionsLog;
}

std::vector<Workout>& Studio::getWorkoutOptions(){
    return workout_options;
}

void Studio::deleteActionsLog() {
    for(int i =0; i<actionsLog.size(); i++)
        if(actionsLog[i] != nullptr) {
            delete actionsLog[i];
            actionsLog[i] = nullptr;
        }
    actionsLog.clear();

}
Studio::~Studio(){
    deleteTrainers();
    deleteActionsLog();
    workout_options.clear();
}
//copy constructor
Studio::Studio(const Studio &stud): open(stud.open), num_of_trainers(stud.num_of_trainers){
    copy(stud.open, stud.num_of_trainers, stud.trainers, stud.workout_options, stud.actionsLog);
}
//move constructor
Studio::Studio(Studio&& stud): open(stud.open), num_of_trainers(stud.num_of_trainers){
    trainers = std::move(stud.trainers);
    workout_options = std::move(stud.workout_options);
    actionsLog = std::move(stud.actionsLog);

}
//copy assignment
Studio& Studio::operator=(const Studio &stud){
    if(this != &stud){
        deleteTrainers();
        deleteActionsLog();
        workout_options.clear();
        num_of_trainers = 0;
        open = false;
        copy(stud.open, stud.num_of_trainers, stud.trainers, stud.workout_options, stud.actionsLog);
    }
    return *this;
}
//move assignment
Studio& Studio::operator=(Studio &stud){
    if(this != &stud){
        deleteTrainers();
        deleteActionsLog();
        open = stud.open;
        num_of_trainers = stud.num_of_trainers;
        trainers = std::move(stud.trainers);
        workout_options = std::move(stud.workout_options);
        actionsLog = std::move(stud.actionsLog);
        stud.open = false;
        stud.num_of_trainers = 0;
    }
    return *this;
}
void Studio::copy(const bool &_open, const int &_num_of_trainers, const std::vector<Trainer *> &_trainers,
                  const std::vector<Workout> &_workout_options, const std::vector<BaseAction *> &_actionsLog) {
    open = _open;
    num_of_trainers = _num_of_trainers;
    for(int i = 0; i<_num_of_trainers; i++){
        trainers.push_back(new Trainer(*_trainers[i]));
    }
    for(int i = 0; i< _workout_options.size(); i++)
        workout_options.push_back(_workout_options[i]);
    for(int i = 0; i<_actionsLog.size(); i++)
        actionsLog.push_back(_actionsLog[i]->copy());
}

void Studio::deleteTrainers(){
    for(int i = 0; i<trainers.size(); i++)
        if(trainers[i] != nullptr){
            delete trainers[i];
            trainers[i] = nullptr;
        }
    trainers.clear();
}