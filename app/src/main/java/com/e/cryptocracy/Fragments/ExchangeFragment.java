package com.e.cryptocracy.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.cryptocracy.ExchangeActivity;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.Exchange_Model;
import com.e.cryptocracy.R;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ExchangeFragment extends Fragment {

    RecyclerView recyclerView;
    XchangeHolder adapter;
    List<Exchange_Model> models = new ArrayList<>();
    AVLoadingIndicatorView progressBar;
    int CURRENT_PAGE = 1;
    String PER_PAGE = "100";
    int ITEM_VIEWD, TOTAL_ITEM, CURRENT_ITEM;
    RecyclerView.LayoutManager layoutManager;
    Context context;
    SwipeRefreshLayout refreshLayout;

    NumberFormat format = NumberFormat.getCurrencyInstance();


    @SuppressLint("ValidFragment")
    public ExchangeFragment(Context context) {
        // Required empty public constructor

        this.context = context;
        adapter = new XchangeHolder( models, context );

        format.setMaximumFractionDigits( 0 );
        format.setCurrency( Currency.getInstance( "btc" ) );


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_exchabge, container, false );

        recyclerView = (RecyclerView) view.findViewById( R.id.exchange_recycler );
        refreshLayout = (SwipeRefreshLayout) view.findViewById( R.id.exchange_ref );
        progressBar = (AVLoadingIndicatorView) view.findViewById( R.id.progress_avi );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( context );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setAdapter( adapter );
        getXchangeData( CURRENT_PAGE );

        recyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    progressBar.setVisibility( View.GONE );
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                TOTAL_ITEM = layoutManager.getItemCount();
                CURRENT_ITEM = layoutManager.getChildCount();
                ITEM_VIEWD = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                if (TOTAL_ITEM == ITEM_VIEWD + CURRENT_ITEM) {
                    CURRENT_PAGE = CURRENT_PAGE + 1;
                    progressBar.setVisibility( View.VISIBLE );
                    getXchangeData( CURRENT_PAGE );
                }

            }
        } );

        refreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility( View.VISIBLE );
                recyclerView.removeAllViews();
                models.clear();
                recyclerView.removeAllViews();
                CURRENT_PAGE = 1;
                getXchangeData( CURRENT_PAGE );
            }
        } );

        return view;
    }

    private void getXchangeData(int current_page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();


        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<List<Exchange_Model>> call = uploadInterFace.getXchangeData( PER_PAGE, "" + current_page );
        call.enqueue( new Callback<List<Exchange_Model>>() {
            @Override
            public void onResponse(Call<List<Exchange_Model>> call, Response<List<Exchange_Model>> response) {
                progressBar.setVisibility( View.GONE );
                if (!response.isSuccessful()) {
                    Toast.makeText( context, "failed " + response.message(), Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (response.body() != null) {
                    models.addAll( response.body() );
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility( View.VISIBLE );
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing( false );
                    }
                }


            }

            @Override
            public void onFailure(Call<List<Exchange_Model>> call, Throwable t) {

                progressBar.setVisibility( View.GONE );
                getActivity().finish();
                Toast.makeText( getActivity(), "error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );


    }

    private class XchangeHolder extends RecyclerView.Adapter<XchangeHolder.MyViewHolder> {

        List<Exchange_Model> models;
        Context context;

        public XchangeHolder(List<Exchange_Model> models, Context context) {
            this.models = models;
            this.context = context;
        }

        @NonNull
        @Override
        public XchangeHolder.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.exchange_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull XchangeHolder.MyViewHolder myViewHolder, final int i) {

            try {
                String name = models.get( i ).getName();
                myViewHolder.XchangeName.setText( name );
                String imageUrl = models.get( i ).getImage();
                if (imageUrl != null && !imageUrl.equalsIgnoreCase( "" ))
                    Picasso.with( context ).load( imageUrl ).into( myViewHolder.ExchabgeImage );

                int trust_score_rank = models.get( i ).getTrust_score_rank();
                myViewHolder.ExchangeRank.setText( "" + trust_score_rank + ".  " );

                int trustScore = models.get( i ).getTrust_score();
                if (trustScore > 5) {
                    myViewHolder.ExchangeTrustImage.setImageDrawable( getResources().getDrawable( R.drawable.trust_badge_green ) );
                    myViewHolder.ExchangeTrustScore.setTextColor( getResources().getColor( R.color.green ) );
                } else {
                    myViewHolder.ExchangeTrustImage.setImageDrawable( getResources().getDrawable( R.drawable.trust_badge_red ) );
                    myViewHolder.ExchangeTrustScore.setTextColor( getResources().getColor( R.color.red ) );
                }
                myViewHolder.ExchangeTrustScore.setText( "" + trustScore );

                double trade_volume_24h_btc_normalized=models.get( i ).getTrade_volume_24h_btc_normalized();
                myViewHolder.XchangeVolumeNormalized.setText( format.format( trade_volume_24h_btc_normalized ) );

                double trade_volume_24h_btc=models.get( i ).getTrade_volume_24h_btc();
                myViewHolder.XchangeVolume24H.setText( format.format( trade_volume_24h_btc ) );

                myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String exchangeId=models.get(i).getId();
                        String exchangeImageUrl=models.get(i).getImage();
                        String exchangeName=models.get(i).getName();
                        double trade_volume_24h_btc_normalized=models.get( i ).getTrade_volume_24h_btc_normalized();

                        context.startActivity( new Intent( context, ExchangeActivity.class )
                        .putExtra( "exchangeId",exchangeId )
                        .putExtra( "exchangeImageUrl" ,exchangeImageUrl)
                                .putExtra( "trade_volume_24h_btc_normalized" ,trade_volume_24h_btc_normalized)
                        .putExtra( "exchangeName",exchangeName ));
                    }
                } );

            } catch (Exception e) {
            }
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView XchangeName, ExchangeRank, ExchangeTrustScore, XchangeVolumeNormalized, XchangeVolume24H;
            ImageView ExchabgeImage, ExchangeTrustImage;
            private RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                XchangeName = (TextView) itemView.findViewById( R.id.name );
                ExchabgeImage = (ImageView) itemView.findViewById( R.id.coin_image );
                ExchangeTrustImage = (ImageView) itemView.findViewById( R.id.up_down_image );
                ExchangeRank = (TextView) itemView.findViewById( R.id.rank );
                ExchangeTrustScore = (TextView) itemView.findViewById( R.id.symbol );
                XchangeVolumeNormalized = (TextView) itemView.findViewById( R.id.coin_price );
                XchangeVolume24H = (TextView) itemView.findViewById( R.id.mcap );
                layout=(RelativeLayout)itemView.findViewById( R.id.main_lay );
            }
        }
    }
}
