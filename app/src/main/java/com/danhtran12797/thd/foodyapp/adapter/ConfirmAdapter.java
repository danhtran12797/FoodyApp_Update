package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.ViewHolder> {
    private static final String TAG = "ConfirmAdapter";

    Context context;
    ArrayList<ShopingCart> arrayList;
    DecimalFormat decimalFormat;

    public ConfirmAdapter(Context context, ArrayList<ShopingCart> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_product_confirm, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopingCart shopingCart = arrayList.get(position);
        Log.d(TAG, "shopingCart: " + shopingCart.getName());
        holder.nameProduct.setText(shopingCart.getName());
        holder.priceProduct.setText(decimalFormat.format(shopingCart.getPrice()) + " VNĐ");
        holder.quantityProduct.setText("x " + shopingCart.getQuantity());
        holder.categoryProduct.setText(shopingCart.getCategoty());
        Picasso.get().load(Ultil.url_image_product + shopingCart.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView nameProduct;
        TextView priceProduct;
        TextView quantityProduct;
        TextView categoryProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product_confirm);
            nameProduct = itemView.findViewById(R.id.txt_name_product_confirm);
            quantityProduct = itemView.findViewById(R.id.txt_quantity_confirm);
            priceProduct = itemView.findViewById(R.id.txt_price_product_confirm);
            categoryProduct=itemView.findViewById(R.id.txt_category_product_confirm);
        }
    }
}
