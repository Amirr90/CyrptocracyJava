package com.e.cryptocracy.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e.cryptocracy.Adapter.CoinAdapter;
import com.e.cryptocracy.HomeScreen;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.CoinModal;
import com.e.cryptocracy.R;
import com.e.cryptocracy.interfaces.onLoadMoreInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("ValidFragment")
public class CoinFragment extends Fragment {
    private static final String TAG = "CoinFragment";

    Context context;
    CoinAdapter adapter;
    List<CoinModal> coinModalList = new ArrayList<>();
    RecyclerView recyclerView;
    AVLoadingIndicatorView progress;
    int ITEM_VIEWED, TOTAL_ITEM, CURRENT_ITEM, CURRENT_PAGE;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;

    onLoadMoreInterface onLoadMoreInterface;

    @SuppressLint("ValidFragment")
    public CoinFragment(Context context, onLoadMoreInterface onLoadMoreInterface) {
        this.onLoadMoreInterface = onLoadMoreInterface;
        this.context = context;
        final Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        HomeScreen.titleText.startAnimation(aniFade);

        adapter = new CoinAdapter(coinModalList, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coin, container, false);
        recyclerView = view.findViewById(R.id.coin_recycler);
        progress = view.findViewById(R.id.progress_avi);
        refreshLayout = view.findViewById(R.id.swiperefresh);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        loadCoinData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    progress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                TOTAL_ITEM = layoutManager.getItemCount();
                CURRENT_ITEM = layoutManager.getChildCount();
                ITEM_VIEWED = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                if (TOTAL_ITEM == ITEM_VIEWED + CURRENT_ITEM) {
                    CURRENT_PAGE = CURRENT_PAGE + 1;
                    progress.setVisibility(View.VISIBLE);
                    loadCoinData(CURRENT_PAGE);

                }

            }
        });

        refreshLayout.setOnRefreshListener(() -> {
            progress.setVisibility(View.VISIBLE);
            recyclerView.removeAllViews();
            coinModalList.clear();
            recyclerView.removeAllViews();
            loadCoinData(1);
        });

        return view;
    }


    public OkHttpClient.Builder provideHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        httpClient.addInterceptor(logging);
        httpClient.dispatcher(dispatcher);
        return httpClient;
    }


    private void loadCoinData(int currentPage) {
        onLoadMoreInterface.onLoadMore(currentPage);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideHttpClient().build())
                .build();
        RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
        Call<List<CoinModal>> call = uploadInterFace.getAllCoins(HomeScreen.CURRENCY, HomeScreen.PER_PAGE, currentPage, HomeScreen.SORT_ORDER, true);
        call.enqueue(new Callback<List<CoinModal>>() {
            @Override
            public void onResponse(@NotNull Call<List<CoinModal>> call, @NotNull Response<List<CoinModal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    coinModalList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    //Call retry layout here
                    Log.d(TAG, "onResponse: error code " + response.code());
                    Toast.makeText(context, "error " + response.code() + " msg: " + response.message(), Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
                if (refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(@NotNull Call<List<CoinModal>> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadCoinData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e == null) {
                                if (documentSnapshot.exists()) {
                                    CURRENT_PAGE = 1;
                                    HomeScreen.showLoadingLayout(false);
                                    coinModalList.clear();
                                    String CURRENCY = documentSnapshot.getString("currency");
                                    if (CURRENCY != null)
                                        HomeScreen.mChangeCurrencyText.setText(CURRENCY.toUpperCase());
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("https://api.coingecko.com/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
                                    Call<List<CoinModal>> call = uploadInterFace.getAllCoins(CURRENCY, HomeScreen.PER_PAGE, CURRENT_PAGE, HomeScreen.SORT_ORDER, true);
                                    call.enqueue(new Callback<List<CoinModal>>() {
                                        @Override
                                        public void onResponse(Call<List<CoinModal>> call, Response<List<CoinModal>> response) {
                                            if (response.isSuccessful() && !response.body().isEmpty()) {
                                                coinModalList.addAll(response.body());
                                                adapter.notifyDataSetChanged();
                                                HomeScreen.showLoadingLayout(true);
                                            } else {
                                                //Call retry layout here
                                                HomeScreen.showLoadingLayout(true);
                                                Toast.makeText(context, "error " + response.errorBody(), Toast.LENGTH_SHORT).show();
                                            }


                                        }

                                        @Override
                                        public void onFailure(Call<List<CoinModal>> call, Throwable t) {

                                        }
                                    });
                                } else {
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("https://api.coingecko.com/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
                                    Call<List<CoinModal>> call = uploadInterFace.getAllCoins(HomeScreen.CURRENCY, HomeScreen.PER_PAGE, CURRENT_PAGE, HomeScreen.SORT_ORDER, true);
                                    call.enqueue(new Callback<List<CoinModal>>() {
                                        @Override
                                        public void onResponse(Call<List<CoinModal>> call, Response<List<CoinModal>> response) {
                                            if (response.isSuccessful() && !response.body().isEmpty()) {
                                                coinModalList.addAll(response.body());
                                                adapter.notifyDataSetChanged();
                                                HomeScreen.showLoadingLayout(true);
                                            } else {
                                                //Call retry layout here
                                                HomeScreen.showLoadingLayout(true);
                                                Toast.makeText(context, "error " + response.errorBody(), Toast.LENGTH_SHORT).show();
                                            }


                                        }

                                        @Override
                                        public void onFailure(Call<List<CoinModal>> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        }
                    });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        CURRENT_PAGE = 1;
    }
}
