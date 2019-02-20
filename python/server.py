#!/usr/bin/python3

from concurrent import futures
import time
import logging
import grpc
import demo_pb2
import demo_pb2_grpc
import threading
import os
_ONE_DAY_IN_SECONDS = 60 * 60 * 24
port=os.environ['PORT']

class ModelPusher(demo_pb2_grpc.ModelPublisherServicer):
    @classmethod
    def last_wins(cls, models):
        return models[-1]


    def __init__(self, server):
        self.server = server
        self.model = demo_pb2.Model()
        self.model_updates=[]
        self.model_reconciliation=ModelPusher.last_wins

    def pushModel(self, model, context):
        self.model_updates.append(model)
        return demo_pb2.Empty()

    def listenModels(self,empty,context):
        model_counter=0
        if len(self.model_updates)==0:
            self.model_updates=[demo_pb2.Model()]
        while True:
            if len(self.model_updates)>model_counter:
                model_counter=len(self.model_updates)
                yield self.model_reconciliation(self.model_updates)



def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=100), )
    demo_pb2_grpc.add_ModelPublisherServicer_to_server(ModelPusher(server), server)
    server.add_insecure_port('[::]:%s'%port)
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    logging.basicConfig()
    serve()
