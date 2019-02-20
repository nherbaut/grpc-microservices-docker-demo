
package com.variamos.ng;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
		final String host=System.getenv("SERVER");
		final int port = Integer.parseInt(System.getenv("PORT"));
		App app = new App(host, port);

		StreamObserver<Model> modelObserver = new StreamObserver<Model>() {

			@Override
			public void onNext(Model value) {

				System.out.println(String.format("new model value is %d ", value.getCounter()));
				model = value;

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
				Random rand = new Random();
				while (true) {

					int delta = rand.nextInt(10) - 5;
					model = Model.newBuilder(model).setCounter(model.getCounter() + delta).build();

					app.blockingStub.pushModel(model);
					System.out.println("delta " + delta);
					try {

						Thread.sleep(rand.nextInt(500));
					} catch (InterruptedException e) {
						// noop
					}
				}

			}
		});
		t.start();

		try

		{
			t.join();
		} catch (InterruptedException e) {
			// noop
		}
	}
}
