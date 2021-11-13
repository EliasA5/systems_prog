#ifndef STUDIO_H_
#define STUDIO_H_

#include <vector>
#include <string>
#include "Workout.h"
#include "Trainer.h"
#include "Action.h"


class Studio{		
public:
	Studio();
    Studio(const std::string &configFilePath);
    void start();
    int getNumOfTrainers() const;
    Trainer* getTrainer(int tid);
	const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    std::vector<Workout>& getWorkoutOptions();
    void deleteActionsLog();
    //deconstruct
    virtual ~Studio();
    //copy constructor
    Studio(const Studio &stud);
    //move constructor
    Studio(Studio&& studio);
    //copy assignment
    Studio& operator=(const Studio &stud);
    //move assignment
    Studio& operator=(Studio &stud);
    void copy(bool _open, int _num_of_trainers, std::vector<Trainer*> _trainers, std::vector<Workout> _workout_options, std::vector<BaseAction*> _actionsLog);

private:
    bool open;
    int num_of_trainers;
    std::vector<Trainer*> trainers;
    std::vector<Workout> workout_options;
    std::vector<BaseAction*> actionsLog;
};

#endif