package com.e.cryptocracy.Model;

public class Exchange_Model {
    private String id;
    private String name;
    private float year_established;
    private String country;
    private String description;
    private String url;
    private String image;
    private boolean has_trading_incentive;
    private float trade_volume_24h_btc;
    private float trade_volume_24h_btc_normalized;
    private int trust_score_rank;
    private int trust_score;

    public int getTrust_score() {
        return trust_score;
    }

    public int getTrust_score_rank() {
        return trust_score_rank;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getYear_established() {
        return year_established;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public boolean has_trading_incentive() {
        return has_trading_incentive;
    }

    public float getTrade_volume_24h_btc() {
        return trade_volume_24h_btc;
    }

    public float getTrade_volume_24h_btc_normalized() {
        return trade_volume_24h_btc_normalized;
    }

}
