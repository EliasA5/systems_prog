CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/ConnectionHandler.o bin/BGSclient.o bin/userInput.o $(LDFLAGS)

BGSclient: bin/ConnectionHandler.o bin/BGSclient.o bin/userInput.o
	
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/userInput.o: src/userInput.cpp
	g++ $(CFLAGS) -o bin/userInput.o src/userInput.cpp

.PHONY: clean
clean:
	rm -f bin/*
