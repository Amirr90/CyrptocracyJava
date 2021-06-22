package com.e.cryptocracy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.cryptocracy.CoinDetailActivity;
import com.e.cryptocracy.HomeScreen;
import com.e.cryptocracy.Model.CoinModal;
import com.e.cryptocracy.Model.Favourite;
import com.e.cryptocracy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import co.blankkeys.animatedlinegraphview.AnimatedLineGraphView;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.MyViewHolder> {
    private final List<CoinModal> coinList;
    private final Context context;
    private final String TAG = "CoinAdapter";
    private static final int MAX_LENGTH = 4;
    FirebaseFirestore db;
    CollectionReference favRef;

    public CoinAdapter(List<CoinModal> coinList, Context context) {
        this.coinList = coinList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        favRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Favourite");
    }

    @NonNull
    @Override
    public CoinAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coin_view, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CoinAdapter.MyViewHolder myViewHolder, final int position) {
        double Price_change_percentage_24h = coinList.get(position).getPrice_change_percentage_24h();
        double COIN_PRICE = coinList.get(position).getCurrent_price();
        double COIN_MCAP = coinList.get(position).getMarket_cap();

        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(9);
        format.setCurrency(Currency.getInstance(HomeScreen.CURRENCY));


        setFavIcon(myViewHolder, position);
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

        myViewHolder.favIcon.setOnClickListener(view -> {
            String coinId = coinList.get(position).getId();
            myViewHolder.favIcon.setImageResource(R.drawable.ic_star_filled);
            favRef.document(coinId)
                    .set(new Favourite(coinId))
                    .addOnCompleteListener(task -> Toast.makeText(context, "added", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> myViewHolder.favIcon.setImageResource(R.drawable.ic_star_border_black_24dp));
        });


    }


    private void setFavIcon(final MyViewHolder myViewHolder, int position) {
        favRef.document(coinList.get(position).getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            myViewHolder.favIcon.setImageResource(R.drawable.ic_star_filled);
                        } else {
                            myViewHolder.favIcon.setImageResource(R.drawable.ic_star_border_black_24dp);
                        }
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
