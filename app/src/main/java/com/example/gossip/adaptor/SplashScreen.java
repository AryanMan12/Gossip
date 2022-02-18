package com.example.gossip.adaptor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gossip.R;

public class SplashScreen extends AppCompatActivity {
ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.gossip.R.layout.activity_splash_screen);
        logo = findViewById(R.id.imageView8);
        animateLogo();
        new Handler().postDelayed(new Runnable() {
            //showing splashscreen with a timer //
            @Override
            public void run() {
                //this is executed once the timer is over//

                Intent i = new Intent(SplashScreen.this, com.example.gossip.Login.class);
                startActivity(i);
                finish();

            }
        },2000);
    }

    private void animateLogo() {
        Animation fadingInAnimation = AnimationUtils.loadAnimation(this,com.example.gossip.R.anim.fade_in);
        fadingInAnimation.setDuration(2000);
        logo.startAnimation(fadingInAnimation);
    }
}