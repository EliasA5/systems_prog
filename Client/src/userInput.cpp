//
// Created by spl211 on 04/01/2022.
//

#include "../include/userInput.h"


command getCommandFromString(std::string const &input){
    if(input == "REGISTER") return REGISTER;
    if(input == "LOGIN") return LOGIN;
    if(input == "LOGOUT") return LOGOUT;
    if(input == "FOLLOW") return FOLLOW;
    if(input == "POST") return POST;
    if(input == "PM") return PM;
    if(input == "LOGSTAT") return LOGSTAT;
    if(input == "STAT") return STAT;
    if(input == "BLOCK") return BLOCK;
    return NOCOMMAND;
}
userInput::userInput(ConnectionHandler* _handler, bool& _terminate): handler(_handler), terminate(_terminate){}
userInput::~userInput(){}

void userInput::operator()() {
    const short bufsize = 1024;
    char buf[bufsize];
    while(!terminate){
        std::cin.getline(buf, bufsize);
        std::stringstream ss(buf);

        std::string command;
        if(ss.good()){
            getline(ss, command, ' ');
        }
        switch(getCommandFromString(command)){
            case REGISTER: {
                std::string username;
                std::string password;
                std::string birthday;
                bool error = false;
                if(ss.good())
                    getline(ss, username, ' ');
                else error = true;
                if(ss.good())
                    getline(ss, password, ' ');
                else error = true;
                if(ss.good())
                    getline(ss, birthday, ' ');
                else error = true;
                if(error) {
                    std::cout << "Usage: REGISTER username password birthday" << std::endl;
                    break;
                }
                int numBytesToSend = 2+username.length()+1+password.length()+1+birthday.length()+2;
                char bytesToSend[numBytesToSend];
                memset(bytesToSend, 0, numBytesToSend);

                shortToBytes(1, bytesToSend);
                strcpy(bytesToSend+2, username.c_str());
                strcpy(bytesToSend+2+username.length()+1, password.c_str());
                strcpy(bytesToSend+2+username.length()+1+password.length()+1, birthday.c_str());
                bytesToSend[numBytesToSend-2] = '\0';
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case LOGIN: {
                std::string username;
                std::string password;
                std::string captcha;
                bool error = false;
                if(ss.good())
                    getline(ss, username, ' ');
                else error = true;
                if(ss.good())
                    getline(ss, password, ' ');
                else error = true;
                if(ss.good())
                    getline(ss, captcha, ' ');
                else error = true;
                if(error) {
                    std::cout << "Usage: LOGIN username password captcha" << std::endl;
                    break;
                }
                int numBytesToSend = 2+username.length()+1+password.length()+1+1+1;
                char bytesToSend[numBytesToSend];
                memset(bytesToSend, 0, numBytesToSend);

                shortToBytes(2, bytesToSend);
                strcpy(bytesToSend+2, username.c_str());
                strcpy(bytesToSend+2+username.length()+1, password.c_str());
                if(captcha == "1")
                    bytesToSend[2+username.length()+1+password.length()+1] = 1;
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case LOGOUT:{
                int numBytesToSend = 3;
                char bytesToSend[numBytesToSend];
                memset(bytesToSend, 0, numBytesToSend);
                shortToBytes(3, bytesToSend);
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case FOLLOW:{
                std::string username;
                std::string follow;
                bool error = false;
                if(ss.good()) {
                    getline(ss, follow, ' ');
                    if(follow.length() != 1 || follow.at(0) != '0' || follow.at(0) != '1')
                        error = true;
                }
                else error = true;
                if(ss.good())
                    getline(ss, username, ' ');
                else error = true;
                if(error){
                    std::cout << "Usage: FOLLOW <0/1 (follow/unfollow)> username" << std::endl;
                    break;
                }
                char followByte = follow.at(0) == '0' ? 0 : 1;
                int numBytesToSend = 2+1+username.length()+1;
                char bytesToSend[numBytesToSend];
                shortToBytes(4, bytesToSend);
                bytesToSend[3] = followByte;
                strcpy(bytesToSend+3, username.c_str());
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case POST:{
                std::string content;
                bool error = false;
                if(ss.good())
                    getline(ss, content);
                else error = true;
                if(error){
                    std::cout << "Usage: POST <content>" << std::endl;
                    break;
                }
                int numBytesToSend = 2+content.length()+2;
                char bytesToSend[numBytesToSend];
                shortToBytes(5, bytesToSend);
                strcpy(bytesToSend+2, content.c_str());
                bytesToSend[numBytesToSend-2] = '\0';
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case PM:{
                std::string username;
                std::string content;
                bool error = false;
                if(ss.good())
                    getline(ss, username, ' ');
                else error = true;
                if(ss.good()) {
                    getline(ss, content);
                    if(content.length() == 0)
                        error = true;
                }
                else error = true;
                if(error){
                    std::cout << "Usage: PM <user> <content>" << std::endl;
                    break;
                }
                std::string dateString = getDateAsString();
                int numBytesToSend = 2+4+username.length()+content.length()+dateString.length();
                char bytesToSend[numBytesToSend];
                shortToBytes(6, bytesToSend);
                strcpy(bytesToSend+2, username.c_str());
                strcpy(bytesToSend+2+username.length()+1, content.c_str());
                strcpy(bytesToSend+2+username.length()+1+content.length()+1, dateString.c_str());
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case LOGSTAT:{
                int numBytesToSend = 3;
                char bytesToSend[numBytesToSend];
                memset(bytesToSend, 0, numBytesToSend);
                shortToBytes(7, bytesToSend);
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case STAT:{
                std::string username;
                std::vector<char> bytesToSendVector;
                char opcode[2];
                shortToBytes(8, opcode);
                bytesToSendVector.push_back(opcode[0]);
                bytesToSendVector.push_back(opcode[1]);
                if(!ss.good()){
                    std::cout << "Usage: STAT <user>|<user>...|<user>" << std::endl;
                    break;
                }
                while(ss.good()) {
                    getline(ss, username, '|');
                    for(char &a : username)
                        bytesToSendVector.push_back(a);
                    bytesToSendVector.push_back('\0');
                }
                bytesToSendVector.push_back(';');
                if(!handler->sendBytes(bytesToSendVector.data(), bytesToSendVector.size()))
                    terminate = true;
                break;
            }
            case BLOCK:{
                std::string username;
                bool error = false;
                if(ss.good())
                    getline(ss, username);
                else error = true;
                if(error){
                    std::cout << "Usage: BLOCK <user>" << std::endl;
                    break;
                }
                int numBytesToSend = 2+username.length()+2;
                char bytesToSend[numBytesToSend];
                shortToBytes(12, bytesToSend);
                strcpy(bytesToSend+2, username.c_str());
                bytesToSend[numBytesToSend-2] = '\0';
                bytesToSend[numBytesToSend-1] = ';';
                if(!handler->sendBytes(bytesToSend, numBytesToSend))
                    terminate = true;
                break;
            }
            case NOCOMMAND:{
                std::cout<< "Not a valid command" << std::endl;
                break;
            }
        }

    }
}