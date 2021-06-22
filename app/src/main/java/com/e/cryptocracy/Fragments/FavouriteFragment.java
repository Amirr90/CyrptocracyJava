package com.e.cryptocracy.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e.cryptocracy.CoinDetailActivity;
import com.e.cryptocracy.HomeScreen;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.CoinModal;
import com.e.cryptocracy.R;
import com.e.cryptocracy.interfaces.onLoadMoreInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.annotation.Nullable;

import co.blankkeys.animatedlinegraphview.AnimatedLineGraphView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@SuppressLint("ValidFragment")
public class FavouriteFragment extends Fragment {
    Context context;
    FavCoinAdapter adapter;
    List<CoinModal> coinFavList = new ArrayList<>();
    RecyclerView recyclerView;
    AVLoadingIndicatorView progress;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    FirebaseFirestore db;
    CollectionReference FavRef;
    int CURRENT_PAGE;
    private static final int MAX_LENGTH = 4;
    ProgressDialog dialog;

    onLoadMoreInterface onLoadMoreInterface;

    @SuppressLint("ValidFragment")
    public FavouriteFragment(Context context, onLoadMoreInterface onLoadMoreInterface) {
        this.context = context;
        this.onLoadMoreInterface = onLoadMoreInterface;
        adapter = new FavCoinAdapter(coinFavList, context);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        db = FirebaseFirestore.getInstance();
        FavRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Favourite");
        recyclerView = view.findViewById(R.id.fav_coin_recycler);
        progress = view.findViewById(R.id.fav_progress_avi);
        refreshLayout = view.findViewById(R.id.fav_swiperefresh);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Removing, please wait...");
        loadFavCoinData(dialog);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress.setVisibility(View.VISIBLE);
                recyclerView.removeAllViews();
                coinFavList.clear();
                recyclerView.removeAllViews();
                loadFavCoinData(1);
            }
        });
        return view;
    }

    private void loadFavCoinData(int currentPage) {
        CURRENT_PAGE = currentPage;
        FavRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                coinFavList.clear();
                                StringBuilder builder = new StringBuilder();
                                //get All fav Coins Ids using FireStore
                                QuerySnapshot snapshot = task.getResult();
                                for (int a = 0; a < task.getResult().size(); a++) {
                                    builder.append(snapshot.getDocuments().get(a).getId() + ",");
                                }

                                //get favCoin Using Gecko API
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://api.coingecko.com/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
                                Call<List<CoinModal>> call = uploadInterFace.getAllFavCoins(HomeScreen.CURRENCY, builder.toString(), HomeScreen.PER_PAGE, CURRENT_PAGE, HomeScreen.SORT_ORDER, true);

                                call.enqueue(new Callback<List<CoinModal>>() {
                                    @Override
                                    public void onResponse(Call<List<CoinModal>> call, Response<List<CoinModal>> response) {
                                        if (refreshLayout.isRefreshing()) {
                                            refreshLayout.setRefreshing(false);
                                        }
                                        progress.setVisibility(View.GONE);

                                        if (response.isSuccessful() && !response.body().isEmpty()) {
                                            coinFavList.addAll(response.body());
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            //Call retry layout here
                                            progress.setVisibility(View.GONE);
                                            Toast.makeText(context, "error " + response.errorBody(), Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<List<CoinModal>> call, Throwable t) {
                                        Toast.makeText(getActivity(), "ERROR " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        if (refreshLayout.isRefreshing()) {
                                            refreshLayout.setRefreshing(false);
                                        }
                                        progress.setVisibility(View.GONE);

                                    }
                                });
                            } else {
                                //show no fav coins layout
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                progress.setVisibility(View.GONE);
                                Toast.makeText(context, "No fav Coins", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void loadFavCoinData(final ProgressDialog dialog) {
        onLoadMoreInterface.onLoadMore(dialog);
        FavRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (queryDocumentSnapshots != null) {
                        coinFavList.clear();
                        StringBuilder builder = new StringBuilder();
                        //get All fav Coins Ids using FireStore
                        List<DocumentSnapshot> snapshot = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot1 : snapshot) {
                            builder.append(snapshot1.getId() + ",");
                        }

                        //get fav CoinPrice Using Gecko API
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://api.coingecko.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
                        Call<List<CoinModal>> call = uploadInterFace.getAllFavCoins(HomeScreen.CURRENCY, builder.toString(), HomeScreen.PER_PAGE, CURRENT_PAGE, HomeScreen.SORT_ORDER, true);

                        call.enqueue(new Callback<List<CoinModal>>() {
                            @Override
                            public void onResponse(Call<List<CoinModal>> call, Response<List<CoinModal>> response) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                progress.setVisibility(View.GONE);
                                if (response.isSuccessful() && !response.body().isEmpty()) {
                                    coinFavList.addAll(response.body());
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    if (progress != null)
                                        progress.setVisibility(View.GONE);
                                } else {
                                    //Call retry layout here
                                    if (progress != null)
                                        progress.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toast.makeText(context, "error " + response.errorBody(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<CoinModal>> call, Throwable t) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "failed to read ", Toast.LENGTH_SHORT).show();

                                coinFavList.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        //show no fav coins layout
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                        progress.setVisibility(View.GONE);
                        coinFavList.clear();
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    public class FavCoinAdapter extends RecyclerView.Adapter<FavCoinAdapter.MyViewHolder> {
        private final List<CoinModal> coinList;
        private final Context context;
        private final String TAG = "CoinAdapter";


        FirebaseFirestore db;
        CollectionReference favRef;

        public FavCoinAdapter(List<CoinModal> coinList, Context context) {
            this.coinList = coinList;
            this.context = context;
            db = FirebaseFirestore.getInstance();
            favRef = db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Favourite");
        }

        @NonNull
        @Override
        public FavCoinAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coin_view, viewGroup, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
            double Price_change_percentage_24h = coinList.get(position).getPrice_change_percentage_24h();
            double COIN_PRICE = coinList.get(position).getCurrent_price();
            double COIN_MCAP = coinList.get(position).getMarket_cap();

            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(9);
            format.setCurrency(Currency.getInstance(HomeScreen.CURRENCY));

            myViewHolder.favIcon.setImageResource(R.drawable.ic_star_filled);
            myViewHolder.title.setText(coinList.get(position).getName());
            myViewHolder.mCap.setText("MCap: " + format(COIN_MCAP));
            myViewHolder.coin_price.setText(format.format(COIN_PRICE));
            myViewHolder.rank.setText(coinList.get(position).getMarket_cap_rank() + ".");
            myViewHolder.symbol.setText(coinList.get(position).getSymbol());
            myViewHolder.price_chang_prcntage.setText(coinList.get(position).getPrice_change_percentage_24h() + "%");
            if (Price_change_percentage_24h > 0) {
                myViewHolder.sortIcon.setImageResource(R.mipmap.sort_up);
                myViewHolder.price_chang_prcntage.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                myViewHolder.sortIcon.setImageResource(R.mipmap.sort_down);
                myViewHolder.price_chang_prcntage.setTextColor(context.getResources().getColor(R.color.red));
            }
            String image_url = coinList.get(position).getImage();
            if (image_url != null) {
                Picasso.with(context).load(image_url).into(myViewHolder.coinIcon);
            }

            myViewHolder.layout.setOnClickListener(view -> {
                String coinId = coinList.get(position).getId();
                context.startActivity(new Intent(context, CoinDetailActivity.class)
                        .putExtra("coin_id", coinId)
                        .putExtra("coinName", coinList.get(position).getName())
                        .putExtra("coinSymbol", coinList.get(position).getSymbol())
                );
            });

            myViewHolder.favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                    String coinId = coinList.get(position).getId();
                    favRef.document(coinId)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            adapter.notifyItemRemoved(position);
                            adapter.notifyDataSetChanged();
                            loadFavCoinData(dialog);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    });

                }
            });


        }

        private String format(double value) {

            try {
                int power;
                String suffix = " KMBT";
                String formattedNumber = "";
                NumberFormat formatter = new DecimalFormat("#,###.#");
                power = (int) StrictMath.log10(value);
                value = value / (Math.pow(10, (power / 3) * 3));
                formattedNumber = formatter.format(value);
                formattedNumber = formattedNumber + suffix.charAt(power / 3);
                return formattedNumber.length() > MAX_LENGTH ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
            } catch (Exception e) {
                Log.d(TAG, "format error: " + e.getMessage());
                return "0";
            }
        }

        @Override
        public int getItemCount() {
            return coinList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView title;
            private final TextView rank;
            private final TextView symbol;
            private final TextView price_chang_prcntage;
            private final TextView coin_price;
            private final TextView mCap;
            ImageView coinIcon, sortIcon, favIcon;
            RelativeLayout layout;
            AnimatedLineGraphView graph;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.name);
                rank = itemView.findViewById(R.id.rank);
                symbol = itemView.findViewById(R.id.symbol);
                mCap = itemView.findViewById(R.id.mcap);
                coin_price = itemView.findViewById(R.id.coin_price);
                price_chang_prcntage = itemView.findViewById(R.id.change_percentage);
                coinIcon = itemView.findViewById(R.id.coin_image);
                sortIcon = itemView.findViewById(R.id.up_down_image);
                favIcon = itemView.findViewById(R.id.favourite_icon);
                layout = itemView.findViewById(R.id.main_lay);
                graph = itemView.findViewById(R.id.graph);

            }
        }
    }

}
