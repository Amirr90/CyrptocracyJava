package com.e.cryptocracy.Model;

public class CoinExchange {

    String name;

    public String getName() {
        return name;
    }

    private TickerModel[] tickers;

    public TickerModel[] getTickers() {
        return tickers;
    }
}
