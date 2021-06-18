package com.e.cryptocracy.Model;

public class CoinPrice {
    private String id;
    private String symbol;
    private String name;
    private String image;
    private float current_price;
    private float market_cap;
    private float market_cap_rank;
    private float total_volume;
    private double high_24h;
    private double low_24h;
    private float price_change_24h;
    private float price_change_percentage_24h;
    private float market_cap_change_24h;
    private float market_cap_change_percentage_24h;
    private float circulating_supply;
    private float total_supply;
    private double ath;
    private float ath_change_percentage;
    private String ath_date;
    private String last_updated;
    private float price_change_percentage_14d_in_currency;
    private float price_change_percentage_1y_in_currency;
    private float price_change_percentage_200d_in_currency;
    private float price_change_percentage_24h_in_currency;
    private float price_change_percentage_30d_in_currency;
    private float price_change_percentage_7d_in_currency;
    private long coingecko_rank;


    public long getCoingecko_rank() {
        return coingecko_rank;
    }

    public float getPrice_change_percentage_14d_in_currency() {
        return price_change_percentage_14d_in_currency;
    }

    public float getPrice_change_percentage_1y_in_currency() {
        return price_change_percentage_1y_in_currency;
    }

    public float getPrice_change_percentage_200d_in_currency() {
        return price_change_percentage_200d_in_currency;
    }

    public float getPrice_change_percentage_24h_in_currency() {
        return price_change_percentage_24h_in_currency;
    }

    public float getPrice_change_percentage_30d_in_currency() {
        return price_change_percentage_30d_in_currency;
    }

    public float getPrice_change_percentage_7d_in_currency() {
        return price_change_percentage_7d_in_currency;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public float getMarket_cap() {
        return market_cap;
    }

    public float getMarket_cap_rank() {
        return market_cap_rank;
    }

    public float getTotal_volume() {
        return total_volume;
    }

    public double getHigh_24h() {
        return high_24h;
    }

    public double getLow_24h() {
        return low_24h;
    }

    public float getPrice_change_24h() {
        return price_change_24h;
    }

    public float getPrice_change_percentage_24h() {
        return price_change_percentage_24h;
    }

    public float getMarket_cap_change_24h() {
        return market_cap_change_24h;
    }

    public float getMarket_cap_change_percentage_24h() {
        return market_cap_change_percentage_24h;
    }

    public float getCirculating_supply() {
        return circulating_supply;
    }

    public float getTotal_supply() {
        return total_supply;
    }

    public double getAth() {
        return ath;
    }

    public float getAth_change_percentage() {
        return ath_change_percentage;
    }

    public String getAth_date() {
        return ath_date;
    }


    public String getLast_updated() {
        return last_updated;
    }

}
