from __future__ import print_function
import logging

import grpc

import demo_pb2
import demo_pb2_grpc
import time

import threading


class RpcClient:
    def __init__(self):
        self.channel = grpc.insecure_channel('localhost:50051')
        self.model = demo_pb2.Model()
        self.conn = demo_pb2_grpc.ModelPublisherStub(self.channel)
        threading.Thread(target=self.__listen_for_messages, daemon=True).start()

        try:
            while True:
                keyboard_input = input()
                try:
                    computation="%d%s" % (self.model.counter, keyboard_input)
                    print(computation)
                    self.model.counter = eval(computation)
                    self.conn.pushModel(self.model)
                except Exception as e:
                    raise e
                    print("invalid model update discarded, try +1 or -5")

        except KeyboardInterrupt as e:
            exit(0)

    def __listen_for_messages(self):
        for model in self.conn.listenModels(demo_pb2.Empty()):
            self.model = model
            print("new model value is %d" % self.model.counter)


if __name__ == '__main__':
    logging.basicConfig()
    RpcClient()
