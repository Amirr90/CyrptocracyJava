package com.e.cryptocracy;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.chart.common.dataentry.DataEntry;
import com.e.cryptocracy.Adapter.PairMarketAdapter;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.CoinExchange;
import com.e.cryptocracy.Model.CoinPrice;
import com.e.cryptocracy.Model.Favourite;
import com.e.cryptocracy.Model.GraphModel;
import com.e.cryptocracy.Model.TickerModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marcoscg.dialogsheet.DialogSheet;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinDetailActivity extends AppCompatActivity {
    private static final String TAG = "CoinDetailActivity";
    public String COIN_ID;
    FirebaseFirestore db;
    private String DAYS;
    List<DataEntry> seriesData;
    private String change = "24h,7d,14d,30d,200d,1y";
    TextView market_cap_rank, market_cap, T_volume, high24, low24, available_total,
            allTime_high, since_allTimeHigh, allTimeHighDate, mName;
    TextView H24, D7, D14, D30, D200, Y1;
    List<CoinPrice> list;
    private ProgressBar progressBar;
    TextView pair, mCoinName;
    DialogSheet dialogSheet;
    RecyclerView recyclerView;
    PairMarketAdapter adapter;
    List<TickerModel> pairMarketList;
    CoinExchange exchange;
    ImageView favCoinImage;
    CollectionReference favRef;
    CircleImageView mProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coin_detail );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        db = FirebaseFirestore.getInstance();


        findViews();
        if (getIntent().hasExtra( "coin_id" )) {
            COIN_ID = getIntent().getStringExtra( "coin_id" );
            DAYS = "1";
            setToolbar( toolbar );
            getCoinDataById( COIN_ID );
            setGraphView( DAYS, COIN_ID );
            bottomDialog();
            setFavIcon( COIN_ID );
        } else {
            finish();
        }


        SingleSelectToggleGroup single = (SingleSelectToggleGroup) findViewById( R.id.group_choices );
        single.setOnCheckedChangeListener( new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.choice_24h:
                        setGraphView( "1", COIN_ID );
                        break;
                    case R.id.choice_1w:
                        setGraphView( "7", COIN_ID );
                        break;
                    case R.id.choice_1m:
                        setGraphView( "30", COIN_ID );
                        break;
                    case R.id.choice_3m:
                        setGraphView( "90", COIN_ID );
                        break;
                    case R.id.choice_6m:
                        setGraphView( "180", COIN_ID );
                        break;
                    case R.id.choice_1y:
                        setGraphView( "360", COIN_ID );
                        break;

                    case R.id.choice_max:
                        setGraphView( "max", COIN_ID );
                        break;
                }
            }
        } );

        favCoinImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavCoin( COIN_ID );
            }
        } );

    }

    private void setFavIcon(String coinId) {
        favRef = db.collection( "users" )
                .document( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                .collection( "Favourite" );
        favRef.document( coinId )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                favCoinImage.setImageResource( R.drawable.ic_star_filled );
                            } else {
                                favCoinImage.setImageResource( R.drawable.ic_star_border_black_24dp );
                            }
                        }
                    }
                } );
    }


    private void updateFavCoin(final String coinId) {

        favRef = db.collection( "users" )
                .document( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                .collection( "Favourite" );
        favRef.document( coinId )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                favCoinImage.setImageResource( R.drawable.ic_star_border_black_24dp );
                                favRef.document( coinId )
                                        .delete().addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        favCoinImage.setImageResource( R.drawable.ic_star_filled );
                                    }
                                } );
                            } else {
                                favCoinImage.setImageResource( R.drawable.ic_star_filled );
                                favRef.document( coinId )
                                        .set( new Favourite( coinId ) )
                                        .addOnFailureListener( new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                favCoinImage.setImageResource( R.drawable.ic_star_border_black_24dp );
                                            }
                                        } );
                            }
                        }
                    }
                } );

    }

    private void findViews() {
        progressBar = (ProgressBar) findViewById( R.id.progress_bar );
        market_cap_rank = (TextView) findViewById( R.id.price_m_cap_rank );
        market_cap = (TextView) findViewById( R.id.price_cap_rank );
        T_volume = (TextView) findViewById( R.id.country );
        high24 = (TextView) findViewById( R.id.description );
        low24 = (TextView) findViewById( R.id.has_trading_incentive );
        available_total = (TextView) findViewById( R.id.trade_volume_24h_btc_normalized );
        allTime_high = (TextView) findViewById( R.id.telegram_channel_user_count );
        since_allTimeHigh = (TextView) findViewById( R.id.coingecko_rank );
        mName = (TextView) findViewById( R.id.coin_name );
        allTimeHighDate = (TextView) findViewById( R.id.country_origin );
        favCoinImage = (ImageView) findViewById( R.id.favourite_icon_1 );

        H24 = (TextView) findViewById( R.id.price_h24 );
        D7 = (TextView) findViewById( R.id.price_d7 );
        D14 = (TextView) findViewById( R.id.price_d14 );
        D30 = (TextView) findViewById( R.id.price_d30 );
        D200 = findViewById( R.id.price_d200 );
        Y1 = (TextView) findViewById( R.id.price_y1 );

        mProfileImage = (CircleImageView) findViewById( R.id.profile_image );

    }

    private void getCoinDataById(String coin_id) {


        final NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits( 6 );
        format.setCurrency( Currency.getInstance( HomeScreen.CURRENCY ) );


        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();
        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<List<CoinPrice>> call = uploadInterFace.getCoinPrice( HomeScreen.CURRENCY, coin_id, change );
        call.enqueue( new Callback<List<CoinPrice>>() {
            @Override
            public void onResponse(Call<List<CoinPrice>> call, Response<List<CoinPrice>> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility( View.GONE );
                    Toast.makeText( CoinDetailActivity.this, "failed: " + response.errorBody(), Toast.LENGTH_SHORT ).show();
                    return;
                }
                list = response.body();
                progressBar.setVisibility( View.GONE );
                try {

                    String imageUrl = list.get( 0 ).getImage();
                    if (imageUrl != null && !imageUrl.equals( "" )) {
                        Picasso.with( CoinDetailActivity.this ).load( imageUrl ).into( mProfileImage );
                    }
                    mCoinName.setText( list.get( 0 ).getSymbol() );
                    ImageView imageView = (ImageView) findViewById( R.id.up_down_image );
                    TextView price = (TextView) findViewById( R.id.price );
                    float pri = list.get( 0 ).getCurrent_price();


                    //set Price
                    price.setText( format.format( pri ) );


                    //setPair
                    if (list.get( 0 ).getSymbol() != null && HomeScreen.CURRENCY != null)
                        pair.setText( list.get( 0 ).getSymbol().toUpperCase() + " Market" );


                    //set Price Percentage
                    TextView price_percentage = (TextView) findViewById( R.id.change_percentage );
                    float pri_percentage = list.get( 0 ).getPrice_change_percentage_24h();
                    price_percentage.setText( pri_percentage + "%" );
                    if (pri_percentage > 0) {
                        price_percentage.setTextColor( getResources().getColor( R.color.green ) );
                        imageView.setImageResource( R.mipmap.sort_up );
                    } else {
                        imageView.setImageResource( R.mipmap.sort_down );
                        price_percentage.setTextColor( getResources().getColor( R.color.red ) );
                    }

                    market_cap_rank.setText( "" + (int) list.get( 0 ).getMarket_cap_rank() );
                    String cap = String.valueOf( (long) list.get( 0 ).getMarket_cap() );
                    if (cap.length() > 13)
                        market_cap.setTextSize( TypedValue.COMPLEX_UNIT_SP, (float) 13 );

                    market_cap.setText( format.format( (long) list.get( 0 ).getMarket_cap() ) );
                    T_volume.setText( format.format( list.get( 0 ).getTotal_volume() ) );
                    high24.setText( format.format( list.get( 0 ).getHigh_24h() ) );
                    mName.setText( list.get( 0 ).getName() );
                    low24.setText( format.format( list.get( 0 ).getLow_24h() ) );
                    available_total.setText( new DecimalFormat( "###,###,###,###" ).format( list.get( 0 ).getCirculating_supply() ) + "/"
                            + new DecimalFormat( "###,###,###,###" ).format( list.get( 0 ).getTotal_supply() ) );
                    allTime_high.setText( format.format( list.get( 0 ).getAth() ) );
                    since_allTimeHigh.setText( list.get( 0 ).getAth_change_percentage() + "%" );
                    allTimeHighDate.setText( list.get( 0 ).getAth_date() );


                    H24.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_24h_in_currency() ) + "%" );
                    D7.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_7d_in_currency() ) + "%" );
                    D14.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_14d_in_currency() ) + "%" );
                    D30.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_30d_in_currency() ) + "%" );
                    D200.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_200d_in_currency() ) + "%" );
                    Y1.setText( "" + new DecimalFormat( "0.#" ).format( list.get( 0 ).getPrice_change_percentage_1y_in_currency() ) + "%" );
                    setTTextColor( H24, list.get( 0 ).getPrice_change_percentage_24h_in_currency() );
                    setTTextColor( D7, list.get( 0 ).getPrice_change_percentage_7d_in_currency() );
                    setTTextColor( D14, list.get( 0 ).getPrice_change_percentage_14d_in_currency() );
                    setTTextColor( D30, list.get( 0 ).getPrice_change_percentage_30d_in_currency() );
                    setTTextColor( D200, list.get( 0 ).getPrice_change_percentage_200d_in_currency() );
                    setTTextColor( Y1, list.get( 0 ).getPrice_change_percentage_1y_in_currency() );
                    setTTextColor( since_allTimeHigh, list.get( 0 ).getAth_change_percentage() );

                } catch (Exception e) {
                    Toast.makeText( CoinDetailActivity.this, "error " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                }


            }

            @Override
            public void onFailure(Call<List<CoinPrice>> call, Throwable t) {
                Toast.makeText( CoinDetailActivity.this, "error: " + t.getMessage(), Toast.LENGTH_SHORT ).show();
                progressBar.setVisibility( View.GONE );

            }
        } );


    }

    private void setTTextColor(TextView textView, float price) {
        textView.setTextColor( price > 0 ? getResources().getColor( R.color.green ) : getResources().getColor( R.color.red ) );
    }

    private void setGraphView(String DAYS, String COIN_ID) {
        progressBar.setVisibility( View.VISIBLE );
        seriesData = new ArrayList<>();
        //adding data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();
        RetrofitService GraphData = retrofit.create( RetrofitService.class );
        Call<GraphModel> call = GraphData.getGraphData( COIN_ID, HomeScreen.CURRENCY, DAYS );
        call.enqueue( new Callback<GraphModel>() {
            @Override
            public void onResponse(Call<GraphModel> call, Response<GraphModel> response) {
                progressBar.setVisibility( View.GONE );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!seriesData.isEmpty())
                            seriesData.clear();
                        GraphModel graphData = response.body();
                        loadChart( graphData );

                    } else {
                        Toast.makeText( CoinDetailActivity.this, "no data ", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( CoinDetailActivity.this, "response is not successful", Toast.LENGTH_SHORT ).show();
                }
            }


            @Override
            public void onFailure(Call<GraphModel> call, Throwable t) {
                progressBar.setVisibility( View.GONE );
                Log.e( TAG, "read error in graph data: " + t.getLocalizedMessage() );
                Toast.makeText( CoinDetailActivity.this, "try again " + t.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );


    }

    private void loadChart(GraphModel graphData) {
        LineChart chart = (LineChart) findViewById( R.id.chart );
        List<Entry> entries = new ArrayList<Entry>();
        try {
            for (int a = 0; a < graphData.getPrices().size(); a++) {
                float timeStamp = graphData.getPrices().get( a ).get( 0 );
                float price = graphData.getPrices().get( a ).get( 1 );
              /*  String time = java.text.DateFormat.getTimeInstance().format( timeStamp );
                float market_caps = graphData.getMarket_caps().get( a ).get( 1 );
                float total_volumes = graphData.getTotal_volumes().get( a ).get( 1 );*/
                try {
                    entries.add( new Entry( timeStamp, price, R.drawable.app_logo ) );
                }
                catch (Exception e){}
            }
        } catch (Exception e) {
            Log.e( TAG, "null value: " + e.getLocalizedMessage() );
        }

        chart.setDragEnabled( true );
        chart.getDescription().setEnabled( false );
        chart.setScaleEnabled( true );
        chart.animateX( 1500 );
        chart.getXAxis().setEnabled( false );
        chart.getAxisRight().setEnabled( false );
        Legend l = chart.getLegend();
        l.setEnabled( true );

        LineDataSet dataSet = new LineDataSet( entries, "Price in (" + HomeScreen.CURRENCY.toUpperCase() + ")" ); // add entries to dataset
        dataSet.setColor( R.color.colorPrimaryDark );
        dataSet.setValueTextColor( R.color.colorAccent );
        dataSet.setCircleRadius( 1f );
        dataSet.setCircleHoleRadius( 0.2f );
        dataSet.setCircleColor( R.color.colorPrimaryDark );

        chart.setNoDataText( "Loading..." );

        LineData lineData = new LineData( dataSet );
        chart.setData( lineData );
        chart.invalidate();

    }

    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        pair = (TextView) toolbar.findViewById( R.id.pairing );
        mCoinName = (TextView) toolbar.findViewById( R.id.name_coin );

        pair.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility( View.VISIBLE );
                dialogSheet.show();
            }
        } );
    }


    private void bottomDialog() {
        dialogSheet = new DialogSheet( this );

        // get the layout inflater
        LayoutInflater inflater = CoinDetailActivity.this.getLayoutInflater();
        View view = inflater.inflate( R.layout.coin_pair_recycler_layout, null );

        recyclerView = (RecyclerView) view.findViewById( R.id.pair_rec );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        loadPairData();

        dialogSheet.setTitle( "Market" )
                .setTitleTextSize( 20 ) // In SP
                .setCancelable( true )
                .setView( view )
                .setPositiveButton( "Dismiss", new DialogSheet.OnPositiveClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Your action
                    }
                } )
                .setBackgroundColor( Color.BLACK ) // Your custom background color
                .setButtonsColorRes( R.color.colorAccent );// ;You can use dialogSheetAccent style attribute instead

        dialogSheet.setBackgroundColor( getResources().getColor( R.color.colorWhite ) );
        dialogSheet.setButtonsColor( getResources().getColor( R.color.colorPrimaryDark ) );
        dialogSheet.setButtonsColorRes( R.color.dark_grey );


        dialogSheet.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                progressBar.setVisibility( View.GONE );
            }
        } );

    }

    private void loadPairData() {
        progressBar.setVisibility( View.VISIBLE );
        exchange = new CoinExchange();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

        Call<CoinExchange> call = uploadInterFace.getExchangeByCoinId( COIN_ID );

        call.enqueue( new Callback<CoinExchange>() {
            @Override
            public void onResponse(Call<CoinExchange> call, Response<CoinExchange> response) {

                progressBar.setVisibility( View.GONE );
                if (response.isSuccessful()) {
                    exchange = response.body();
                    pairMarketList = new ArrayList<>( Arrays.asList( exchange.getTickers() ) );
                    adapter = new PairMarketAdapter( pairMarketList, CoinDetailActivity.this );
                    recyclerView.setAdapter( adapter );
                } else {
                    Toast.makeText( CoinDetailActivity.this, "No data", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<CoinExchange> call, Throwable t) {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( CoinDetailActivity.this, "failed " + t.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
