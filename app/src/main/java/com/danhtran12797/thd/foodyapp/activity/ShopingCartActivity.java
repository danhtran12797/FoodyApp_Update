package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.ShopingCartAdapter;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;

import java.text.DecimalFormat;

import maes.tech.intentanim.CustomIntent;

public class ShopingCartActivity extends AppCompatActivity implements View.OnClickListener, ShopingCartAdapter.IShopingCart {

    private static final String TAG = "ShopingCartActivity";

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RelativeLayout layout_empty_shoping_cart;
    private RelativeLayout layout_shoping_cart;
    private Button btn_checkout;
    private Button btn_next_shoping;
    private TextView txt_total_price_product;

    private ShopingCartAdapter adapter;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoping_cart);

        initView();
        initActionBar();
        setDataView();
    }

    private void dialogDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle("Xóa tất cả giỏ hàng");
        builder.setMessage("Bạn có muốn xóa tất cả thực đơn từ giỏ hàng?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.deleteAll();
                setTotalPrice();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shoping_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_all_shoping_cart:
                if (Ultil.arrShoping != null) {
                    dialogDeleteAll();
                } else {
                    Toast.makeText(this, "Không còn thực đơn nào trong giỏ hàng của bạn! Vui lòng chọn tiếp tục mua hàng.", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDataView() {
        //Ultil.arrShoping=Ultil.getShopingCart(this);
        if (Ultil.arrShoping != null) {
            show_shoping_cart();
            setTotalPrice();
            toolbar.setTitle("Giỏ hàng(" + Ultil.arrShoping.size() + ")");
            adapter = new ShopingCartAdapter(Ultil.arrShoping, ShopingCartActivity.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecorationvider);
        } else {
            hide_layout_shoping_cart();
        }
    }

    private void show_shoping_cart() {
        layout_empty_shoping_cart.setVisibility(View.GONE);
        layout_shoping_cart.setVisibility(View.VISIBLE);
    }

    private void hide_layout_shoping_cart() {
        layout_empty_shoping_cart.setVisibility(View.VISIBLE);
        layout_shoping_cart.setVisibility(View.GONE);
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
        decimalFormat = new DecimalFormat("###,###,###");

        recyclerView = findViewById(R.id.recycler_view_shoping_cart);
        toolbar = findViewById(R.id.toolbar_shoping_cart);
        layout_empty_shoping_cart = findViewById(R.id.layout_empty_shoping_cart);
        layout_shoping_cart = findViewById(R.id.layout_show_shoping_cart);
        btn_checkout = findViewById(R.id.btn_checkout);
        btn_next_shoping = findViewById(R.id.btn_next_shoping);
        txt_total_price_product = findViewById(R.id.txt_total_price_product);

        btn_checkout.setOnClickListener(this);
        btn_next_shoping.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_shoping:
//                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.btn_checkout:
                if (Ultil.user != null) {
                    startActivity(new Intent(this, AddressOrderActivity.class));
                    CustomIntent.customType(this, "fadein-to-fadeout");
                } else {
                    Ultil.dialogQuestionLogin("Bạn cần đăng nhập để tiến hành thủ tục thanh toán", "carts", this, null);
                }
                break;
        }
    }

    private void setTotalPrice() {
        if (Ultil.arrShoping.size() != 0) {
            double total_price = 0;
            for (ShopingCart cart : Ultil.arrShoping) {
                total_price += cart.getPrice() * cart.getQuantity();
            }
            txt_total_price_product.setText(decimalFormat.format(total_price) + " VNĐ");
            toolbar.setTitle("Giỏ hàng(" + Ultil.arrShoping.size() + ")");
        }
    }

    @Override
    public void changeQuantity() {
        setTotalPrice();
    }

    @Override
    public void hideLayoutShopingCart() {
        toolbar.setTitle("Giỏ hàng(0)");
        hide_layout_shoping_cart();
        Ultil.arrShoping = null;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish: ");
        super.finish();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        Ultil.setShopingCart(this);
        super.onStop();
    }
}
