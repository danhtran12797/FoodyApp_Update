package com.danhtran12797.thd.foodyapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.CategoryAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity implements ILoading {

    RecyclerView recyclerView;
    Toolbar toolbar;
    CategoryAdapter adapter;
    ArrayList<Category> arrCategory;
    RotateLoading rotateLoading;
    FrameLayout layout_container;
    RelativeLayout layout_category;
    ILoading mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mListener = this;

        initView();
        intActionBar();
        if (Ultil.isNetworkConnected(this)) {
            getData();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    private void intActionBar() {
        toolbar = findViewById(R.id.toolbar_category);
        toolbar.setTitle("Danh mục");
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
        layout_category = findViewById(R.id.layout_category);
        rotateLoading = findViewById(R.id.rotateLoading);
        recyclerView = findViewById(R.id.recyclerViewCategory);
    }

    private void getData() {
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Call<List<Category>> callback = dataService.GetCategory();
        callback.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                arrCategory = (ArrayList<Category>) response.body();
                adapter = new CategoryAdapter(arrCategory);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                recyclerView.setAdapter(adapter);
                mListener.stop_loading(true);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                mListener.stop_loading(false);
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
            Ultil.show_snackbar(layout_category, null);
        }
    }

}
