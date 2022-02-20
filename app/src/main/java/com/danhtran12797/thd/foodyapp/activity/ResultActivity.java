package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.danhtran12797.thd.foodyapp.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Log.d("III", "onCreate: ResultActivity");
        startActivity(new Intent(this, CategoryActivity.class));
    }
}
