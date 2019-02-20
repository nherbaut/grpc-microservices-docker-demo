# GRPC Microservice demo

## prerequisites

* download an install grpc protoc tools from here: https://github.com/protocolbuffers/protobuf/releases/
* install docker


for example, on ubuntu 18.04 for release 3.6.1 installation can be done with:
```bash
sudo unzip protoc-3.6.1-linux-x86_64.zip -d /usr/local/
```

## Business

The business is very simple: once the server is started, whenever a client send a message to the server, the server broadcasts it to every other clients having an open channel.

The server owns a Model object sent to every client uppon update. Each client, periodically randomly udates the model by updating its fields , and send it to the server for broadcasting.


## usage

every operation is down through de make file

```
make python #create the python docker images
make java   #crete the java docker images
make clean  #erase the images and clean everything up
make run    #launch 7 containers as an example of interaction between microservices
```
