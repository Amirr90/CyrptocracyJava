package com.e.cryptocracy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    CountDownTimer myCountdownTimer;
    FirebaseFirestore db;
    ImageView mLogoImage;
    FirebaseAuth auth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        auth = FirebaseAuth.getInstance();
        final Animation aniFade = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.fade_in );
        mLogoImage = (ImageView) findViewById( R.id.app_logo );
        mLogoImage.startAnimation( aniFade );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );

        startTimer();

    }

    private void startTimer() {
        myCountdownTimer = new CountDownTimer( 3000, 1000 ) {

            public void onTick(long millisUntilFinished) {
                // Kick off your AsyncTask here
            }

            public void onFinish() {
                progressBar.setVisibility( View.VISIBLE );
                checkLoginStatus();

            }
        }.start();
    }

    private void checkLoginStatus() {

        if (auth.getCurrentUser() != null) {
            progressBar.setVisibility( View.GONE );
            sendToMainActivity();
        } else {
            showLoginDialog();
        }
    }

    private void showLoginDialog() {
        ProgressDialog dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading" );
        dialog.setMessage( "Please wait" );
        dialog.setCancelable( false );
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build() );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders( providers )
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html" )
                        //.setLogo(R.drawable.logoo)      // Set logo drawable
                        .setTheme( R.style.AppTheme_NoActionBar )
                        .build(),
                10 );
    }

    private void sendToMainActivity() {
        startActivity( new Intent( SplashScreen.this,HomeScreen.class ) );
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myCountdownTimer != null) {
            myCountdownTimer.cancel();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            IdpResponse response = IdpResponse.fromResultIntent( data );

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    progressBar.setVisibility( View.GONE );
                    Toast.makeText( this, "sign in successfully", Toast.LENGTH_SHORT ).show();
                    sendToMainActivity();

                }

            } else {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( this, "Sign in failed ", Toast.LENGTH_SHORT ).show();
                Log.e( TAG, "onActivityResult: "+response.getError().getLocalizedMessage()  );
                showLoginDialog();
            }
        }
    }

}

