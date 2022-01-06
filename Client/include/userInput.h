//
// Created by spl211 on 04/01/2022.
//

#ifndef CLIENT_USERINPUT_H
#define CLIENT_USERINPUT_H

#include <ConnectionHandler.h>
#include <helperFunctions.h>
#include <iostream>
#include <string>
#include <fstream>
#include <stdlib.h>
enum command{
    REGISTER,
    LOGIN,
    LOGOUT,
    FOLLOW,
    POST,
    PM,
    LOGSTAT,
    STAT,
    BLOCK,
    NOCOMMAND
};

class userInput{
private:
    ConnectionHandler *handler;
    bool terminate;
public:
    userInput(ConnectionHandler* _handler, bool& _terminate);
    virtual ~userInput();
    void operator()();
};


#endif //CLIENT_USERINPUT_H
