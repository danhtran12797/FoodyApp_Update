package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;

import maes.tech.intentanim.CustomIntent;

public class PayActivity extends AppCompatActivity {

    private static final String TAG = "PayActivity";

    private Toolbar toolbar;
    private RadioGroup radioGroupPayment;
    private RadioGroup radioGroupDelivery;
    private RadioButton radioButton3;
    private Button btn_pay_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        initView();
        initActionBar();

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(PayActivity.this).inflate(R.layout.layout_wallet_payment, null);
                RadioGroup radioGroup = view.findViewById(R.id.radioWallet);
                if (!radioButton3.getTag().equals("4")) {
                    radioGroup.check(R.id.radio_momo);
                } else {
                    radioGroup.check(R.id.radio_zalo);
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(PayActivity.this);
                dialog.setView(view);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton1 = view.findViewById(id);
                        if (radioButton1.getTag().equals("3")) {
                            radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_momo, 0, 0, 0);
                            radioButton3.setText("Ví MoMo");
                            radioButton3.setTag("3");
                        } else {
                            radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_zalo, 0, 0, 0);
                            radioButton3.setText("Ví ZaloPay");
                            radioButton3.setTag("4");
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_pay);
        btn_pay_next = findViewById(R.id.btn_pay_next);
        radioGroupDelivery = findViewById(R.id.radio_group_delivery);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
        radioButton3 = findViewById(R.id.radio_payment_wallet);

        btn_pay_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id_delivery_radio = radioGroupDelivery.getCheckedRadioButtonId();
                int id_payment_radio = radioGroupPayment.getCheckedRadioButtonId();
                if (id_delivery_radio != -1 && id_payment_radio != -1) {
                    RadioButton radioButtonDelivery = findViewById(id_delivery_radio);
                    RadioButton radioButtonPayment = findViewById(id_payment_radio);

                    Intent intent = new Intent(PayActivity.this, ConfirmActivity.class);
                    intent.putExtra("id_delivery", (String) radioButtonDelivery.getTag());
                    intent.putExtra("id_payment", (String) radioButtonPayment.getTag());
                    startActivity(intent);

                    CustomIntent.customType(PayActivity.this, "fadein-to-fadeout");
                } else {
                    if (id_delivery_radio == -1) {
                        Toast.makeText(PayActivity.this, "Vui lòng chọn hình thức giao hàng", Toast.LENGTH_SHORT).show();
                    }
                    if (id_payment_radio == -1) {
                        Toast.makeText(PayActivity.this, "Vui lòng chọn hình thức thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }
}
