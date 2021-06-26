package com.e.cryptocracy;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.e.cryptocracy.Model.GraphModel;
import com.e.cryptocracy.models.TweetModel;
import com.e.cryptocracy.repo.Repository;
import com.google.gson.internal.JsonReaderInternalAccess;

import java.util.List;

import javax.inject.Inject;

public class AppViewModel extends ViewModel {
    private static final String TAG = "AppViewModel";

    @Inject
    Repository apiRepository;

    @Inject
    public AppViewModel(Repository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public LiveData<List<TweetModel>> tweetList(String coinIdAndSymbol) {
        Log.d(TAG, "tweetList: "+ coinIdAndSymbol);
        return apiRepository.getTweetList(coinIdAndSymbol);
    }

    public LiveData<GraphModel> getGraphData(String days, String coin_id) {
        return apiRepository.getGraphData(days,coin_id);
    }
}
