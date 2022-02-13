package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.restorePrefData;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (restorePrefData(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        }
    }
}
