package com.example.gossip.adaptor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.gossip.R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            //showing splashscreen with a timer //

            @Override
            public void run() {
                //this is executed once the timer is over//

                Intent i = new Intent(SplashScreen.this, com.example.gossip.Root.class);
                startActivity(i);
                finish();

            }
        },2000);
    }
}