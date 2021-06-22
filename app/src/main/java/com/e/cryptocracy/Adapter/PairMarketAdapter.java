package com.e.cryptocracy.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.TickerModel;
import com.e.cryptocracy.Model.TradingModel;
import com.e.cryptocracy.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PairMarketAdapter extends RecyclerView.Adapter<PairMarketAdapter.MyViewHolder> {
    List<TickerModel> pairMarketModelslist;
    Context context;
    NumberFormat format;


    public PairMarketAdapter(List<TickerModel> pairMarketModelslist, Context context) {
        this.pairMarketModelslist = pairMarketModelslist;
        this.context = context;


    }

    @NonNull
    @Override
    public PairMarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.coin_pair_view, viewGroup, false );
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull PairMarketAdapter.MyViewHolder myViewHolder, int position) {
        String target = pairMarketModelslist.get( position ).getTarget();
        String base = pairMarketModelslist.get( position ).getBase();
        double price = pairMarketModelslist.get( position ).getLast();
        double volume = pairMarketModelslist.get( position ).getVolume();
        myViewHolder.base.setText( "/" + base );
        myViewHolder.target.setText( target );
        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits( 9 );
        if (target.length() == 3) {
            format.setCurrency( Currency.getInstance( target.toLowerCase() ) );
            myViewHolder.price.setText( "Price: "+format.format( price ) );

        } else {
            myViewHolder.price.setText( "Price: "+target + price );
        }

        myViewHolder.volume.setText( "Volume: "+new DecimalFormat("###,###,###.###").format( volume ) );
        myViewHolder.trading_site_name.setText( pairMarketModelslist.get( position ).getMarket().getName() );
        String trading_siteID = pairMarketModelslist.get( position ).getMarket().getIdentifier();
        setTradingIcon( myViewHolder, trading_siteID );

    }

    private void setTradingIcon(final MyViewHolder myViewHolder, final String trading_siteID) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();


        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

        Call<TradingModel> call = uploadInterFace.getImageUrl( trading_siteID );
        call.enqueue( new Callback<TradingModel>() {
            @Override
            public void onResponse(Call<TradingModel> call, Response<TradingModel> response) {
                if (response.isSuccessful()) {
                    TradingModel tradingModel = response.body();
                    if (!trading_siteID.isEmpty()) {
                        String image_url = tradingModel.getImage();
                        Picasso.with( context ).load( image_url ).into( myViewHolder.imageView );
                    }
                }
            }

            @Override
            public void onFailure(Call<TradingModel> call, Throwable t) {

            }
        } );


    }

    @Override
    public int getItemCount() {
        return pairMarketModelslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView base, target, trading_site_name, price, volume;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            base = itemView.findViewById( R.id.base );
            target = itemView.findViewById( R.id.target );
            trading_site_name = itemView.findViewById( R.id.trading_site_name );
            imageView = itemView.findViewById( R.id.imageView2 );
            price = itemView.findViewById( R.id.textView9 );
            volume = itemView.findViewById( R.id.textView10 );


        }
    }
}
