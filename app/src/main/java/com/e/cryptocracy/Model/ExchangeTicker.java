package com.e.cryptocracy.Model;

public class ExchangeTicker {
    private String base;
    private String target;
    private float last;
    private float volume;
    private String trust_score;
    private String trade_url;

    public String getTrade_url() {
        return trade_url;
    }

    public String getTrust_score() {
        return trust_score;
    }

    public float getLast() {
        return last;
    }

    public float getVolume() {
        return volume;
    }

    public String getBase() {
        return base;
    }

    public String getTarget() {
        return target;
    }
}
