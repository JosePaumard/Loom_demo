package org.paumard.loom.travelpage;

import org.paumard.loom.travelpage.model.Quotation;
import org.paumard.loom.travelpage.model.TravelPage;
import org.paumard.loom.travelpage.model.Weather;

import java.util.concurrent.ExecutionException;

public class TravelPageExample {

    // --enable-preview --add-modules jdk.incubator.concurrent --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Weather weather = Weather.readWeather();
        Quotation quotation = Quotation.readQuotation();

        TravelPage travelPage = new TravelPage(quotation, weather);
        System.out.println("Travel page = " + travelPage);
    }
}
