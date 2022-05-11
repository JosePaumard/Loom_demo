package org.paumard.loom.travelpage.model;

public record Weather(String agency, String weather) {

    public static Weather readWeather() throws InterruptedException {
        return new Weather("WA", "Sunny");
    }
}
