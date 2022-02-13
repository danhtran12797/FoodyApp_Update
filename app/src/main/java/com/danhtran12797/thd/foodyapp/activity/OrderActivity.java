package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.OrderAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements ILoading, OrderAdapter.OnItemClickListener {

    private static final String TAG = "OrderActivity";
    private static final int REQUEST_CODE_ORDER_DETAIL = 12;

    Toolbar toolbar;
    RecyclerView recyclerView;
    RotateLoading rotateLoading;
    FrameLayout layout_container;
    OrderAdapter adapter;
    LinearLayout layout;
    RelativeLayout layout_order;
    Button btn_buy_now;
    ArrayList<Order> arrOrder;
    int position;

    ILoading mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mListener = this;

        initView();
        initActionBar();
        if (Ultil.isNetworkConnected(this)) {
            loadData();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    private void close_layout_list_order() {
        recyclerView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Log.d(TAG, "user id: " + Ultil.user.getId());
        Call<List<Order>> callback = dataService.GetOrder(Ultil.user.getId(), "");
        callback.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                arrOrder = (ArrayList<Order>) response.body();
                if (arrOrder.size() > 0) {
                    adapter = new OrderAdapter(OrderActivity.this, arrOrder);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(OrderActivity.this, linearLayoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecorationvider);
                } else {
                    close_layout_list_order();
                }

                mListener.stop_loading(true);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListener.stop_loading(false);
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

    private void initView() {
        layout_container = findViewById(R.id.layout_container);
        layout_order = findViewById(R.id.layout_order);
        toolbar = findViewById(R.id.toolbar_order);
        recyclerView = findViewById(R.id.recycler_view_order);
        rotateLoading = findViewById(R.id.rotateLoading);
        layout = findViewById(R.id.layout_no_product_love);
        btn_buy_now = findViewById(R.id.btn_buy_now);
        btn_buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            Ultil.show_snackbar(layout_order, null);
        }
    }

    @Override
    public void onItemClick(int position) {
        this.position = position;
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_detail", arrOrder.get(position));
        startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ORDER_DETAIL && resultCode == RESULT_OK && data != null) {
            boolean update = data.getBooleanExtra("update1", false);
            if (update) {
                arrOrder.get(position).setStatus("3");
                adapter.notifyItemChanged(position);
            }
        }
    }
}
