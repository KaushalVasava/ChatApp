package com.kmv.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               //startActivity(new Intent(SplashScreenActivity.this, PhoneNumberActivity.class));
                FirebaseAuth auth=FirebaseAuth.getInstance();

                if(auth.getInstance().getCurrentUser()==null){
                    Log.d("P","if");
                    Intent intent=new Intent(SplashScreenActivity.this,PhoneNumberActivity.class);
                    Log.d("P","name : ");

                    startActivity(intent);
                }
                else{
                    Log.d("P","else");

                    Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                    FirebaseUser user = auth.getInstance().getCurrentUser();
                    Log.d("P","phone no : "+user.getPhoneNumber());
                    //   Log.d("P","name : "+user.getPhotoUrl());
                    startActivity(intent);
                }
                finish();
                //finish();
            }
        },1000);
    }
}