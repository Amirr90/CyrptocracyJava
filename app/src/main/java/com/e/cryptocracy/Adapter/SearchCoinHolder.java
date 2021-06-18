package com.e.cryptocracy.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.e.cryptocracy.CoinDetailActivity;
import com.e.cryptocracy.Model.CoinModal;
import com.e.cryptocracy.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchCoinHolder extends RecyclerView.Adapter<SearchCoinHolder.SearchViewHolder> {
    List<CoinModal> list;
    Context context;



    public SearchCoinHolder(List<CoinModal> list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.serach_coin_view, viewGroup, false);
        return new SearchViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder searchViewHolder, final int i) {

        try {
            StringBuilder name=new StringBuilder();
            name.append( list.get( i ).getName()+"(" );
            name.append( list.get( i ).getSymbol()+")" );
            searchViewHolder.s_Name.setText( name.toString() );
            Picasso.with( context ).load( "https://res.cloudinary.com/dxi90ksom/image/upload/"+list.get( i ).getSymbol()+".png" )
                    .networkPolicy( NetworkPolicy.OFFLINE ).placeholder( R.drawable.app_logo )
                    .into( searchViewHolder.s_Icon, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    try {
                        Picasso.with( context ).load( "https://res.cloudinary.com/dxi90ksom/image/upload/"+list.get( i ).getSymbol()+".png" )
                                .placeholder( R.drawable.app_logo ).into( searchViewHolder.s_Icon);
                    }
                    catch (IndexOutOfBoundsException e)
                    {

                    }
                }
            } );
        }
        catch (Exception e)
        {

        }

        searchViewHolder.layout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( context, CoinDetailActivity.class );
                intent.putExtra( "coin_id",list.get( i ).getId() );
                context.startActivity(intent);
            }
        } );

    }


    public void updateList(List<CoinModal> list, ProgressBar progressBar){
        this.list=list;
        notifyDataSetChanged();
        progressBar.setVisibility( View.GONE );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView s_Rank,s_Name;
        ImageView s_Icon;
        LinearLayout layout;
        public SearchViewHolder(@NonNull View itemView) {
            super( itemView );

            layout=(LinearLayout)itemView.findViewById( R.id.s_coin_layout );
            s_Name=(TextView)itemView.findViewById( R.id.s_name );
            //*s_Rank=(TextView)itemView.findViewById( R.id.s_rank );*//*
            s_Icon=(ImageView)itemView.findViewById( R.id.s_icon );
        }
    }
}
