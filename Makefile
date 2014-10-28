CC=clang
CXX=clang++
CFLAGS+=-Wall -Wextra -pedantic -std=c11
CFLAGS+=-g -DDEBUG
CXXFLAGS+=-Wall -Wextra -pedantic -std=c++14 -Wno-long-long
CXXFLAGS+=-g -DDEBUG

LDFLAGS=-g -lcfitsio

OBJECTS=fits2csv.o
TARGET=fits2csv

all: $(TARGET)

$(TARGET) : $(OBJECTS)
	$(CXX) $^ $(LDFLAGS) -o $(TARGET)

fits2csv.o: fits2csv.cpp StringSlice.hpp type_mapping.inc

%.o: %.c
	$(CC) $(CFLAGS) -c $*.c
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $*.cpp

.PHONY: clean
clean:
	rm -f *.o $(TARGET)
