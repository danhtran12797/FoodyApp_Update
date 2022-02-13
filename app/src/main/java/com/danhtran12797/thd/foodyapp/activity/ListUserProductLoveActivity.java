package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.SwipeToDeleteCallback;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.LoveProductAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUserProductLoveActivity extends AppCompatActivity implements ILoading {

    private static final String TAG = "ListUserProductLoveActi";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layout;
    private LoveProductAdapter adapter;
    private ArrayList<Product> arrProduct = null;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private RelativeLayout layout_user_love;
    private Button btn_buy_now;

    ILoading mListener;

    JWTToken jwtToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_product_love);

        mListener = this;

        initView();
        initActionBar();
        if (Ultil.isNetworkConnected(this)) {
            jwtToken = Ultil.getTokenPreference(this);
            loadData();
            Toast.makeText(this, "Trượt ngang để xóa thực đơn bạn thích", Toast.LENGTH_SHORT).show();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_product_love, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove_all_list_product_love:
                if (arrProduct != null) {
                    if (arrProduct.size() != 0) {
                        dialogDeleteAll();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle("Xóa thực đơn yêu thích");
        builder.setMessage("Bạn có muốn xóa tất cả thực đơn yêu thích?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "token user: " + jwtToken.getToken());
                deleteAllUserProduct(jwtToken.getToken());
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void setAdapter(ArrayList<Product> arrProduct) {
        adapter = new LoveProductAdapter(ListUserProductLoveActivity.this, arrProduct);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setDeleteItemListener(new LoveProductAdapter.IDeleteLove() {
            @Override
            public void deleteItem(String idProduct) {
                deleteUserLoveProduct(idProduct, jwtToken.getToken());
            }
        });
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteAllUserProduct(String token) {
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteAllUserLoveProduct(token);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                if (response.isSuccessful()) {
                    String message = response.body();
                    Log.d(TAG, message);
                    if (message.equals("success")) {
                        Toast.makeText(ListUserProductLoveActivity.this, "Đã xóa tất cả thực đơn trong danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show();
                        adapter.deleteAllItem();
                        close_layout_list_product();
                    }
                } else {
                    if (response.code() == 401) {
                        Ultil.logout_account(ListUserProductLoveActivity.this);
                        Ultil.dialogRequestTimeOut(ListUserProductLoveActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

    private void deleteUserLoveProduct(String idProduct, String token) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteUserLoveProduct(idProduct, token);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "code: " + response.code());
                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message.equals("success")) {
                        if (arrProduct.size() == 0) {
                            close_layout_list_product();
                        }
                    }
                } else {
                    if (response.code() == 401) {
                        Ultil.logout_account(ListUserProductLoveActivity.this);
                        Ultil.dialogRequestTimeOut(ListUserProductLoveActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListener.stop_loading(false);
                adapter.undoDelete();
            }
        });
    }

    private void loadData() {
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProductUserLove(Ultil.user.getId());
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                arrProduct = (ArrayList<Product>) response.body();
                if (arrProduct.size() > 0) {
                    open_layout_list_product();
                    setAdapter(arrProduct);
                } else {
                    close_layout_list_product();
                }
                mListener.stop_loading(true);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

    private void open_layout_list_product() {
        recyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
    }

    private void close_layout_list_product() {
        recyclerView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
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
        layout_user_love = findViewById(R.id.layout_user_love);
        toolbar = findViewById(R.id.toolbar_list_user_product_love);
        recyclerView = findViewById(R.id.recyclerView_list_user_product_love);
        layout = findViewById(R.id.layout_no_product_love);
        rotateLoading = findViewById(R.id.rotateLoading);
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
            Ultil.show_snackbar(layout_user_love, null);
        }
    }
}
