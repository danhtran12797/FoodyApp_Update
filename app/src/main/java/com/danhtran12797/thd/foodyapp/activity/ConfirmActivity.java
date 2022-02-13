package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.ConfirmAdapter;
import com.danhtran12797.thd.foodyapp.adapter.OrderDetailAdapter;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.OrderDetail;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.module.payment.OnInsertOrderListener;
import com.danhtran12797.thd.foodyapp.module.payment.OnUpdateOrderListener;
import com.danhtran12797.thd.foodyapp.module.payment.PaymentManager;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.text.DecimalFormat;
import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;
import vn.momo.momo_partner.AppMoMoLib;
import vn.zalopay.sdk.MerchantReceiver;
import vn.zalopay.sdk.ZaloPaySDK;

import static com.danhtran12797.thd.foodyapp.module.payment.PaymentManager.REQUEST_CODE_BRAINTREE;

public class ConfirmActivity extends AppCompatActivity implements View.OnClickListener, ILoading, OnInsertOrderListener, OrderDetailAdapter.IOrderDetail {

    private static final String TAG = "ConfirmActivity";
    private static final int REQUEST_CODE_CHANGE_PAYMENT = 321;
    private static final int REQUEST_CODE_CHANGE_ADDRESS_SHIPPING = 100;
    private static final int REQUEST_CODE_ZALOPAY = 64207;

    private Toolbar toolbar;
    private Button btn_confirm;
    private TextView txt_total_all_price;
    private TextView txt_total_price;
    private TextView txt_transport;
    private RecyclerView recyclerView;
    private TextView txt_delivery;
    private TextView txt_payment;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_address;
    private TextView txt_request;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;

