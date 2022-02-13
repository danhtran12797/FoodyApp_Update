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
import com.danhtran12797.thd.foodyapp.model.OrderDetail;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private static final String TAG = "OrderDetailAdapter";

//    Context context;
    ArrayList<OrderDetail> arrayList;
    DecimalFormat decimalFormat;
    IOrderDetail listener;

    public OrderDetailAdapter(IOrderDetail listener, ArrayList<OrderDetail> arrayList) {
        this.listener = listener;
//        this.context = context;
        this.arrayList = arrayList;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    public interface IOrderDetail {
        void itemClick(int position, View view);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_product_order_detail, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail orderDetail = arrayList.get(position);
        Log.d(TAG, "OrderDetailAdapter: " + orderDetail.getNameProduct());
        holder.nameProduct.setText(orderDetail.getNameProduct());
        holder.priceProduct.setText(decimalFormat.format(Integer.parseInt(orderDetail.getPrice())) + " VNĐ");
        holder.quantityProduct.setText("x " + orderDetail.getQuantityProduct());
        holder.categoryProduct.setText(orderDetail.getNameCategory());
        Picasso.get().load(Ultil.url_image_product + orderDetail.getImage())
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
            imgProduct = itemView.findViewById(R.id.img_product_order_detail);
            nameProduct = itemView.findViewById(R.id.txt_name_product_order_detail);
            quantityProduct = itemView.findViewById(R.id.txt_quantity_order_detail);
            priceProduct = itemView.findViewById(R.id.txt_price_product_order_detail);
            categoryProduct = itemView.findViewById(R.id.txt_category_product_order_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.itemClick(getAdapterPosition(), view);
                }
            });
        }
    }
}
