
package com.variamos.ng;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.protobuf.Message;
import com.variamos.ng.ModelPublisherGrpc.ModelPublisherBlockingStub;
import com.variamos.ng.ModelPublisherGrpc.ModelPublisherStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Sample client code that makes gRPC calls to the server.
 */
public class App {
	private static final Logger logger = Logger.getLogger(App.class.getName());

	private final ManagedChannel channel;
	private final ModelPublisherBlockingStub blockingStub;
	private final ModelPublisherStub asyncStub;
	private static Model model = Model.getDefaultInstance();

	public App(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
	}

	/**
	 * Construct client for accessing RouteGuide server using the existing channel.
	 */
	public App(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		blockingStub = ModelPublisherGrpc.newBlockingStub(channel);
		asyncStub = ModelPublisherGrpc.newStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public static void main(String args[]) {
		App app = new App("localhost", 50051);

		StreamObserver<Model> modelObserver = new StreamObserver<Model>() {

			@Override
			public void onNext(Model value) {

				System.out.println(String.format("new model value is %d ", value.getCounter()));
				model=value;

			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				throw new RuntimeException(t);

			}

			@Override
			public void onCompleted() {
				// noop

			}
		};
		app.asyncStub.listenModels(Empty.getDefaultInstance(), modelObserver);

		

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (true) {

					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");

					String input = "" + scanner.next();
					System.out.println(input);
					String foo = String.format("%d%s", model.getCounter(), input);
					try {
						model = Model.newBuilder(model).setCounter(Long.parseLong(engine.eval(foo).toString())).build();
						System.out.println(foo);
						app.blockingStub.pushModel(model);
					} catch (NumberFormatException | ScriptException e) {
						System.out.println("invalid model update discarded, try +1 or -5");
					}
					try {
						Thread.currentThread().sleep(10);
					} catch (InterruptedException e) {

					}

				}
				

			}
		});
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
