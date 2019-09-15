package br.com.emmanuelneri.producer;

import br.com.emmanuelneri.producer.verticle.OrderHttpServerVerticle;
import br.com.emmanuelneri.producer.verticle.OrderProducerVerticle;
import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new OrderHttpServerVerticle());
        vertx.deployVerticle(new OrderProducerVerticle());
    }

}
