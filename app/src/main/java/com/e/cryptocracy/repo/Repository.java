package com.e.cryptocracy.repo;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.e.cryptocracy.AppContext;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.models.TweetModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Repository {

    public MutableLiveData<List<TweetModel>> tweetList;

    RetrofitService api;

    @Inject
    public Repository(RetrofitService api) {
        this.api = api;
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
}
