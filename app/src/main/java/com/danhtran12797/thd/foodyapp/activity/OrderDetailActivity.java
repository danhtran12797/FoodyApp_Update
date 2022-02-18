package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.OrderDetailAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.OrderDetail;
import com.danhtran12797.thd.foodyapp.module.payment.OnCancelOrderListener;
import com.danhtran12797.thd.foodyapp.module.payment.OnUpdateOrderListener;
import com.danhtran12797.thd.foodyapp.module.payment.PaymentManager;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.victor.loading.rotate.RotateLoading;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity implements OrderDetailAdapter.IOrderDetail, ILoading, View.OnClickListener {

    private static final String TAG = "OrderDetailActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txt_code;
    private TextView txt_date;
    private TextView txt_state;
    private TextView txt_address;
    private TextView txt_phone;
    private TextView txt_name_user;
    private TextView txt_delivery;
    private TextView txt_payment;
    private TextView txt_total;
    private TextView txt_money_trans;
    private TextView txt_total_all;
    private Button btn_cancle;
    private Button btn_payment_again;

    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private RelativeLayout layout_order_detail;

    private Order order;
    private double money_transport = 0;
    private double all_money = 0;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    boolean update = false;

    private BottomSheetDialog bottomSheetDialog;
    private RadioGroup radioGroup;
    private ImageView imgClose;
    private Button btnCancleOrder;
    private View viewBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("order_detail");

        initView();
        initActionBar();

        if (Ultil.isNetworkConnected(this)) {
            setStatusView();
            setDataView();
            setViewDelivery();
            setViewPayment();
            txt_total.setText(decimalFormat.format(getTotalPriceProduct()) + " VNĐ");
            setAllMoney();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    private double getTotalPriceProduct() {
        double s = 0;
        for (OrderDetail orderDetail : order.getOrderDetail()) {
            s += Double.parseDouble(orderDetail.getPrice()) * Integer.parseInt(orderDetail.getQuantityProduct());
        }
        return s;
    }

    private void setAllMoney() {
        all_money = getTotalPriceProduct() + money_transport;
        txt_total_all.setText(decimalFormat.format(all_money) + " VNĐ");
    }

    private void setStatusView() {
        if (order.getStatus().equals("1")) {
            txt_state.setText("Đang xử lý");
            btn_cancle.setVisibility(View.VISIBLE);
        } else if (order.getStatus().equals("2")) {
            txt_state.setText("Giao hàng thành công");
        } else if (order.getStatus().equals("4")) {
            txt_state.setText("Đã xác nhận");
        } else if (order.getStatus().equals("5")) {
            txt_state.setText("Chờ thanh toán");
            btn_payment_again.setVisibility(View.VISIBLE);
            btn_cancle.setVisibility(View.VISIBLE);
        } else if (order.getStatus().equals("6")) {
            txt_state.setText("Đang vận chuyển");
        } else {
            txt_state.setText("Đã hủy");
            btn_payment_again.setVisibility(View.GONE);
            btn_cancle.setVisibility(View.GONE);
        }
    }

    private void setDataView() {
        layout_container.setVisibility(View.GONE);
        OrderDetailAdapter adapter = new OrderDetailAdapter(OrderDetailActivity.this, (ArrayList<OrderDetail>) order.getOrderDetail());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(OrderDetailActivity.this, linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecorationvider);

        txt_date.setText(order.getDate());
        txt_code.setText(order.getId());
        txt_name_user.setText(order.getName());
        txt_phone.setText(order.getPhone());
        txt_address.setText(order.getAddress());
    }

    private void setViewDelivery() {
        String delivery = order.getDelivery();
        if (delivery.equals("1")) {
            txt_delivery.setText("Giao hàng tiêu chuẩn(miễn phí)");
        } else {
            txt_delivery.setText("Shop giao nhanh(30.000 VNĐ)");
            txt_money_trans.setText("30,000 VNĐ");
            money_transport = 30000;
        }
    }

    private void setViewPayment() {
        String payment = order.getPayment();
        if (payment.equals("1")) {
            txt_payment.setText("Thanh toán tiền mặt khi nhận hàng");
        } else if (payment.equals("2")) {
            txt_payment.setText("Thanh toán bằng thẻ quốc tế Visa, Master, JCB");
        } else if (payment.equals("3")) {
            txt_payment.setText("Thanh toán bằng ví MoMo");
        } else if (payment.equals("4")) {
            txt_payment.setText("Thanh toán bằng ví ZaloPay");
        } else {
            txt_payment.setText("Thanh toán bằng ví VNPAY");
        }
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_order_detail = findViewById(R.id.layout_order_detail);
        layout_container.setVisibility(View.GONE);

        toolbar = findViewById(R.id.toolbar_order_detail);
        recyclerView = findViewById(R.id.recyler_view_order_detail);
        txt_code = findViewById(R.id.txt_code_order_detail);
        txt_date = findViewById(R.id.txt_date_order_detail);
        txt_state = findViewById(R.id.txt_state_order_detail);
        txt_name_user = findViewById(R.id.txt_name_user_order_detail);
        txt_phone = findViewById(R.id.txt_phone_user_order_detail);
        txt_address = findViewById(R.id.txt_address_user_order_detail);
        txt_delivery = findViewById(R.id.txt_delivery_order_detail);
        txt_payment = findViewById(R.id.txt_payment_order_detail);
        txt_total = findViewById(R.id.txt_total_price_order_detail);
        txt_total_all = findViewById(R.id.txt_total_price_product_order_detail);
        txt_money_trans = findViewById(R.id.txt_trans_order_detail);
        btn_cancle = findViewById(R.id.btn_cancle_order_detail);
        btn_payment_again = findViewById(R.id.btn_paymen_again);

        // bottom sheet dialog
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        viewBottomSheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_cancel_order, null);
        radioGroup = viewBottomSheet.findViewById(R.id.radio_group_cancle_order);
        radioGroup.check(R.id.radio_button4);
        imgClose = viewBottomSheet.findViewById(R.id.img_close_bottom_sheet);
        btnCancleOrder = viewBottomSheet.findViewById(R.id.btn_cancle_order);

        bottomSheetDialog.setContentView(viewBottomSheet);

        btnCancleOrder.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_payment_again.setOnClickListener(this);
        imgClose.setOnClickListener(this);
    }

    @Override
    public void itemClick(int position, View view) {
        new PaymentManager(this, this).getProduct(order.getOrderDetail().get(position).getIdProduct());
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
            Ultil.show_snackbar(layout_order_detail, null);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("update1", update);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void cancleOrder() {
        JWTToken jwtToken = Ultil.getTokenPreference(OrderDetailActivity.this);
        new PaymentManager(OrderDetailActivity.this, OrderDetailActivity.this, jwtToken.getToken(), new OnUpdateOrderListener() {
            @Override
            public void orderSuccess() {
                Toast.makeText(OrderDetailActivity.this, "Hủy đơn hàng '" + txt_code.getText().toString() + "' thành công", Toast.LENGTH_SHORT).show();
                order.setStatus("3");
                update = true;
                setStatusView();
            }
        }).Update_Order(order.getId(), order.getPayment(), "3", order.getRequire(), order.getDelivery(), order.getPhone(), order.getAddress(), order.getEmail(), order.getName());
    }

    private void cancelOrderWithCard() {
        new PaymentManager(this, this, order.getPayment(), new OnCancelOrderListener() {
            @Override
            public void cancelSuccess() {
                cancleOrder();
            }
        }).getPayment(order.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle_order:
                bottomSheetDialog.dismiss();
                if (Ultil.isNetworkConnected(this)) {
                    if (order.getStatus().equals("1")) {
                        switch (order.getPayment()) {
                            case "1":
                                cancleOrder();
                                break;
                            case "2":
                                cancelOrderWithCard();
                                break;
                            case "3":
                                cancelOrderWithCard();
                                break;
                            case "4":
                                cancelOrderWithCard();
                                break;
                        }
                    } else {
                        cancleOrder();
                    }
                } else {
                    stop_loading(false);
                }
                break;
            case R.id.btn_cancle_order_detail:
                bottomSheetDialog.show();
                break;
            case R.id.btn_paymen_again:
                Intent intent = new Intent(OrderDetailActivity.this, PaymentActivity.class);
                intent.putExtra("order_detail", order);
                startActivity(intent);
                break;
            case R.id.img_close_bottom_sheet:
                bottomSheetDialog.dismiss();
                break;
        }
    }
}
