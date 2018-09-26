package com.example.astri.popularmovies.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.astri.popularmovies.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView logo = findViewById(R.id.app_logo);

        int splashTimeOut = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }, splashTimeOut);

        Animation myAnim = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        logo.startAnimation(myAnim);
    }
}

