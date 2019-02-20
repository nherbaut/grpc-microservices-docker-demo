#!/usr/bin/python3

from __future__ import print_function
import logging

import grpc

import demo_pb2
import demo_pb2_grpc
import time

import threading
import os


import os
import random
chan_spec="%s:%s"%(os.environ['SERVER'],os.environ['PORT'])
class RpcClient:
    def __init__(self):
        self.channel = grpc.insecure_channel(chan_spec)
        self.model = demo_pb2.Model()
        self.conn = demo_pb2_grpc.ModelPublisherStub(self.channel)
        threading.Thread(target=self.__listen_for_messages, daemon=True).start()
        rand = random.Random()

        while True:
            delta=rand.randint(-5,5)
            try:

                print("delta %d" %delta)
                self.model.counter = self.model.counter+delta
                self.conn.pushModel(self.model)
            except Exception as e:
                raise e
                print("invalid model update discarded, try +1 or -5")
            time.sleep(rand.randint(0,500)/1000.0)



    def __listen_for_messages(self):
        for model in self.conn.listenModels(demo_pb2.Empty()):
            self.model = model
            print("new model value is %d" % self.model.counter)


if __name__ == '__main__':
    logging.basicConfig()
    RpcClient()
