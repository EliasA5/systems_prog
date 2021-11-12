

CC = g++
CFLAGS = -g -Wall -Weffc++ -std=c++11
LFLAGS = -L/usr/lib

all: studio

# studio depends on:
studio: bin/main.o bin/Studio.o bin/Workout.o bin/Action.o bin/Customer.o bin/Trainer.o
	@echo 'building the studio'
	@echo 'linking'
	$(CC) -o bin/studio bin/main.o bin/Studio.o bin/Workout.o bin/Action.o bin/Customer.o bin/Trainer.o $(LFLAGS)
	@echo 'studio built'
	@echo ' '

bin/main.o: src/main.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/main.o src/main.cpp
bin/Studio.o: src/Studio.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Studio.o src/Studio.cpp

bin/Workout.o: src/Workout.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Workout.o src/Workout.cpp
	
bin/Action.o: src/Action.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Action.o src/Action.cpp
	
bin/Customer.o: src/Customer.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Customer.o src/Customer.cpp
	
bin/Trainer.o: src/Trainer.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Trainer.o src/Trainer.cpp
	
clean:
	rm -f bin/*
