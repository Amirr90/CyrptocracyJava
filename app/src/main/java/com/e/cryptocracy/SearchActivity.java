package com.e.cryptocracy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.e.cryptocracy.Adapter.SearchCoinHolder;
import com.e.cryptocracy.Interface.RetrofitService;
import com.e.cryptocracy.Model.CoinModal;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    MaterialSearchBar searchBar;
    SearchCoinHolder mSearchAdapter;
    ProgressBar progressBar;
    List<CoinModal> data2;
    RecyclerView mSearchRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setToolbar( toolbar );

        searchBar = (MaterialSearchBar) toolbar.findViewById( R.id.searchBar );
        progressBar = (ProgressBar) findViewById( R.id.progressBar2 );
        mSearchRec=(RecyclerView)findViewById( R.id.search_rec );
        mSearchRec.setLayoutManager( new LinearLayoutManager( this ) );
        data2 = new ArrayList<>();

        setSearchCoin();
        mSearchAdapter = new SearchCoinHolder( data2, this  );
        mSearchRec.setAdapter( mSearchAdapter );

        searchBar.addTextChangeListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                progressBar.setVisibility( View.VISIBLE );
                //Toast.makeText( SearchActivity.this, charSequence.toString(), Toast.LENGTH_SHORT ).show();
                filter( charSequence.toString() );

            }

            @Override
            public void afterTextChanged(Editable editable) {
                progressBar.setVisibility( View.VISIBLE );
               // Toast.makeText( SearchActivity.this, editable.toString(), Toast.LENGTH_SHORT ).show();
                filter( editable.toString() );
            }
        } );


    }


    private void setSearchCoin() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://api.coingecko.com/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();
        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<List<CoinModal>> call = uploadInterFace.getSearchCoin();
        call.enqueue( new Callback<List<CoinModal>>() {
            @Override
            public void onResponse(Call<List<CoinModal>> call, Response<List<CoinModal>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                data2 = response.body();
                mSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<CoinModal>> call, Throwable t) {

                Toast.makeText( SearchActivity.this, "fail" + t.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }


    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        //getSupportActionBar().setTitle( id );


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void filter(String text) {
        List<CoinModal> temp = new ArrayList();
        if (!data2.isEmpty()) {
            for (CoinModal d : data2) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getName().contains( text ) || d.getSymbol().contains( text ) || d.getId().contains( text )) {
                    temp.add( d );
                }
            }
            //update recyclerView
            mSearchAdapter.updateList( temp, progressBar );
        }
    }
}
