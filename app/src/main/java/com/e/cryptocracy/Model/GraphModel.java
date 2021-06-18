package com.e.cryptocracy.Model;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {

    ArrayList<ArrayList<Float>> prices = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Float>> market_caps = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Float>> total_volumes = new ArrayList<ArrayList<Float>>();

    public ArrayList<ArrayList<Float>> getPrices() {
        return prices;
    }

    public ArrayList<ArrayList<Float>> getMarket_caps() {
        return market_caps;
    }

    public ArrayList<ArrayList<Float>> getTotal_volumes() {
        return total_volumes;
    }
}
