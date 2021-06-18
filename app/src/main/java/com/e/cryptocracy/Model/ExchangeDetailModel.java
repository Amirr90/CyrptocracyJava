package com.e.cryptocracy.Model;

import java.util.List;

public class ExchangeDetailModel {
    List<ExchangeTicker> tickers;
    private int trust_score_rank;
    private int trust_score;
    private float trade_volume_24h_btc;
    private float trade_volume_24h_btc_normalized;
    private boolean centralized;
    private int year_established;
    private String other_url_1;
    private String other_url_2;
    private String facebook_url;
    private String reddit_url;
    private String url;
    private String country;
    private boolean has_trading_incentive;

    public String getCountry() {
        return country;
    }

    public boolean isHas_trading_incentive() {
        return has_trading_incentive;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public String getReddit_url() {
        return reddit_url;
    }

    public String getUrl() {
        return url;
    }

    public String getOther_url_1() {
        return other_url_1;
    }

    public String getOther_url_2() {
        return other_url_2;
    }

    public int getYear_established() {
        return year_established;
    }

    public List<ExchangeTicker> getTickers() {
        return tickers;
    }

    public boolean isCentralized() {
        return centralized;
    }

    public int getTrust_score_rank() {
        return trust_score_rank;
    }

    public int getTrust_score() {
        return trust_score;
    }

    public float getTrade_volume_24h_btc() {
        return trade_volume_24h_btc;
    }

    public float getTrade_volume_24h_btc_normalized() {
        return trade_volume_24h_btc_normalized;
    }
}
