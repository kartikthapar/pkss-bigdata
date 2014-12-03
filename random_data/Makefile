CC=gcc
CXX=g++
CFLAGS+=-Wall -Wextra -pedantic -std=c11
CFLAGS+=-g -DDEBUG
CXXFLAGS+=-Wall -Wextra -pedantic -std=c++11 -Wno-long-long
CXXFLAGS+=-g -DDEBUG

LDFLAGS=-g 

OBJECTS=random_data.o
TARGET=random_data

all: $(TARGET)

$(TARGET) : $(OBJECTS)
	$(CXX) $^ $(LDFLAGS) -o $(TARGET)

random_data.o: random_data.cpp

%.o: %.c
	$(CC) $(CFLAGS) -c $*.c
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $*.cpp

.PHONY: clean
clean:
	rm -f *.o $(TARGET)
