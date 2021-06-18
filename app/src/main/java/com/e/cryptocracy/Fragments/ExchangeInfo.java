package com.e.cryptocracy.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.cryptocracy.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ExchangeInfo extends Fragment {
    public static TextView year;
    public static TextView Link1;
    public static TextView Link2;
    public static TextView Country;
    public static TextView TradingIncentive;
    public static LinearLayout homepage;
    public static LinearLayout facebook;
    public static LinearLayout reddit;


    Context context;

    @SuppressLint("ValidFragment")
    public ExchangeInfo(Context context) {
        this.context = context;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_exchange_info, container, false );

        year = (TextView) view.findViewById( R.id.textView20 );
        Link1 = (TextView) view.findViewById( R.id.link1 );
        Link2 = (TextView) view.findViewById( R.id.link2 );
        Country = (TextView) view.findViewById( R.id.ex_country );
        TradingIncentive = (TextView) view.findViewById( R.id.ex_trading_incentive );

        homepage = (LinearLayout) view.findViewById( R.id.homepage );
        facebook = (LinearLayout) view.findViewById( R.id.face );
        reddit = (LinearLayout) view.findViewById( R.id.red );

        return  view;
    }

}
