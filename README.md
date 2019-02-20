# GRPC Microservice demo

## prerequisites for dev

* download & install grpc protoc tools from here: https://github.com/protocolbuffers/protobuf/releases/
* install docker and make sure your user belongs to the docker group.

## Business

The business is very simple: once the server is started, whenever a client send a message to the server, the server broadcasts it to every other clients having an open channel.

The server owns a Model object sent to every client uppon update. Each client, periodically randomly udates the model by updating its fields , and send it to the server for broadcasting.


## usage

use the makefile to build, run and test the project

```
make python #create the python docker images
make doc # generate the protobuf doc
make java   #crete the java docker images
make clean  #erase the images and clean everything up
make run    #launch 7 containers as an example of interaction between microservices
```
