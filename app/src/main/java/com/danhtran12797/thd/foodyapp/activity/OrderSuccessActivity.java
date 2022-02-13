package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.danhtran12797.thd.foodyapp.R;

public class OrderSuccessActivity extends AppCompatActivity {

    Button btn_seen_order;
    Button btn_next_shop;
    TextView txt_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_order);

        btn_next_shop = findViewById(R.id.btn_next_shoping_infor_order);
        btn_seen_order = findViewById(R.id.btn_seen_order);
        txt_payment = findViewById(R.id.txt_payment);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_payment_result);
        txt_payment.startAnimation(animation);

        btn_next_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderSuccessActivity.this, MainActivity.class));
                finish();
            }
        });

        btn_seen_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderSuccessActivity.this, OrderActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
