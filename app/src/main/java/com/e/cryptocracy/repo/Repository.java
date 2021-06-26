package com.e.cryptocracy.repo;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.e.cryptocracy.AppContext;
import com.e.cryptocracy.HomeScreen;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.GraphModel;
import com.e.cryptocracy.models.TweetModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Repository {
    private static final String TAG = "Repository";
    public MutableLiveData<List<TweetModel>> tweetList;
    public MutableLiveData<GraphModel> graphMutableLiveData;

    RetrofitService api;
    OkHttpClient.Builder builder;

    @Inject
    public Repository(RetrofitService api, OkHttpClient.Builder builder) {
        this.api = api;
        this.builder = builder;
    }

    public LiveData<List<TweetModel>> getTweetList(String coinIdAndSymbol) {
        if (null == tweetList)
            tweetList = new MutableLiveData<>();
        initTweetData(coinIdAndSymbol);
        return tweetList;
    }

    private void initTweetData(String coinIdAndSymbol) {
        api.getTweetData(coinIdAndSymbol).enqueue(new Callback<List<TweetModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<TweetModel>> call, @NotNull Response<List<TweetModel>> response) {
                if (response.code() == 200 && response.body() != null) {
                    tweetList.setValue(response.body());
                }


            }

            @Override
            public void onFailure(@NotNull Call<List<TweetModel>> call, @NotNull Throwable t) {
                Toast.makeText(AppContext.context, "Something went wrong  !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<GraphModel> getGraphData(String days, String coin_id) {
        if (null == graphMutableLiveData)
            graphMutableLiveData = new MutableLiveData<>();
        initGraphData(days, coin_id);
        return graphMutableLiveData;
    }

    private void initGraphData(String days, String coin_id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
        RetrofitService GraphData = retrofit.create(RetrofitService.class);
        Call<GraphModel> call = GraphData.getGraphData(coin_id, HomeScreen.CURRENCY, days);
        call.enqueue(new Callback<GraphModel>() {
            @Override
            public void onResponse(@NotNull Call<GraphModel> call, @NotNull Response<GraphModel> response) {
                if (response.code() == 200 && response.body() != null) {
                    graphMutableLiveData.setValue(response.body());
                }
            }


            @Override
            public void onFailure(@NotNull Call<GraphModel> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(AppContext.context, "Something went wrong  !!", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
