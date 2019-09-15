package br.com.emmanuelneri.consumer;

import br.com.emmanuelneri.consumer.verticle.OrderConsumerVerticle;
import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new OrderConsumerVerticle());
    }

}
