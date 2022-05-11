package org.paumard.loom.travelpage.model;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.paumard.loom.travelpage.TravelPageExample;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public record Weather(String agency, String weather) implements PageComponent {

    public static final Weather UNKNOWN = new Weather("", "Mostly Sunny");

    private static class WeatherScope implements AutoCloseable {

        private StructuredTaskScope.ShutdownOnSuccess<Weather> scope =
              new StructuredTaskScope.ShutdownOnSuccess<>();
        private boolean timeout = false;

        public WeatherScope joinUntil(Instant deadline) throws InterruptedException {
            try {
                scope.joinUntil(deadline);
            } catch (TimeoutException e) {
                scope.shutdown();
                this.timeout = true;
            }
            return this;
        }

        public Future<Weather> fork(Callable<? extends Weather> task) {
            return scope.fork(task);
        }

        @Override
        public void close() {
            scope.close();
        }

        public Weather getWeather() throws ExecutionException {
            if (!timeout) {
                return this.scope.result();
            } else {
                return Weather.UNKNOWN;
            }
        }
    }

    public static Weather readWeather() throws InterruptedException, ExecutionException {

        Random random = new Random();

        try (var scope = new WeatherScope()) {

            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(30, 110), TravelPageExample.CHRONO_UNIT));
                return new Weather("WA", "Sunny");
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(20, 90), TravelPageExample.CHRONO_UNIT));
                return new Weather("WB", "Sunny");
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(10, 120), TravelPageExample.CHRONO_UNIT));
                return new Weather("WC", "Sunny");
            });

            scope.joinUntil(Instant.now().plus(100, TravelPageExample.CHRONO_UNIT));

            return scope.getWeather();
        }
    }
}
