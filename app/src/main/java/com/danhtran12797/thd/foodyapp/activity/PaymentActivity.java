package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.OrderDetail;

import java.text.DecimalFormat;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    private Toolbar toolbar;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    private TextView txtAmount;
    private TextView txtTrans;
    private TextView txtTotalAmount;

    Order order = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initView();
        initActionBar();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("type_payment")) {
                String payment = intent.getStringExtra("type_payment");
                double amount = intent.getDoubleExtra("amount", 0.0);
                String delivery = intent.getStringExtra("delivery");
                checkRadio(payment);
                setAmountUI(amount, delivery);
            } else {
                order = (Order) intent.getSerializableExtra("order_detail");
                setAmountUI(getAmountOrder(), order.getDelivery());
            }
        }

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(PaymentActivity.this).inflate(R.layout.layout_wallet_payment, null);
                RadioGroup radioGroup = view.findViewById(R.id.radioWallet);
                if (!radioButton3.getTag().equals("4")) {
                    radioGroup.check(R.id.radio_momo);
                } else {
                    radioGroup.check(R.id.radio_zalo);
                }

                AlertDialog dialog = new AlertDialog.Builder(PaymentActivity.this).create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setView(view);
                dialog.setCancelable(false);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton1 = view.findViewById(id);
                        if (radioButton1.getTag().equals("3")) {
                            radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_momo, 0, 0, 0);
                            radioButton3.setText("Ví MoMo");
                            radioButton3.setTag("3");
                        } else if (radioButton1.getTag().equals("4")) {
                            radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_zalo, 0, 0, 0);
                            radioButton3.setText("Ví ZaloPay");
                            radioButton3.setTag("4");
                        } else {
                            radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_vnpay, 0, 0, 0);
                            radioButton3.setText("Ví VNPAY");
                            radioButton3.setTag("5");
                        }
                    }
                });
                dialog.show();
                Button yesButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                yesButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_button_yes_pay));
            }
        });
    }

    private void checkRadio(String payment) {
        if (payment.equals("1")) {
            radioButton1.setChecked(true);
        } else if (payment.equals("2")) {
            radioButton2.setChecked(true);
        } else {
            radioButton3.setChecked(true);
            if (payment.equals("3")) {
                radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_momo, 0, 0, 0);
                radioButton3.setText("Ví MoMo");
                radioButton3.setTag("3");
            } else if (payment.equals("4")) {
                radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_zalo, 0, 0, 0);
                radioButton3.setText("Ví ZaloPay");
                radioButton3.setTag("4");
            }
            else {
                radioButton3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_radio_vnpay, 0, 0, 0);
                radioButton3.setText("Ví VNPAY");
                radioButton3.setTag("5");
            }
        }
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

    private void initView() {
        toolbar = findViewById(R.id.toolbar_payment);
        txtAmount = findViewById(R.id.txtAmount);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtTrans = findViewById(R.id.txtTrans);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radio_one);
        radioButton2 = findViewById(R.id.radio_two);
        radioButton3 = findViewById(R.id.radio_three);
    }

    private double getAmountOrder() {
        double d = 0;
        for (OrderDetail detail : order.getOrderDetail()) {
            d += Double.parseDouble(detail.getPrice()) * Double.parseDouble(detail.getQuantityProduct());
        }
        return d;
    }

    private void setAmountUI(double amount, String delivery) {
        double amount_trans = 0;
        if (!delivery.equals("1")) {
            amount_trans = 30000;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        double amountTotal = amount + amount_trans;
        double amount_usd = Math.ceil(amount / 23000);
        txtAmount.setText(decimalFormat.format(amount) + " VNĐ");
        txtTotalAmount.setText(decimalFormat.format(amountTotal) + " VNĐ ~ " + amount_usd + " USD");
        txtTrans.setText(decimalFormat.format(amount_trans) + " VNĐ");
    }

    public void Continue(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        if (radioId != -1) {
            RadioButton radioButton = findViewById(radioId);
            String payment = (String) radioButton.getTag();
            if (order != null) {
                Intent intent = new Intent(this, ConfirmActivity.class);
                intent.putExtra("order_detail", order);
                intent.putExtra("type_payment", payment);
                startActivity(intent);
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("type_payment", payment);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } else {
            Toast.makeText(this, "Vui lòng chọn hình thức thanh toán", Toast.LENGTH_SHORT).show();
        }
    }
}
