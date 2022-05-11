package org.paumard.loom.travelpage;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.paumard.loom.travelpage.model.PageComponent;
import org.paumard.loom.travelpage.model.Quotation;
import org.paumard.loom.travelpage.model.TravelPage;
import org.paumard.loom.travelpage.model.Weather;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TravelPageExample {

    public static final ChronoUnit CHRONO_UNIT = ChronoUnit.SECONDS;

    private static class TravelPageScope extends StructuredTaskScope<PageComponent> {

        private volatile Quotation quotation;
        private volatile Weather weather = Weather.UNKNOWN;
        private volatile Quotation.QuotationException exception;

        @Override
        protected void handleComplete(Future<PageComponent> future) {
            switch (future.state()) {
                case RUNNING -> throw new IllegalStateException("Task is still running");
                case SUCCESS -> {
                    switch (future.resultNow()) {
                        case Quotation quotation -> this.quotation = quotation;
                        case Weather weather -> this.weather = weather;
                    }
                }
                case FAILED -> {
                    switch (future.exceptionNow()) {
                        case Quotation.QuotationException e -> this.exception = e;
                        default -> throw new RuntimeException(future.exceptionNow());
                    }
                }
                case CANCELLED -> {
                }
            }
        }

        public TravelPage travelPage() {
            if (this.quotation != null) {
                return new TravelPage(this.quotation, this.weather);
            } else {
                throw exception;
            }
        }
    }

    // --enable-preview --add-modules jdk.incubator.concurrent --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        var pid = ProcessHandle.current().pid();
        System.out.println("pid = " + pid);

        try (var scope = new TravelPageScope()) {

            scope.fork(Weather::readWeather);
            scope.fork(Quotation::readQuotation);

            scope.join();

            TravelPage travelPage = scope.travelPage();
            System.out.println("Travel page = " + travelPage);
        }
    }
}