    private double money_transport = 0.0;
    private double amount_product = 0.0;
    private double amount = 0.0;
    private double amount_usd = 0.0;
    private String request_user = "";
    private AddressShipping address;
    private String delivery;
    private String payment;
    private Order order = null;

    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    JWTToken jwtToken = null;
    private PaymentManager paymentManager;
    MerchantReceiver reciver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm);

        reciver = new MerchantReceiver();
        intentFilter = new IntentFilter("vn.zalopay.sdk.ZP_ACTION");

        initView();
        initActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("order_detail")) {
                order = (Order) intent.getSerializableExtra("order_detail");
                payment = intent.getStringExtra("type_payment");
                delivery = order.getDelivery();
                txt_request.setText(order.getRequire());
            } else {
                delivery = intent.getStringExtra("id_delivery");
                payment = intent.getStringExtra("id_payment");
            }
        }

        jwtToken = Ultil.getTokenPreference(this);

        setViewDelivery(delivery);
        setViewPayment(payment);
        setViewAddressUser(getAddressUser(order));
        setAdapter(order);

        setAmount(order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(reciver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciver);
    }

    private double getAmountOrder(Order order) {
        double d = 0;
        for (OrderDetail detail : order.getOrderDetail()) {
            d += Double.parseDouble(detail.getPrice()) * Double.parseDouble(detail.getQuantityProduct());
        }
        return d;
    }

    private double getTotalPriceProduct() {
        double s = 0;
        for (ShopingCart shopingCart : Ultil.arrShoping) {
            s += shopingCart.getPrice() * shopingCart.getQuantity();
        }
        return s;
    }

    private void setAmount(Order order) {
        if (order != null) {
            amount_product = getAmountOrder(order);
        } else {
            amount_product = getTotalPriceProduct();
        }
        txt_total_price.setText(decimalFormat.format(amount_product) + " VNĐ");
        amount = amount_product + money_transport;
        amount_usd = Math.ceil(amount / 23000);
        txt_total_all_price.setText(decimalFormat.format(amount) + " VNĐ ~ " + amount_usd + " USD");
    }

    private AddressShipping getAddressUser(Order order) {
        if (order != null) {
            address = new AddressShipping(order.getName(), order.getAddress(), order.getPhone(), true);
            Ultil.arrAddressShipping = Ultil.getAddressShipping(this);
            if (Ultil.arrAddressShipping == null) {
                Ultil.arrAddressShipping = new ArrayList<>();
                Ultil.arrAddressShipping.add(address);
                Ultil.setAddressShipping(this);
            } else {
                for (AddressShipping addressShipping : Ultil.arrAddressShipping) {
                    if (addressShipping.getAddress().equals(address.getAddress()) && addressShipping.getName().equals(address.getName())) {
                        for (AddressShipping shipping : Ultil.arrAddressShipping) {
                            shipping.setCheck(false);
                        }
                        addressShipping.setCheck(true);
                        Ultil.setAddressShipping(this);
                        return address;
                    }
                }
                for (AddressShipping shipping : Ultil.arrAddressShipping) {
                    shipping.setCheck(false);
                }
                Ultil.arrAddressShipping.add(0, address);
                Ultil.setAddressShipping(this);
            }
        } else {
            for (AddressShipping addressShipping : Ultil.arrAddressShipping) {
                if (addressShipping.isCheck()) {
                    address = addressShipping;
                    break;
                }
            }
        }
        return address;
    }

    private void setViewAddressUser(AddressShipping address) {
        txt_name.setText(address.getName());
        txt_phone.setText(address.getPhone());
        txt_address.setText(address.getAddress());
    }

    private void setViewDelivery(String delivery) {
        if (delivery.equals("1")) {
            txt_delivery.setText("Giao hàng tiêu chuẩn(miễn phí)");
            money_transport = 0;
        } else {
            txt_delivery.setText("Shop giao nhanh(30.000 VNĐ)");
            txt_transport.setText("30,000 VNĐ");
            money_transport = 30000;
        }
    }

    private void setViewPayment(String payment) {
        if (payment.equals("1")) {
            txt_payment.setText("Thanh toán tiền mặt khi nhận hàng");
        } else if (payment.equals("2")) {
            txt_payment.setText("Thanh toán bắng thẻ quốc tế Visa, Master, JCB");
        } else if (payment.equals("3")) {
            txt_payment.setText("Thanh toán bằng ví MoMo");
        } else {
            txt_payment.setText("Thanh toán bằng ví ZaloPay");
        }
    }

    private void setAdapter(Order order) {
        RecyclerView.Adapter adapter = null;
        if (order != null) {
            adapter = new OrderDetailAdapter(this, (ArrayList<OrderDetail>) order.getOrderDetail());
        } else {
            adapter = new ConfirmAdapter(this, Ultil.arrShoping);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecorationvider);
    }

    private void initView() {
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar_confirm);
        btn_confirm = findViewById(R.id.btn_confirm);
        txt_total_all_price = findViewById(R.id.txt_total_price_product_confirm);
        txt_total_price = findViewById(R.id.txt_total_price_confirm);
        txt_transport = findViewById(R.id.txt_trans_confirm);
        recyclerView = findViewById(R.id.recyler_view_confirm);
        txt_delivery = findViewById(R.id.txt_delivery_confirm);
        txt_payment = findViewById(R.id.txt_payment_confirm);
        txt_name = findViewById(R.id.txt_name_confirm);
        txt_phone = findViewById(R.id.txt_phone_confirm);
        txt_address = findViewById(R.id.txt_address_confirm);
        txt_request = findViewById(R.id.txt_request);

        btn_confirm.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (Ultil.isNetworkConnected(this)) {
                    String order_id = String.valueOf(System.currentTimeMillis());
                    boolean update = false;
                    request_user = txt_request.getText().toString();
                    if (order != null) {
                        order_id = order.getId();
                        update = true;
                    }
                    paymentManager = new PaymentManager(this, this, this, request_user, amount, jwtToken.getToken(), address, delivery, payment, order_id, update);
                    if (payment.equals("1")) {
                        if (order != null)
                            new PaymentManager(this, this, jwtToken.getToken(), new OnUpdateOrderListener() {
                                @Override
                                public void orderSuccess() {
                                    status_order("1", order.getId());
                                }
                            }).Update_Order(order.getId(), payment, "1", request_user, delivery, address.getPhone(), address.getAddress(), Ultil.user.getEmail(), address.getName());
                        else
                            paymentManager.insert_order("1");
                    } else if (payment.equals("2")) {
                        paymentManager.getTokenBraintree();
                    } else if (payment.equals("3")) {
                        paymentManager.requestPaymentMoMo(amount);
                    } else {
                        ZaloPaySDK.getInstance().initWithAppId(553);
                        paymentManager.paymentZaloPay();
                    }
                } else {
                    stop_loading(false);
                }
                break;
        }
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
            Ultil.show_snackbar(btn_confirm, btn_confirm);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1 && data != null) {
            if (data.getIntExtra("status", -1) == 0) {
                //TOKEN IS AVAILABLE
                Log.d("AAA", "message: " + "Get token " + data.getStringExtra("message"));
                String token = data.getStringExtra("data"); //Token response
                String phoneNumber = data.getStringExtra("phonenumber");
                String env = data.getStringExtra("env");
                if (env == null) {
                    env = "app";
                }

                if (token != null && !token.equals("")) {
                    // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                    // IF Momo topup success, continue to process your order
                    Log.d("AAA", "token: " + token);
                    Log.d("AAA", "phoneNumber: " + phoneNumber);
                    Log.d(TAG, "env: " + env);
                    paymentManager.paymentProcessing(token, phoneNumber);
                } else {
                    Log.d(TAG, "message1: " + this.getString(R.string.not_receive_info));
                    paymentManager.insert_order("5");
                }
            } else if (data.getIntExtra("status", -1) == 1) {
                //TOKEN FAIL
                String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                Log.d(TAG, "message2: " + message);
                paymentManager.insert_order("5");
            } else if (data.getIntExtra("status", -1) == 2) {
                //TOKEN FAIL
                Log.d(TAG, "message3: " + this.getString(R.string.not_receive_info));
                paymentManager.insert_order("5");
            } else {
                //TOKEN FAIL
                Log.d(TAG, "message4: " + this.getString(R.string.not_receive_info));
                paymentManager.insert_order("5");
            }
        } else if (requestCode == REQUEST_CODE_BRAINTREE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                    String strNone = nonce.getNonce();

                    Log.d(TAG, "strNone: " + strNone);
                    paymentManager.sendPaymentBraintree(String.valueOf(amount_usd), strNone);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult braintree: user canceled");
                paymentManager.insert_order("5");
            }
        } else if (requestCode == REQUEST_CODE_CHANGE_PAYMENT && resultCode == RESULT_OK && data != null) {
            payment = data.getStringExtra("type_payment");
            setViewPayment(payment);
        } else if (requestCode == REQUEST_CODE_ZALOPAY && resultCode == RESULT_OK && data != null) {
            ZaloPaySDK.getInstance().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_CODE_CHANGE_ADDRESS_SHIPPING && resultCode == RESULT_OK && data != null) {
            address = Ultil.arrAddressShipping.get(data.getIntExtra("position_address_shipping", -1));
            if (order != null) {
                order.setAddress(address.getAddress());
                order.setPhone(address.getPhone());
                order.setName(address.getName());
            }
            setViewAddressUser(address);
        }
    }

    @Override
    public void status_order(String status, String order_id) {
        if (status.equals("1")) {
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        } else {
            Intent intent = new Intent(this, PaymentFailedActivity.class);
            intent.putExtra("order_id", order_id);
            intent.putExtra("amount", amount);
            intent.putExtra("type", payment);
            intent.putExtra("request", request_user);
            intent.putExtra("addressShipping", address);
            intent.putExtra("delivery", delivery);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            CustomIntent.customType(this, "fadein-to-fadeout");
        }
    }

    public void ChangePayment(View view) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("type_payment", payment);
        intent.putExtra("amount", amount_product);
        intent.putExtra("delivery", delivery);
        startActivityForResult(intent, REQUEST_CODE_CHANGE_PAYMENT);
    }

    public void ChangeDelivery(View view) {
        String[] items = new String[]{
                "Giao hàng tiêu chuẩn(miễn phí)",
                "Shop giao nhanh(30.000 VNĐ)"
        };
        int checkItem = Integer.parseInt(delivery) - 1;
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình thức giao hàng")
                .setCancelable(false)
                .setSingleChoiceItems(items, checkItem, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        delivery = String.valueOf(selectedPosition + 1);
                        setViewDelivery(delivery);
                        setAmount(order);
                    }
                })
                .show();
    }

    @Override
    public void itemClick(int position, View view) {
        new PaymentManager(this, this).getProduct(order.getOrderDetail().get(position).getIdProduct());
    }

    public void ChangeAddressShipping(View view) {
        Intent intent = new Intent(this, AddressOrderActivity.class);
        intent.putExtra("change_address_shipping", true);
        startActivityForResult(intent, REQUEST_CODE_CHANGE_ADDRESS_SHIPPING);
    }
}
