HADOOP_PREFIX=/home/paul/Downloads/hadoop-2.5.1
CC=clang
CXX=clang++
CFLAGS+=-Wall -Wextra -pedantic -std=c11 -I ${HADOOP_PREFIX}/include/
CFLAGS+=-g -DDEBUG
CXXFLAGS+=-Wall -Wextra -pedantic -std=c++14 -Wno-long-long -I ${HADOOP_PREFIX}/include/
CXXFLAGS+=-g -DDEBUG

LDFLAGS=-g -lcfitsio -lboost_program_options 
#		-L${HADOOP_PREFIX}/lib/native -lhdfs \
#		-L/usr/lib/jvm/default/jre/lib/amd64/server \
#		-L/usr/lib/jvm/default/jre/lib/amd64 \
#		-lverify \
#		-ljvm \
#		-ljava \


OBJECTS=fits2csv.o
TARGET=fits2csv

all: $(TARGET)

$(TARGET) : $(OBJECTS)
	$(CXX) $^ $(LDFLAGS) -o $(TARGET)

fits2csv.o: fits2csv.cpp type_mapping.inc

%.o: %.c
	$(CC) $(CFLAGS) -c $*.c
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $*.cpp

.PHONY: clean
clean:
	rm -f *.o $(TARGET)
