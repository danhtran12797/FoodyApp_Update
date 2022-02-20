package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.module.payment.OnUpdateOrderListener;
import com.danhtran12797.thd.foodyapp.module.payment.PaymentManager;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFailedActivity extends AppCompatActivity implements View.OnClickListener, ILoading {

    private static final String TAG = "PaymentFailedActivity";

    private ConstraintLayout layout_payment_failed;
    private TextView txt_order_id;
    private TextView txt_amount;
    private TextView txt_delivery;
    private ImageView img_logo;
    private Button btnPaymentAgain;
    private Button btnPayment;
    private FrameLayout layout_container;
    private RotateLoading rotateLoading;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    private String type = "";
    private String order_id = "";
    private String request = "";
    private String delivery = "";

    private AddressShipping address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        initView();

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            order_id = intent.getStringExtra("order_id");
            request = intent.getStringExtra("request");
            delivery = intent.getStringExtra("delivery");
            address = (AddressShipping) intent.getSerializableExtra("addressShipping");
            double amount = intent.getDoubleExtra("amount", 0.0);
            double amount_usd = Math.ceil(amount / 23000);
            setDataUI(order_id, type, amount, amount_usd);
        }
    }

    private void initView() {
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_payment_failed = findViewById(R.id.layout_payment_failed);

        txt_amount = findViewById(R.id.txtAmount);
        txt_order_id = findViewById(R.id.txtOrderId);
        txt_delivery = findViewById(R.id.txtDelivery);
        img_logo = findViewById(R.id.imgLogo);
        btnPayment = findViewById(R.id.btnPayment);
        btnPaymentAgain = findViewById(R.id.btnPaymentAgain);

        btnPayment.setOnClickListener(this);
        btnPaymentAgain.setOnClickListener(this);
    }

    private void setDataUI(String order_id, String type, double amount, double amount_usd) {
        txt_order_id.setText(order_id);
        txt_amount.setText(decimalFormat.format(amount) + " VND ~ " + amount_usd + " USD");
        if (type.equals("2")) {
            txt_delivery.setText("Thẻ quốc tế");
            img_logo.setImageResource(R.drawable.logo_popular_credit);
        } else if (type.equals("3")) {
            txt_delivery.setText("Ví MoMo");
            img_logo.setImageResource(R.drawable.logo_momo);
        } else if (type.equals("4")) {
            txt_delivery.setText("Ví ZaloPay");
            img_logo.setImageResource(R.drawable.zalo_pay);
        }else {
            txt_delivery.setText("Ví VNPAY");
            img_logo.setImageResource(R.drawable.vn_pay);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPayment:
                JWTToken jwtToken = Ultil.getTokenPreference(this);
                new PaymentManager(this, this, jwtToken.getToken(), new OnUpdateOrderListener() {
                    @Override
                    public void orderSuccess() {
                        startActivity(new Intent(PaymentFailedActivity.this, OrderSuccessActivity.class));
                        finish();
                    }
                }).Update_Order(order_id, "1", "1", request, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
                break;
            case R.id.btnPaymentAgain:
                getOrder(order_id);
                break;
        }
    }

    private void getOrder(String order_id) {
        start_loading();
        DataService dataService = APIService.getService();
        Call<List<Order>> callback = dataService.GetOrder(Ultil.user.getId(), order_id);
        callback.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                stop_loading(true);
                ArrayList<Order> arrOrder = (ArrayList<Order>) response.body();
                Intent intent = new Intent(PaymentFailedActivity.this, PaymentActivity.class);
                intent.putExtra("order_detail", arrOrder.get(0));
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                stop_loading(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        if (!isConnect) {
            Ultil.show_snackbar(layout_payment_failed, null);
        }
    }
}
