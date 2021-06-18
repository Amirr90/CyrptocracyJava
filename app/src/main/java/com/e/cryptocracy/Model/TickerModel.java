package com.e.cryptocracy.Model;

public class TickerModel {

    private double last;
    private double volume;



    private String trade_url;
    public String base;
    private String target;

    private MarketModel market;

    public String getTrade_url() {
        return trade_url;
    }

    /*private Converted converted_volume;
    private Converted_lst converted_last;

    public Converted_lst getConverted_last() {
        return converted_last;
    }

    public Converted getConverted_volume() {
        return converted_volume;
    }*/

    public double getLast() {
        return last;
    }

    public double getVolume() {
        return volume;
    }

    public MarketModel getMarket() {
        return market;
    }

    public String getBase() {
        return base;
    }

    public String getTarget() {
        return target;
    }
}
