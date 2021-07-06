package com.example.spacegram;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (haveNetwork()) {

                    Intent homeIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(homeIntent);
                    finish();


                } else if (!haveNetwork()) {
                    Toast.makeText(MainActivity.this, "Network Connection is not available!!", Toast.LENGTH_SHORT).show();
                    Intent home = new Intent(MainActivity.this, Error.class);
                    startActivity(home);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);


    }

    private Boolean haveNetwork() {

        boolean have_WIFI = false;
        boolean have_MobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkinfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkinfo) {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected()) {
                    have_WIFI = true;
                }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected()) {
                    have_MobileData = true;
                }

        }
        return have_MobileData || have_WIFI;

    }

}