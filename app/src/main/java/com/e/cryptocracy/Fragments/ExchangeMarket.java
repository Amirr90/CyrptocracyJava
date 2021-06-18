package com.e.cryptocracy.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.e.cryptocracy.Model.ExchangeDetailModel;
import com.e.cryptocracy.Model.ExchangeTicker;
import com.e.cryptocracy.R;
import com.e.cryptocracy.WebViewActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
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
public class ExchangeMarket extends Fragment {

    List<ExchangeTicker> tickers = new ArrayList<>();
    RecyclerView recyclerView;
    AVLoadingIndicatorView progress;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    ExchangeMarketAdapter adapter;
    String EXCHANGE_ID;
    int ITEM_VIEWED, TOTAL_ITEM, CURRENT_ITEM;
    public boolean isShowing = false;


    Context context;

    @SuppressLint("ValidFragment")
    public ExchangeMarket(Context context, String EXCHANGE_ID) {
        this.context = context;
        this.EXCHANGE_ID = EXCHANGE_ID;
        adapter = new ExchangeMarketAdapter( tickers, context );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_exchange_market, container, false );


        recyclerView = (RecyclerView) view.findViewById( R.id.ex_market_recycler );
        progress = (AVLoadingIndicatorView) view.findViewById( R.id.ex_progress_avi );
        refreshLayout = (SwipeRefreshLayout) view.findViewById( R.id.ex_mar_swiperefresh );
        layoutManager = new LinearLayoutManager( context );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setAdapter( adapter );
        loadData();

        recyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    progress.setVisibility( View.GONE );
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                TOTAL_ITEM = layoutManager.getItemCount() / 2;
                CURRENT_ITEM = layoutManager.getChildCount();
                ITEM_VIEWED = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                /*if (TOTAL_ITEM == ITEM_VIEWED + CURRENT_ITEM) {
                    if (isShowing){
                        //setVisibility( false );
                    }
                    else {
                        setVisibility( true );
                    }
                }*/


            }
        } );
        refreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress.setVisibility( View.VISIBLE );
                recyclerView.removeAllViews();
                tickers.clear();
                recyclerView.removeAllViews();
                loadData();
            }
        } );
        return view;
    }

    private void setVisibility(boolean b) {
        if (b) {
            isShowing = false;
            Toast.makeText( getActivity(), "hide", Toast.LENGTH_SHORT ).show();
        } else {
            isShowing = true;
            Toast.makeText( getActivity(), "show", Toast.LENGTH_SHORT ).show();
        }
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();
        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

        Call<ExchangeDetailModel> call = uploadInterFace.getExchangeDetailById( EXCHANGE_ID );
        call.enqueue( new Callback<ExchangeDetailModel>() {
            @Override
            public void onResponse(Call<ExchangeDetailModel> call, Response<ExchangeDetailModel> response) {

                progress.setVisibility( View.GONE );
                if (!response.isSuccessful()) {
                    Toast.makeText( getActivity(), "failed: try again", Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (response.body() != null) {
                    ExchangeDetailModel detailModel = response.body();
                    setText( detailModel );
                    setInfo( detailModel );
                    tickers.addAll( detailModel.getTickers() );
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility( View.VISIBLE );
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing( false );
                    }

                }
            }

            @Override
            public void onFailure(Call<ExchangeDetailModel> call, Throwable t) {
                Toast.makeText( getActivity(), "try again", Toast.LENGTH_SHORT ).show();
                getActivity().finish();
            }
        } );
    }

    private void setInfo(final ExchangeDetailModel detailModel) {
        final String link1 = detailModel.getOther_url_1();
        final String link2 = detailModel.getOther_url_2();
        ExchangeInfo.year.setText( "" + detailModel.getYear_established() );
        ExchangeInfo.Country.setText( "" + detailModel.getCountry() );
        ExchangeInfo.TradingIncentive.setText( detailModel.isHas_trading_incentive() ? "Yes" : "No" );
        ExchangeInfo.Link1.setText( link1 );
        ExchangeInfo.Link2.setText( link2 );

        ExchangeInfo.Link1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent( context, WebViewActivity.class ).putExtra( "url", link2 ) );
            }
        } );

        ExchangeInfo.Link2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity( new Intent( context, WebViewActivity.class ).putExtra( "url", link2 ) );
            }
        } );

        ExchangeInfo.facebook.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String facebookUrl = detailModel.getFacebook_url();
                context.startActivity( new Intent( context, WebViewActivity.class ).putExtra( "url", facebookUrl ) );
            }
        } );

        ExchangeInfo.homepage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String homeUrl = detailModel.getUrl();
                context.startActivity( new Intent( context, WebViewActivity.class ).putExtra( "url", homeUrl ) );
            }
        } );

        ExchangeInfo.reddit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String redditUrl = detailModel.getReddit_url();
                context.startActivity( new Intent( context, WebViewActivity.class ).putExtra( "url", redditUrl ) );
            }
        } );

    }

    private void setText(ExchangeDetailModel detailModel) {
        try {
            ExchangeActivity.trust_score_rank.setText( "Rank #" + detailModel.getTrust_score_rank() );
            ExchangeActivity.trade_volume_24h_btc.setText( "BTC" + new DecimalFormat( "#,##0.##" ).format( detailModel.getTrade_volume_24h_btc() ) );
            ExchangeActivity.trade_volume_24h_btc_normalized.setText( "24H Normalized BTC" + new DecimalFormat( "#,##0.##" ).format( detailModel.getTrade_volume_24h_btc_normalized() ) );
            ExchangeActivity.trust_score.setText( "Trust " + detailModel.getTrust_score() + "/10" );
            boolean centralized = detailModel.isCentralized();
            if (centralized) {
                ExchangeActivity.centralized.setText( "CENTRALIZED" );
            } else {
                ExchangeActivity.centralized.setText( "NON-CENTRALIZED" );
            }

        } catch (Exception e) {
            Toast.makeText( context, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
        }

    }

    private class ExchangeMarketAdapter extends RecyclerView.Adapter<ExchangeMarketAdapter.MyViewHolder> {
        List<ExchangeTicker> tickers;
        Context context;
        NumberFormat format = NumberFormat.getCurrencyInstance();
        NumberFormat format2 = NumberFormat.getCurrencyInstance();


        public ExchangeMarketAdapter(List<ExchangeTicker> tickers, Context context) {
            this.tickers = tickers;
            this.context = context;
        }

        @NonNull
        @Override
        public ExchangeMarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( context ).inflate( R.layout.exchange_market_view, viewGroup, false );
            format.setMaximumFractionDigits( 6 );
            format2.setMaximumFractionDigits( 2 );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull ExchangeMarketAdapter.MyViewHolder myViewHolder, final int i) {

            try {
                String pair = tickers.get( i ).getBase() + "\n" + tickers.get( i ).getTarget();
                myViewHolder.pair.setText( pair );


                float Price = tickers.get( i ).getLast();
                float Volume = tickers.get( i ).getVolume();

                if (tickers.get( i ).getTarget().length() == 3) {
                    format.setCurrency( Currency.getInstance( tickers.get( i ).getTarget() ) );
                    myViewHolder.price.setText( format.format( Price ) );
                } else {
                    myViewHolder.price.setText( tickers.get( i ).getTarget() + new DecimalFormat( "#,##0.##" ).format( Price ) );
                }

                myViewHolder.volume.setText( format2.format( Volume ) );

                String trust_score = tickers.get( i ).getTrust_score();
                if (trust_score.equalsIgnoreCase( "green" ))
                    myViewHolder.trustImage.setImageResource( R.drawable.green_cirle );
                else {
                    myViewHolder.trustImage.setImageResource( R.drawable.red_circle );
                }

                myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Url = tickers.get( i ).getTrade_url();
                        startActivity( new Intent( Intent.ACTION_VIEW,
                                Uri.parse( Url ) ) );

                    }
                } );
            } catch (Exception e) {
            }

        }

        @Override
        public int getItemCount() {
            return tickers.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView pair, price, volume;
            private ImageView trustImage;
            private RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );

                pair = (TextView) itemView.findViewById( R.id.textView13 );
                price = (TextView) itemView.findViewById( R.id.textView14 );
                volume = (TextView) itemView.findViewById( R.id.textView17 );
                trustImage = (ImageView) itemView.findViewById( R.id.textView18 );
                layout = (RelativeLayout) itemView.findViewById( R.id.ex_header_lay );
            }
        }
    }
}
