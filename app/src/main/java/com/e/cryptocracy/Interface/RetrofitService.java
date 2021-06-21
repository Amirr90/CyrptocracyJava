package com.e.cryptocracy.Interface;

import com.e.cryptocracy.Model.CoinExchange;
import com.e.cryptocracy.Model.CoinModal;
import com.e.cryptocracy.Model.CoinPrice;
import com.e.cryptocracy.Model.CurrencyData;
import com.e.cryptocracy.Model.ExchangeDetailModel;
import com.e.cryptocracy.Model.Exchange_Model;
import com.e.cryptocracy.Model.GraphModel;
import com.e.cryptocracy.Model.TradingModel;
import com.e.cryptocracy.models.TweetModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("api/v3/coins/markets")
    Call<List<CoinModal>> getAllCoins(@Query("vs_currency") String currency,
                                      @Query("per_page") Integer per_page,
                                      @Query("page") Integer page,
                                      @Query("order") String order,
                                      @Query("sparkline") boolean sparkline);


    @GET("api/v3/coins/markets")
    Call<List<CoinModal>> getAllFavCoins(@Query("vs_currency") String currency,
                                         @Query("ids") String favCoinIds,
                                         @Query("per_page") Integer per_page,
                                         @Query("page") Integer page,
                                         @Query("order") String order,
                                         @Query("sparkline") boolean sparkline);

    @GET("api/v3/coins/{id}/market_chart")
    Call<GraphModel> getGraphData(@Path("id") String id,
                                  @Query("vs_currency") String currency,
                                  @Query("days") String days);

    @GET("api/v3/coins/{coin_id}")
    Call<CoinPrice> getCoinDataById(@Path("coin_id") String id);


    @GET("api/v3/coins/markets")
    Call<List<CoinPrice>> getCoinPrice(@Query("vs_currency") String currency,
                                       @Query("ids") String id,
                                       @Query("price_change_percentage") String change);

    @GET("api/v3/coins/{id}/tickers")
    Call<CoinExchange> getExchangeByCoinId(@Path("id") String coinID);

    @GET("api/v3/exchanges/{id}")
    Call<TradingModel> getImageUrl(@Path("id") String tradingId);

    @GET("api/v3/coins/list")
    Call<List<CoinModal>> getSearchCoin();

    @GET("api/v3/exchanges")
    Call<List<Exchange_Model>> getXchangeData(@Query("per_page") String per_page,
                                              @Query("page") String page);


    @GET("api/v3/exchanges/{exchangeId}")
    Call<ExchangeDetailModel> getExchangeDetailById(@Path("exchangeId") String exchangeId);

    @GET("api/v3/exchange_rates")
    Call<CurrencyData> currencyData();

    @Headers({
            "x-rapidapi-key: 2d6de22e41msh529033f312cd392p10ec41jsnb1694424915d",
            "x-rapidapi-host: coinpaprika1.p.rapidapi.com"})
    @GET("coins/{coin_id_and_symbol}/twitter")
    Call<List<TweetModel>> getTweetData(@Path("coin_id_and_symbol") String coinIdAndSymbol);


}
