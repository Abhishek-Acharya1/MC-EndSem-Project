package com.example.garbagecollectionproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class splash extends AppCompatActivity {
    LottieAnimationView truck, can;
    TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        truck = findViewById(R.id.truck);
        can = findViewById(R.id.can);
        txtName = findViewById(R.id.txtName);
        Intent iGet = new Intent(splash.this, MainActivity.class);
        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        Animation move1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move1);
//        Animation move2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move2);
//        can.setAnimation(fade_in);
//        truck.setAnimation(fade_in);
        txtName.setAnimation(fade_in);
        can.setAnimation(move);

//        truck.setAnimation(move2);
        truck.setAnimation(move1);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                truck.setAnimation(move1);
//            }
//        }, 1500);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(iGet);
                finish();
            }
        }, 3000);
    }
}