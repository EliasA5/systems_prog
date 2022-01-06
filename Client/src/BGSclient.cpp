//
// Created by spl211 on 06/01/2022.
//
#include <stdlib.h>
#include <thread>
#include <ConnectionHandler.h>
#include <userInput.h>
#include <helperFunctions.h>

int main(int argc, char *argv[]){
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    bool terminate = false;
    userInput sender(&connectionHandler, &terminate);
    std::thread th1(std::ref(sender));

    std::string msg;
    short opcode;

    while(!terminate){
        msg.clear();

        if(!connectionHandler.getFrameAscii(msg, ';')){
            connectionHandler.close();
            terminate = true;
            break;
        }
        const char* bytes = msg.c_str();
        opcode = bytesToShort(bytes);
        if(opcode == 9){ //NOTIFICATION
            std::stringstream ss;
            std::string notiType = msg.at(2) == '\0' ? " PM" : " Public";
            int first_zerobyte = msg.find('\0', 3);
            std::string username = msg.substr(3,  first_zerobyte-2);
            int second_zerobyte = msg.find('\0', 3+first_zerobyte+1);
            std::string content = msg.substr(first_zerobyte+1, second_zerobyte - first_zerobyte);
            ss << "NOTIFICATION" << notiType << " " << username << " " << content;
            std::cout << ss.str() << std::endl;
        }
        else if(opcode == 10){ //ACK
            short msgOpcode = bytesToShort(bytes+2);
            std::stringstream ss;
            ss << "ACK " << msgOpcode << ' ';
            if(msgOpcode == 3){ //LOGOUT
                terminate = true;
                connectionHandler.close();
                ss << std::endl;
                std::cout << ss.str();
                break;
            }
            else if(msgOpcode == 4){ //FOLLOW
                std::string notiType = msg.at(4) == 0 ? "FOLLOW" : "UNFOLLOW";
                int next_zerobyte = msg.find('\0', 5);
                std::string username = msg.substr(5, next_zerobyte);
                ss << notiType << ' ' << username << std::endl;
                std::cout << ss.str();
            }
            else if(msgOpcode == 7 || msgOpcode == 8){ //LOGSTAT or stat
                ss << bytesToShort(bytes+4) << ' ' << bytesToShort(bytes+6) << ' ' << bytesToShort(bytes+8) << ' ' << bytesToShort(bytes+10) << std::endl;
                std::cout << ss.str();
            }
            else{
                ss << std::endl;
                std::cout << ss.str();
            }
        }
        else if(opcode == 11){ //ERROR
            short msgOpcode = bytesToShort(bytes+2);
            std::stringstream ss;
            ss << "ERROR " << msgOpcode << std::endl;
            std::cout << ss.str();
        }
        else{
            std::cout << "Unrecognized reply\n";
        }

    }

    th1.detach();
}
