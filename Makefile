.DEFAULT_GOAL := all

.PHONY prereq_python:
	@pip install grpcio-tools 


doc:
	@echo generating doc 
	@sudo docker run --rm -v $(PWD)/doc:/out -v $(PWD)/protos:/protos   pseudomuto/protoc-gen-doc
	@sudo chown -R $(USER):$(USER) ./doc
	@python -m webbrowser file://$(PWD)/doc/index.html

clean:
	rm -rf doc
	rm -rf */generated

.DEFAULT all: clean python

python: prereq_python python_grpc
	@echo python

python_grpc: 
	python -m grpc_tools.protoc -Iprotos --python_out=python --grpc_python_out=python protos/demo.proto