package com.e.cryptocracy;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.e.cryptocracy.Adapter.ViewPagerAdapter;
import com.e.cryptocracy.Fragments.CoinFragment;
import com.e.cryptocracy.Fragments.ExchangeFragment;
import com.e.cryptocracy.Fragments.FavouriteFragment;
import com.e.cryptocracy.interfaces.onLoadMoreInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends AppCompatActivity {
    static public TextView titleText;
    static RelativeLayout mLoadingLayout, mDataLayout;
    public static String CURRENCY = "inr";
    public static String SORT_ORDER = "market_cap_desc";
    public static int PER_PAGE = 100;
    public int CURRENT_PAGE = 1;
    AppBarLayout appBarLayout;
    public static TextView mChangeCurrencyText;
    public static ImageView mFilterIcon;
    public static ImageView mSearchIcon;
    public ImageView mSettingsIcon;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private final String TAG = "HomeScreen";
    public int SELECTED_ITEM = 3;
    FrameLayout adContainerView;
    AdView adView;
    AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        appBarLayout = findViewById(R.id.app_bar);
        mChangeCurrencyText = appBarLayout.findViewById(R.id.change_currency);
        mFilterIcon = appBarLayout.findViewById(R.id.filter_icon);
        mSettingsIcon = appBarLayout.findViewById(R.id.setting);
        mSearchIcon = appBarLayout.findViewById(R.id.search_icon);
        titleText = findViewById(R.id.title_text);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mDataLayout = findViewById(R.id.data_layout);
        adContainerView = findViewById(R.id.ad_view_container);

        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id_test));
        adContainerView.addView(adView);

        initAds();
        setCurrencyText();

        setFragment();


        mChangeCurrencyText.setOnClickListener(view -> changeCurrencyDialog());

        mSearchIcon.setOnClickListener(view -> startActivity(new Intent(HomeScreen.this, SearchActivity.class)));

        mSettingsIcon.setOnClickListener(view -> {
        });

        mFilterIcon.setOnClickListener(view -> showFilterDialog());

        FirebaseMessaging.getInstance().subscribeToTopic("priceAlert");

    }

    private void initAds() {
        MobileAds.initialize(this, initializationStatus -> {
            setUpAds();
        });
    }

    private void setUpAds() {

        loadBanner();
    }


    private void loadBanner() {
        adRequest =
                new AdRequest.Builder()
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
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

    private void showFilterDialog() {

        final String[] items_toShow = {"Volume Ascending", "Volume Descending", "Market Cap Ascending", "Market Cap Descending", "Gecko Ascending", "Gecko Descending"};
        final String[] items = getResources().getStringArray(R.array.filter_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);

        // Set the dialog title
        builder.setTitle("Choose One")
                .setSingleChoiceItems(items_toShow, SELECTED_ITEM, (arg0, arg1) -> {
                })
                .setPositiveButton("CHANGE", (dialog, id) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    SORT_ORDER = items[selectedPosition];
                    CURRENT_PAGE = 1;
                    SELECTED_ITEM = selectedPosition;
                    String FIELD = "sort_order";
                    changeToDatabase(SORT_ORDER, FIELD);
                })

                .setNegativeButton("CLEAR ALL", (dialog, id) -> {
                    // removes the dialog from the screen

                })

                .show();

    }


    private void setCurrencyText() {
        final Map<String, Object> map = new HashMap<>();
        map.put("currency", "inr");
        map.put("sort_order", "market_cap_desc");
        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot snapshot = task.getResult();
                            CURRENCY = snapshot.getString("currency");
                            //SORT_ORDER = snapshot.getString( "sort_order" );
                            Log.d(TAG, "onComplete: " + CURRENCY);
                            Log.d(TAG, "uid: " + auth.getCurrentUser().getUid());
                            if (CURRENCY != null)
                                mChangeCurrencyText.setText(CURRENCY.toUpperCase());
                        } else {
                            db.collection("users")
                                    .document(auth.getCurrentUser().getUid())
                                    .set(map);
                            mChangeCurrencyText.setText(R.string.inr);
                        }
                    }
                });
    }

    public void changeCurrencyDialog() {


        final String[] items = getResources().getStringArray(R.array.currency_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
        builder.setTitle("Select your currency");
        builder.setItems(items, (dialog, item) -> {
            CURRENCY = items[item].toLowerCase();
            CURRENT_PAGE = 1;
            mChangeCurrencyText.setText(CURRENCY.toUpperCase());
            String FIELD = "currency";
            changeToDatabase(CURRENCY, FIELD);
            dialog.dismiss();

        }).show();
    }


    private void changeToDatabase(String currency, String FIELD) {
        Map<String, Object> updateCurrencyMap = new HashMap<>();
        updateCurrencyMap.put(FIELD, currency);
        if (auth.getCurrentUser() != null) {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .update(updateCurrencyMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                        } else {
                            showLoadingLayout(false);
                            Log.d(TAG, "ChangeCurrencyListner: " + task.getException());
                            Toast.makeText(HomeScreen.this, "please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void setFragment() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.view_pager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.Addfragment(new CoinFragment(HomeScreen.this, onLoadMoreInterface), "Market");
        pagerAdapter.Addfragment(new FavouriteFragment(HomeScreen.this, onLoadMoreInterface), "Favourite");
        pagerAdapter.Addfragment(new ExchangeFragment(HomeScreen.this, onLoadMoreInterface), "Exchanges");

        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static void showLoadingLayout(boolean visibility) {
        mLoadingLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mDataLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SORT_ORDER = "market_cap_desc";
        CURRENT_PAGE = 1;
    }


    public onLoadMoreInterface onLoadMoreInterface = new onLoadMoreInterface() {
        @Override
        public void onLoadMore(Object o) {
            if (null != adView && null != adRequest) {
                // Step 5 - Start loading the ad in the background.
                adView.loadAd(adRequest);
            }
        }
    };
}
