package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.model.Banner;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerAdapter extends androidx.viewpager.widget.PagerAdapter {
    Context context;
    ArrayList<Banner> banners;

    public BannerAdapter(Context context, ArrayList<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    // Định hình cho mỗi page
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.row_banner, null);

        ImageView imgBackgroundBanner = view.findViewById(R.id.imgBackgroundBanner);
        TextView txtTitleBanner = view.findViewById(R.id.txtTitleBanner);
        TextView txtDescBanner = view.findViewById(R.id.txtDescBanner);

        final Banner banner = banners.get(position);
        Picasso.get().load(Ultil.url_image_banner + banner.getImageBanner())
                .error(R.drawable.error)
                .into(imgBackgroundBanner);
        txtTitleBanner.setText(banner.getNameBanner());
        txtDescBanner.setText(banner.getTitleBanner());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_product_to_banner(banner.getIdProduct());
            }
        });

        container.addView(view);
        return view;
    }

    private void get_product_to_banner(String id_product) {
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProductToBanner(id_product);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                ArrayList<Product> arrayList = (ArrayList<Product>) response.body();
                if (arrayList.size() > 0) {
                    Intent intent = new Intent(context, DetailProductActivity.class);
                    intent.putExtra("detail_product", arrayList.get(0));
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
