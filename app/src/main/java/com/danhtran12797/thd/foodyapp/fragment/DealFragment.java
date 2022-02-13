package com.danhtran12797.thd.foodyapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.DealProductAdapter;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    DealProductAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CCC", "onResume DealFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CCC", "onCreateView DealFragment");
        view = inflater.inflate(R.layout.fragment_deal, container, false);
        initView();
        getData();
        return view;
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.recyerViewDeal);
    }

    private void getData() {
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetDealProduct();
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                ArrayList<Product> arrDeal = (ArrayList<Product>) response.body();
                //Log.d("CCC","DealFragment "+arrDeal.size());
                adapter = new DealProductAdapter(arrDeal);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
            }
        });
    }

}
