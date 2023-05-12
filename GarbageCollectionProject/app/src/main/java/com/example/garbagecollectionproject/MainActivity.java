package com.example.garbagecollectionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In your app's main activity or launcher activity, check for the authentication token
        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String authToken = prefs.getString("auth_token", null);
        if (authToken != null) {
            Log.i("TAG","Already Logged In! ?? "+authToken);
            String[] details=authToken.split("/");
            for(int i=0;i<details.length;i++){
                System.out.println(i+","+details[i]);
            }
            if(details[1].equals("User")){
                Intent intent=new Intent(MainActivity.this, user_main.class);
                startActivity(intent);
            }else if(details[1].equals("Collector")){
                Intent intent=new Intent(MainActivity.this, collector_main.class);
                startActivity(intent);
            }else{
                Intent intent=new Intent(MainActivity.this, admin_main.class);
                startActivity(intent);
            }

        } else {
        }

        Button loginButton=findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, LoginActivitiy.class);
                startActivity(intent);
            }
        });

        Button registerButton=findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}