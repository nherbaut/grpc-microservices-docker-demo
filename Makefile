.DEFAULT_GOAL := all

doc:
	@echo generating doc 
	@sudo docker run --rm -v $(PWD)/doc:/out -v $(PWD)/protos:/protos   pseudomuto/protoc-gen-doc
	@sudo chown -R $(USER):$(USER) ./doc
	@python -m webbrowser file://$(PWD)/doc/index.html

clean:
	rm -rf doc
	rm -rf */generated

.DEFAULT all: clean python java js

python: python/generated_grpc_stubs 
	@echo python

java: java/generated_grpc_stubs 
	@echo java
js: js/generated_grpc_stubs
	@echo js

.SECONDEXPANSION:
%/generated_grpc_stubs: 
	@mkdir $*/generated
	@protoc -I=. --$*_out=$*/generated protos/demo.proto