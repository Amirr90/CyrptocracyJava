package com.e.cryptocracy.Model;

import java.util.ArrayList;

public class GraphModel {

    //  ArrayList<ArrayList<Double>> prices = new ArrayList<>();
    Number[][] prices = new Number[][]{};
    ArrayList<ArrayList<Float>> market_caps = new ArrayList<>();
    ArrayList<ArrayList<Float>> total_volumes = new ArrayList<>();

    public Number[][] getPrices() {
        return prices;
    }

    public ArrayList<ArrayList<Float>> getMarket_caps() {
        return market_caps;
    }

    public ArrayList<ArrayList<Float>> getTotal_volumes() {
        return total_volumes;
    }
}
