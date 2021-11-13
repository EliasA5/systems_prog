#include "Studio.h"
#include <iostream>
#include <fstream>
#include <sstream>

Studio::Studio(): open(false){}

Studio::Studio(const std::string &configFilePath): open(false){
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
                for (int i; ss >> i;) {
                    trainersCapacity.push_back(i);
                    if (ss.peek() == ',')
                        ss.ignore();
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
    //TODO add loop parse inputs
//    for(const auto& trainer : trainers) {
//        std::cout << trainer->getCapacity() << "\n";
//    }
//    for(const auto& work : workout_options)
//        std::cout << "name:" << work.getName() << ", type: " << work.getType() << ", price:" << work.getPrice() << "\n";

    CloseAll().act(*this);
}

int Studio::getNumOfTrainers() const{
    return num_of_trainers;
}

Trainer* Studio::getTrainer(int tid){
    if(tid > getNumOfTrainers())
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
        delete actionsLog[i];
}
Studio::~Studio(){CloseAll().act(*this);}
//copy constructor
Studio::Studio(const Studio &stud): open(stud.open), num_of_trainers(stud.num_of_trainers){
    copy(stud.open, stud.num_of_trainers, stud.trainers, stud.workout_options, stud.actionsLog);
}
//move constructor
Studio::Studio(Studio&& stud): open(stud.open), num_of_trainers(stud.num_of_trainers){
    trainers = std::move(stud.trainers);
    actionsLog = std::move(stud.actionsLog);
}
//copy assignment
Studio& Studio::operator=(const Studio &stud){
    if(this != &stud){
        CloseAll().act(*this);
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
        CloseAll().act(*this);
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
void Studio::copy(bool _open, int _num_of_trainers, std::vector<Trainer *> _trainers,
                  std::vector<Workout> _workout_options, std::vector<BaseAction *> _actionsLog) {
    open = _open;
    num_of_trainers = _num_of_trainers;
    for(int i = 0; i<_num_of_trainers; i++){
        trainers[i] = new Trainer(*_trainers[i]);
    }
    workout_options = _workout_options;
    for(int i = 0; i<_actionsLog.size(); i++)
        actionsLog[i] = _actionsLog[i]->copy();
}