# GRPC Microservice demo

## prerequisites

* download an install grpc protoc tools from here: https://github.com/protocolbuffers/protobuf/releases/
* install docker


for example, on ubuntu 18.04 for release 3.6.1 installation can be done with:
```bash
sudo unzip protoc-3.6.1-linux-x86_64.zip -d /usr/local/
```

## usage

every operation is down through de make file

```
make python #create the python docker images
make java   #crete the java docker images
make clean  #erase the images and clean everything up
make run    #launch 7 containers as an example of interaction between microservices
```
