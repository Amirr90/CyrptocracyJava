package com.e.cryptocracy;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject;
import com.highsoft.highcharts.common.hichartsclasses.HIHover;
import com.highsoft.highcharts.common.hichartsclasses.HILabel;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HINavigation;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotBands;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HISpline;
import com.highsoft.highcharts.common.hichartsclasses.HIStates;
import com.highsoft.highcharts.common.hichartsclasses.HISubtitle;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.marcoscg.dialogsheet.DialogSheet;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
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
    List<DataEntry> seriesData;
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


    //Add
    FrameLayout adContainerView, adContainerView2;
    AdView adView, mAdView;
    AdRequest adRequest, adRequest2;

    TextInputLayout inputLayout, inputLayoutPrice;
    TextInputEditText coinQty, coinPriceConverted;

    Float CoinPrice;
    NumberFormat format;

    HIChartView chartView;
    HIOptions options = new HIOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        chartView = findViewById(R.id.chartView);
        chartView.theme = "sand-signika";

        db = FirebaseFirestore.getInstance();


        findViews();
        if (getIntent().hasExtra("coin_id")) {
            COIN_ID = getIntent().getStringExtra("coin_id");
            String DAYS = "1";
            setToolbar(toolbar);
            getCoinDataById(COIN_ID);
            setGraphView(DAYS, COIN_ID);
            bottomDialog();
            setFavIcon(COIN_ID);
            initSpLineWithPlotBandsChart();
        } else {
            finish();
        }


        SingleSelectToggleGroup single = (SingleSelectToggleGroup) findViewById(R.id.group_choices);
        single.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.choice_24h:
                        setGraphView("1", COIN_ID);
                        break;
                    case R.id.choice_1w:
                        setGraphView("7", COIN_ID);
                        break;
                    case R.id.choice_1m:
                        setGraphView("30", COIN_ID);
                        break;
                    case R.id.choice_3m:
                        setGraphView("90", COIN_ID);
                        break;
                    case R.id.choice_6m:
                        setGraphView("180", COIN_ID);
                        break;
                    case R.id.choice_1y:
                        setGraphView("360", COIN_ID);
                        break;

                    case R.id.choice_max:
                        setGraphView("max", COIN_ID);
                        break;
                }
            }
        });

        favCoinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavCoin(COIN_ID);
            }
        });

    }

    private void setFavIcon(String coinId) {
        favRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Favourite");
        favRef.document(coinId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                favCoinImage.setImageResource(R.drawable.ic_star_filled);
                            } else {
                                favCoinImage.setImageResource(R.drawable.ic_star_border_black_24dp);
                            }
                        }
                    }
                });
    }


    private void updateFavCoin(final String coinId) {

        favRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Favourite");
        favRef.document(coinId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                favCoinImage.setImageResource(R.drawable.ic_star_border_black_24dp);
                                favRef.document(coinId)
                                        .delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        favCoinImage.setImageResource(R.drawable.ic_star_filled);
                                    }
                                });
                            } else {
                                favCoinImage.setImageResource(R.drawable.ic_star_filled);
                                favRef.document(coinId)
                                        .set(new Favourite(coinId))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                favCoinImage.setImageResource(R.drawable.ic_star_border_black_24dp);
                                            }
                                        });
                            }
                        }
                    }
                });

    }

    private void findViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        market_cap_rank = (TextView) findViewById(R.id.price_m_cap_rank);
        market_cap = (TextView) findViewById(R.id.price_cap_rank);
        T_volume = (TextView) findViewById(R.id.country);
        high24 = (TextView) findViewById(R.id.description);
        low24 = (TextView) findViewById(R.id.has_trading_incentive);
        available_total = (TextView) findViewById(R.id.trade_volume_24h_btc_normalized);
        allTime_high = (TextView) findViewById(R.id.telegram_channel_user_count);
        since_allTimeHigh = (TextView) findViewById(R.id.coingecko_rank);
        mName = (TextView) findViewById(R.id.coin_name);
        allTimeHighDate = (TextView) findViewById(R.id.country_origin);
        favCoinImage = (ImageView) findViewById(R.id.favourite_icon_1);

        H24 = (TextView) findViewById(R.id.price_h24);
        D7 = (TextView) findViewById(R.id.price_d7);
        D14 = (TextView) findViewById(R.id.price_d14);
        D30 = (TextView) findViewById(R.id.price_d30);
        D200 = findViewById(R.id.price_d200);
        Y1 = (TextView) findViewById(R.id.price_y1);

        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        adContainerView = findViewById(R.id.ad_view_container2);
        adContainerView2 = findViewById(R.id.ad_view_container3);

        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(6);
        format.setCurrency(Currency.getInstance(HomeScreen.CURRENCY));

        mAdView = findViewById(R.id.adView);

        inputLayout = findViewById(R.id.textInputLay);
        inputLayoutPrice = findViewById(R.id.textInputLayPrice);
        coinQty = findViewById(R.id.etCoinQty);
        coinPriceConverted = findViewById(R.id.textPriceConverted);


        coinQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && charSequence.length() > 0) {
                    Float qty = Float.parseFloat(charSequence.toString());

                    if (null != CoinPrice) {
                        double convertedPrice = qty * CoinPrice;
                        inputLayoutPrice.setHint(" " + format.format(CoinPrice));
                        coinPriceConverted.setText(" " + format.format(convertedPrice));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        initAds();

    }

    private void initAds() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: ");
                setUpAds();
            }
        });

    }

    private void setUpAds() {
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id_test));
        adContainerView.addView(adView);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id_test));

        loadBanner();
    }

    private void loadBanner() {
        adRequest = new AdRequest.Builder().build();


        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);


        adRequest2 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest2);

    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void getCoinDataById(String coin_id) {


        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);
        String change = "24h,7d,14d,30d,200d,1y";
        Call<List<CoinPrice>> call = uploadInterFace.getCoinPrice(HomeScreen.CURRENCY, coin_id, change);
        call.enqueue(new Callback<List<CoinPrice>>() {
            @Override
            public void onResponse(Call<List<CoinPrice>> call, Response<List<CoinPrice>> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CoinDetailActivity.this, "failed: " + response.errorBody(), Toast.LENGTH_SHORT).show();
                    return;
                }
                list = response.body();
                progressBar.setVisibility(View.GONE);
                try {

                    String imageUrl = list.get(0).getImage();
                    if (imageUrl != null && !imageUrl.equals("")) {
                        Picasso.with(CoinDetailActivity.this).load(imageUrl).into(mProfileImage);
                    }
                    mCoinName.setText(list.get(0).getSymbol());
                    ImageView imageView = (ImageView) findViewById(R.id.up_down_image);
                    TextView price = (TextView) findViewById(R.id.price);
                    float pri = list.get(0).getCurrent_price();
                    CoinPrice = pri;


                    //set Price
                    price.setText(format.format(pri));


                    //setPair
                    if (list.get(0).getSymbol() != null && HomeScreen.CURRENCY != null)
                        pair.setText(list.get(0).getSymbol().toUpperCase() + " Market");


                    //set Price Percentage
                    TextView price_percentage = (TextView) findViewById(R.id.change_percentage);
                    float pri_percentage = list.get(0).getPrice_change_percentage_24h();
                    price_percentage.setText(pri_percentage + "%");
                    if (pri_percentage > 0) {
                        price_percentage.setTextColor(getResources().getColor(R.color.green));
                        imageView.setImageResource(R.mipmap.sort_up);
                    } else {
                        imageView.setImageResource(R.mipmap.sort_down);
                        price_percentage.setTextColor(getResources().getColor(R.color.red));
                    }

                    market_cap_rank.setText("" + (int) list.get(0).getMarket_cap_rank());
                    String cap = String.valueOf((long) list.get(0).getMarket_cap());
                    if (cap.length() > 13)
                        market_cap.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 13);

                    market_cap.setText(format.format((long) list.get(0).getMarket_cap()));
                    T_volume.setText(format.format(list.get(0).getTotal_volume()));
                    high24.setText(format.format(list.get(0).getHigh_24h()));
                    mName.setText(list.get(0).getName());

                    inputLayout.setHint(list.get(0).getName());
                    double convertedPrice = Float.parseFloat(coinQty.getText().toString()) * pri;

                    inputLayoutPrice.setHint(" " + format.format(pri));
                    coinPriceConverted.setText(" " + format.format(convertedPrice));

                    low24.setText(format.format(list.get(0).getLow_24h()));
                    available_total.setText(new DecimalFormat("###,###,###,###").format(list.get(0).getCirculating_supply()) + "/"
                            + new DecimalFormat("###,###,###,###").format(list.get(0).getTotal_supply()));
                    allTime_high.setText(format.format(list.get(0).getAth()));
                    since_allTimeHigh.setText(list.get(0).getAth_change_percentage() + "%");
                    allTimeHighDate.setText(list.get(0).getAth_date());


                    H24.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_24h_in_currency()) + "%");
                    D7.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_7d_in_currency()) + "%");
                    D14.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_14d_in_currency()) + "%");
                    D30.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_30d_in_currency()) + "%");
                    D200.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_200d_in_currency()) + "%");
                    Y1.setText("" + new DecimalFormat("0.#").format(list.get(0).getPrice_change_percentage_1y_in_currency()) + "%");
                    setTTextColor(H24, list.get(0).getPrice_change_percentage_24h_in_currency());
                    setTTextColor(D7, list.get(0).getPrice_change_percentage_7d_in_currency());
                    setTTextColor(D14, list.get(0).getPrice_change_percentage_14d_in_currency());
                    setTTextColor(D30, list.get(0).getPrice_change_percentage_30d_in_currency());
                    setTTextColor(D200, list.get(0).getPrice_change_percentage_200d_in_currency());
                    setTTextColor(Y1, list.get(0).getPrice_change_percentage_1y_in_currency());
                    setTTextColor(since_allTimeHigh, list.get(0).getAth_change_percentage());

                } catch (Exception e) {
                    Toast.makeText(CoinDetailActivity.this, "error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<CoinPrice>> call, Throwable t) {
                Toast.makeText(CoinDetailActivity.this, "error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });


    }

    private void setTTextColor(TextView textView, float price) {
        textView.setTextColor(price > 0 ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
    }

    private void setGraphView(String DAYS, String COIN_ID) {
        progressBar.setVisibility(View.VISIBLE);
        seriesData = new ArrayList<>();
        //adding data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService GraphData = retrofit.create(RetrofitService.class);
        Call<GraphModel> call = GraphData.getGraphData(COIN_ID, HomeScreen.CURRENCY, DAYS);
        call.enqueue(new Callback<GraphModel>() {
            @Override
            public void onResponse(Call<GraphModel> call, Response<GraphModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!seriesData.isEmpty())
                            seriesData.clear();
                        GraphModel graphData = response.body();
                        loadChart(graphData);
                        setChartValue(graphData);

                    } else {
                        Toast.makeText(CoinDetailActivity.this, "no data ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CoinDetailActivity.this, "response is not successful", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<GraphModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "read error in graph data: " + t.getLocalizedMessage());
                Toast.makeText(CoinDetailActivity.this, "try again " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initSpLineWithPlotBandsChart() {
        chartView.plugins = new ArrayList<>(Arrays.asList("series-label"));
        HITitle title = new HITitle();
        title.setText("Wind speed during two days");
        options.setTitle(title);

        HISubtitle subtitle = new HISubtitle();
        subtitle.setText("May 31 and and June 1, 2015 at two locations in Vik i Sogn, Norway");
        options.setSubtitle(subtitle);

        final HIXAxis xAxis = new HIXAxis();
        xAxis.setType("datetime");
        xAxis.setLabels(new HILabels());
        xAxis.getLabels().setOverflow("justify");
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        final HIYAxis yAxis = new HIYAxis();
        yAxis.setTitle(new HITitle());
        yAxis.getTitle().setText("Wind speed (m/s)");
        yAxis.setMinorGridLineWidth(0);
        yAxis.setGridLineWidth(0);
        yAxis.setAlternateGridColor(null);

        HIPlotBands plotBands1 = new HIPlotBands();
        plotBands1.setFrom(0.3);
        plotBands1.setTo(1.5);
        plotBands1.setColor(HIColor.initWithRGBA(68, 170, 213, 0.1));
        plotBands1.setLabel(new HILabel());
        plotBands1.getLabel().setText("Light air");
        plotBands1.getLabel().setStyle(new HICSSObject());
        plotBands1.getLabel().getStyle().setColor("606060");

        HIPlotBands plotBands2 = new HIPlotBands();
        plotBands2.setFrom(1.5);
        plotBands2.setTo(3.3);
        plotBands2.setColor(HIColor.initWithRGBA(0, 0, 0, 0));
        plotBands2.setLabel(new HILabel());
        plotBands2.getLabel().setText("Light breeze");
        plotBands2.getLabel().setStyle(new HICSSObject());
        plotBands2.getLabel().getStyle().setColor("#606060");

        HIPlotBands plotBands3 = new HIPlotBands();
        plotBands3.setFrom(3.3);
        plotBands3.setTo(5.5);
        plotBands3.setColor(HIColor.initWithRGBA(68, 170, 213, 0.1));
        plotBands3.setLabel(new HILabel());
        plotBands3.getLabel().setText("Gentle breeze");
        plotBands3.getLabel().setStyle(new HICSSObject());
        plotBands3.getLabel().getStyle().setColor("#606060");

        HIPlotBands plotBands4 = new HIPlotBands();
        plotBands4.setFrom(5.5);
        plotBands4.setTo(8);
        plotBands4.setColor(HIColor.initWithRGBA(0, 0, 0, 0));
        plotBands4.setLabel(new HILabel());
        plotBands4.getLabel().setText("Moderate breeze");
        plotBands4.getLabel().setStyle(new HICSSObject());
        plotBands4.getLabel().getStyle().setColor("#606060");

        HIPlotBands plotBands5 = new HIPlotBands();
        plotBands5.setFrom(8);
        plotBands5.setTo(11);
        plotBands5.setColor(HIColor.initWithRGBA(68, 170, 213, 0.1));
        plotBands5.setLabel(new HILabel());
        plotBands5.getLabel().setText("Fresh breeze");
        plotBands5.getLabel().setStyle(new HICSSObject());
        plotBands5.getLabel().getStyle().setColor("#606060");

        HIPlotBands plotBands6 = new HIPlotBands();
        plotBands6.setFrom(11);
        plotBands6.setTo(14);
        plotBands6.setColor(HIColor.initWithRGBA(0, 0, 0, 0));
        plotBands6.setLabel(new HILabel());
        plotBands6.getLabel().setText("Strong breeze");
        plotBands6.getLabel().setStyle(new HICSSObject());
        plotBands6.getLabel().getStyle().setColor("#606060");

        HIPlotBands plotBands7 = new HIPlotBands();
        plotBands7.setFrom(14);
        plotBands7.setTo(15);
        plotBands7.setColor(HIColor.initWithRGBA(68, 170, 213, 0.1));
        plotBands6.setLabel(new HILabel());
        plotBands6.getLabel().setText("High wind");
        plotBands6.getLabel().setStyle(new HICSSObject());
        plotBands6.getLabel().getStyle().setColor("#606060");

        HIPlotBands[] plotBandsList = new HIPlotBands[]{plotBands1, plotBands2, plotBands3, plotBands4, plotBands5, plotBands6, plotBands7};
        yAxis.setPlotBands(new ArrayList<>(Arrays.asList(plotBandsList)));
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setValueSuffix(" m/s");
        options.setTooltip(tooltip);

        HIPlotOptions plotOptions = new HIPlotOptions();
        plotOptions.setSpline(new HISpline());
        plotOptions.getSpline().setLineWidth(4);
        plotOptions.getSpline().setStates(new HIStates());
        plotOptions.getSpline().getStates().setHover(new HIHover());
        plotOptions.getSpline().getStates().getHover().setLineWidth(5);
        plotOptions.getSpline().setMarker(new HIMarker());
        plotOptions.getSpline().getMarker().setEnabled(false);
        plotOptions.getSpline().setPointInterval(3600000);
        plotOptions.getSpline().setPointStart(Date.UTC(2015, 6, 31, 0, 0, 0));
        options.setPlotOptions(plotOptions);
        //options.setSeries(new ArrayList<>(Arrays.asList(series1)));

        HINavigation navigation = new HINavigation();
        navigation.setMenuItemStyle(new HICSSObject());
        navigation.getMenuItemStyle().setFontSize("8px");
        options.setNavigation(navigation);

        chartView.setOptions(options);
    }

    private void setChartValue(GraphModel graphData) {
        HISeries series1 = new HISeries();
        series1.setName("Hestavollane");
        Number[] series1_data = new Number[]{0.2, 0.8, 0.8, 0.8, 1, 1.3, 1.5, 2.9, 1.9, 2.6, 1.6, 3, 4, 3.6, 4.5, 4.2, 4.5, 4.5, 4, 3.1, 2.7, 4, 2.7, 2.3, 2.3, 4.1, 7.7, 7.1, 5.6, 6.1, 5.8, 8.6, 7.2, 9, 10.9, 11.5, 11.6, 11.1, 12, 12.3, 10.7, 9.4, 9.8, 9.6, 9.8, 9.5, 8.5, 7.4, 7.6};
        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));

        HISeries series2 = new HISeries();
        series2.setName("Vik");
        Number[] series2_data = new Number[]{0, 0, 0.6, 0.9, 0.8, 0.2, 0, 0, 0, 0.1, 0.6, 0.7, 0.8, 0.6, 0.2, 0, 0.1, 0.3, 0.3, 0, 0.1, 0, 0, 0, 0.2, 0.1, 0, 0.3, 0, 0.1, 0.2, 0.1, 0.3, 0.3, 0, 3.1, 3.1, 2.5, 1.5, 1.9, 2.1, 1, 2.3, 1.9, 1.2, 0.7, 1.3, 0.4, 0.3};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));

        options.setSeries(new ArrayList<>(Arrays.asList(series1, series2)));

        chartView.redraw();
        Toast.makeText(this, "Added!! " , Toast.LENGTH_SHORT).show();
    }


    private void loadChart(GraphModel graphData) {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        try {
            for (int a = 0; a < graphData.getPrices().size(); a++) {
                float timeStamp = graphData.getPrices().get(a).get(0);
                float price = graphData.getPrices().get(a).get(1);
              /*  String time = java.text.DateFormat.getTimeInstance().format( timeStamp );
                float market_caps = graphData.getMarket_caps().get( a ).get( 1 );
                float total_volumes = graphData.getTotal_volumes().get( a ).get( 1 );*/
                try {
                    entries.add(new Entry(timeStamp, price, R.drawable.app_logo));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "null value: " + e.getLocalizedMessage());
        }

        chart.setDragEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(true);
        chart.animateX(1500);
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        Legend l = chart.getLegend();
        l.setEnabled(true);

        LineDataSet dataSet = new LineDataSet(entries, "Price in (" + HomeScreen.CURRENCY.toUpperCase() + ")"); // add entries to dataset
        dataSet.setColor(R.color.colorPrimaryDark);
        dataSet.setValueTextColor(R.color.colorAccent);
        dataSet.setCircleRadius(1f);
        dataSet.setCircleHoleRadius(0.2f);
        dataSet.setCircleColor(R.color.colorPrimaryDark);

        chart.setNoDataText("Loading...");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

    }

    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pair = (TextView) toolbar.findViewById(R.id.pairing);
        mCoinName = (TextView) toolbar.findViewById(R.id.name_coin);

        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                dialogSheet.show();
            }
        });
    }


    private void bottomDialog() {
        dialogSheet = new DialogSheet(this);

        // get the layout inflater
        LayoutInflater inflater = CoinDetailActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.coin_pair_recycler_layout, null);

        recyclerView = view.findViewById(R.id.pair_rec);
        loadPairData();

        dialogSheet.setTitle("Market")
                .setTitleTextSize(20) // In SP
                .setCancelable(true)
                .setView(view)
                .setPositiveButton("Dismiss", new DialogSheet.OnPositiveClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Your action
                    }
                })
                .setBackgroundColor(Color.BLACK) // Your custom background color
                .setButtonsColorRes(R.color.colorAccent);// ;You can use dialogSheetAccent style attribute instead

        dialogSheet.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        dialogSheet.setButtonsColor(getResources().getColor(R.color.colorPrimaryDark));
        dialogSheet.setButtonsColorRes(R.color.dark_grey);


        dialogSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void loadPairData() {
        progressBar.setVisibility(View.VISIBLE);
        exchange = new CoinExchange();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService uploadInterFace = retrofit.create(RetrofitService.class);

        Call<CoinExchange> call = uploadInterFace.getExchangeByCoinId(COIN_ID);

        call.enqueue(new Callback<CoinExchange>() {
            @Override
            public void onResponse(Call<CoinExchange> call, Response<CoinExchange> response) {

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    exchange = response.body();
                    pairMarketList = new ArrayList<>(Arrays.asList(exchange.getTickers()));
                    adapter = new PairMarketAdapter(pairMarketList, CoinDetailActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(CoinDetailActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CoinExchange> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CoinDetailActivity.this, "failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
