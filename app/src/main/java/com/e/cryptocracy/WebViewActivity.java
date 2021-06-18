package com.e.cryptocracy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_web_view );

        if (getIntent().hasExtra( "url" )){
            url=getIntent().getStringExtra( "url" );
            if (url!=null){
                WebView theWebPage = new WebView(this);
                theWebPage.getSettings().setJavaScriptEnabled(true);
                theWebPage.getSettings().setPluginState( WebSettings.PluginState.ON);
                setContentView(theWebPage);
                theWebPage.loadUrl( url );
            }
        }
    }
}
