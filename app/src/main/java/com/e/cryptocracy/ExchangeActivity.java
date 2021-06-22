package com.e.cryptocracy;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.e.cryptocracy.Adapter.ViewPagerAdapter;
import com.e.cryptocracy.Fragments.ExchangeInfo;
import com.e.cryptocracy.Fragments.ExchangeMarket;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class ExchangeActivity extends AppCompatActivity {

    TextView exchangeName;
    private ImageView ExchangeImage;
    private String EXCHANGE_ID;
    private String EXCHANGE_URL;
    private String EXCHANGE_NAME;

    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    public static TextView trade_volume_24h_btc;
    public static TextView trust_score_rank;
    public static TextView trade_volume_24h_btc_normalized;
    public static TextView centralized;
    public static TextView trust_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_exchange );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );

        exchangeName = toolbar.findViewById( R.id.textView2 );
        trade_volume_24h_btc = findViewById( R.id.textView );
        trust_score_rank = findViewById( R.id.textView12 );
        trade_volume_24h_btc_normalized = findViewById( R.id.textView11 );
        centralized = findViewById( R.id.textView15 );
        trust_score = findViewById( R.id.textView16 );
        ExchangeImage = toolbar.findViewById( R.id.imageView4 );

        if (getIntent().hasExtra( "exchangeId" )) {
            EXCHANGE_ID = getIntent().getStringExtra( "exchangeId" );
            EXCHANGE_NAME = getIntent().getStringExtra( "exchangeName" );
            EXCHANGE_URL = getIntent().getStringExtra( "exchangeImageUrl" );
            if (EXCHANGE_URL != null)
                Picasso.with( this ).load( EXCHANGE_URL ).into( ExchangeImage );
            exchangeName.setText( EXCHANGE_NAME );

            setFragment();

            setDetail( EXCHANGE_ID );

        }


    }

    private void setDetail(String exchange_id) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setFragment() {
        tabLayout = findViewById( R.id.ex_tabs );
        pager = findViewById( R.id.ex_view_pager );
        pagerAdapter = new ViewPagerAdapter( getSupportFragmentManager() );
        pagerAdapter.Addfragment( new ExchangeMarket( ExchangeActivity.this, EXCHANGE_ID ), "Market" );
        pagerAdapter.Addfragment( new ExchangeInfo( ExchangeActivity.this ), "Info" );

        pager.setAdapter( pagerAdapter );
        tabLayout.setupWithViewPager( pager );

    }
}
