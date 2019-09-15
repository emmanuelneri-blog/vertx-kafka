package br.com.emmanuelneri.producer.verticle;

import br.com.emmanuelneri.producer.EventbusAddress;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class OrderHttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderHttpServerVerticle.class);
    private static final int PORT = 8080;
    private static final String ORDERS_PATH = "/orders";

    @Override
    public void start(final Promise<Void> startPromise) {
        final HttpServer httpServer = vertx.createHttpServer();

        final Handler<RoutingContext> failureHandler = this::routingFailureHandler;
        final Handler<RoutingContext> orderRoutingHandler = this::routingSuccessHandler;

        final Router router = Router.router(vertx);
        router.route("/orders/*").handler(BodyHandler.create()).failureHandler(failureHandler);
        router.post(ORDERS_PATH).handler(orderRoutingHandler).failureHandler(failureHandler);

        httpServer.requestHandler(router)
                .listen(PORT, handler -> {
                    if (handler.failed()) {
                        startPromise.fail(handler.cause());
                        return;
                    }
                    startPromise.complete();
                    LOGGER.info("HTTP Server is running.");
                });
    }

    private void routingSuccessHandler(final RoutingContext routingContext) {
        final String body = routingContext.getBodyAsString();
        LOGGER.info("order received: {0}", body);
        vertx.eventBus().send(EventbusAddress.ORDER_RECEVIED.name(), body);
    }

    private void routingFailureHandler(final RoutingContext routingContext) {
        LOGGER.error("Routing error", routingContext.failure());
        routingContext.response()
                .setStatusCode(routingContext.statusCode())
                .end();
    }

}
