package org.paumard.loom.travelpage.model;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.paumard.loom.travelpage.TravelPageExample;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public record Quotation(String agency, int quotation) implements PageComponent {

    public static class QuotationException extends RuntimeException {

    }

    private static class QuotationScope extends StructuredTaskScope<Quotation> {
        private final Collection<Quotation> quotations = new ConcurrentLinkedQueue<>();
        private final Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

        @Override
        protected void handleComplete(Future<Quotation> future) {
            switch (future.state()) {
                case RUNNING -> throw new IllegalStateException("Task is still running");
                case SUCCESS -> this.quotations.add(future.resultNow());
                case FAILED -> this.exceptions.add(future.exceptionNow());
                case CANCELLED -> {
                }
            }
        }

        @Override
        public QuotationScope joinUntil(Instant deadline) throws InterruptedException {

            try {
                super.joinUntil(deadline);
            } catch (TimeoutException e) {
                this.shutdown();
                throw new RuntimeException(e);
            }

            return this;
        }

        public QuotationException exceptions() {
            QuotationException exception = new QuotationException();
            exceptions.forEach(exception::addSuppressed);
            return exception;
        }

        public Quotation bestQuotation() {
            return quotations.stream()
                  .min(Comparator.comparing(Quotation::quotation))
                  .orElseThrow(this::exceptions);
        }
    }

    public static Quotation readQuotation() throws InterruptedException {

        Random random = new Random();

        try (var scope = new QuotationScope()) {

            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(30, 110), TravelPageExample.CHRONO_UNIT));
                return new Quotation("Agency A", random.nextInt(90, 130));
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(40, 120), TravelPageExample.CHRONO_UNIT));
                return new Quotation("Agency B", random.nextInt(90, 120));
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(20, 130), TravelPageExample.CHRONO_UNIT));
                return new Quotation("Agency C", random.nextInt(100, 110));
            });

            scope.joinUntil(Instant.now().plus(120, TravelPageExample.CHRONO_UNIT));

            return scope.bestQuotation();
        }
    }
}
