package com.e.cryptocracy.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.e.cryptocracy.R;
import com.e.cryptocracy.adapters.TweetAdapter;

public class TweetsActivity extends AppCompatActivity {
    private static final String TAG = "TweetsActivity";
    /*
        @Inject
        AppViewModel viewModel;*/
    TweetAdapter tweetAdapter;
    String str;
    RecyclerView tweetRec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);
        getSupportActionBar().setTitle("All Tweets");
    }

    /* @Override
     protected void onStart() {
         super.onStart();

         tweetRec = findViewById(R.id.recAllCoinNews);
         tweetAdapter = new TweetAdapter();
         tweetRec.setHasFixedSize(true);
         tweetRec.setAdapter(tweetAdapter);

         if (getIntent() == null) {
             return;
         }
         str = getIntent().getStringExtra("str");
         viewModel.tweetList(str).observe(this, tweetModels -> {
             Log.d(TAG, "onCreate: tweetModels " + tweetModels.size());
             tweetAdapter.submitList(tweetModels);
         });
     }
 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}