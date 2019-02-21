.DEFAULT_GOAL := all
.PHONY:  prereq_python doc clean all images java python python_grpc run
prereq_python:
	@pip install grpcio-tools 


doc:
	@echo generating doc 
	@sudo docker run --rm -v $(PWD)/doc:/out -v $(PWD)/protos:/protos   pseudomuto/protoc-gen-doc
	@sudo chown -R $(USER):$(USER) ./doc
	@python -m webbrowser file://$(PWD)/doc/index.html

clean: clean-doc clean-container clean-images

clean-doc:
	@rm -rf doc

clean-container:	
	@if [ "$$(sudo docker ps --filter 'label=variamos' -qa)" != "" ]; then \
	sudo docker rm -f $$(sudo docker ps --filter "label=variamos" -qa); \
	else : echo "nothing to do"; \
	fi
clean-images:
	@if [ "$$(sudo docker images --filter 'label=variamos' -qa)" != "" ]; then \
	sudo docker rmi -f $$(sudo docker images --filter "label=variamos" -qa); \
	else : echo "nothing to do"; \
	fi

	
	

.DEFAULT all: clean python java images run

images: 
	@sudo docker images |grep grpc

java:
	
	mvn -f java clean package
	sudo docker build -f java/Dockerfile -t nherbaut/grpc-demo-java-client --label "variamos"  ./java

python: prereq_python python_grpc
	sudo docker build -f python/Dockerfile.server -t nherbaut/grpc-demo-py-server --label "variamos" ./python
	sudo docker build -f python/Dockerfile.client -t nherbaut/grpc-demo-py-client --label "variamos" ./python

python_grpc: 
	python -m grpc_tools.protoc -Iprotos --python_out=python --grpc_python_out=python protos/demo.proto

run:
	docker-compose up --scale client-java=5 --scale client-python=5
