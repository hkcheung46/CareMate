package com.msccs.hku.familycaregiver.tempStructure;

/**
 * Created by HoiKit on 18/01/2018.
 */

public class HomeDataReading {

    private String humidity;
    private long logDate;
    private String temperature;

    public HomeDataReading() {
    }

    public HomeDataReading(String humidity, long logDate, String temperature) {
        this.humidity = humidity;
        this.logDate = logDate;
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public long getLogDate() {
        return logDate;
    }

    public void setLogDate(long logDate) {
        this.logDate = logDate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
