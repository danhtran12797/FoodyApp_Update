package com.danhtran12797.thd.foodyapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LoveProductAdapter extends RecyclerView.Adapter<LoveProductAdapter.ViewHolder> {

    private static final String TAG = "LoveProductAdapter";

    ArrayList<Product> arrLoveProduct;
    DecimalFormat decimalFormat;
    Context context;

    Product mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;

    IDeleteLove listener_delete;
    public boolean is_delete = true;

    public LoveProductAdapter(Context context, ArrayList<Product> arrLoveProduct) {
        this.context = context;
        this.arrLoveProduct = arrLoveProduct;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    public void setDeleteItemListener(IDeleteLove listener) {
        this.listener_delete = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_love_product, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = arrLoveProduct.get(position);
        Log.d(TAG, "onBindViewHolder: " + position);

        String sale1 = product.getSale1();
        int gia = Integer.parseInt(product.getPrice());
        holder.txtPriceProductLove.setText(decimalFormat.format(gia) + " VNĐ");
        if (sale1.equals("0")) {
            holder.layoutSale1ProductLove.setVisibility(View.GONE);
        } else {
            int sale = Integer.parseInt(sale1);
            holder.layoutSale1ProductLove.setVisibility(View.VISIBLE);
            holder.txtCostProductLove.setText(decimalFormat.format(gia) + " Đ/Phần ");
            holder.txtCostProductLove.setPaintFlags(holder.txtSale1ProductLove.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtSale1ProductLove.setText(" -" + sale1 + "% OFF");
            gia = gia * sale / 100;
            holder.txtPriceProductLove.setText(decimalFormat.format(gia) + " VNĐ");
        }
        holder.txtNameProductLove.setText(product.getName());
        if (product.getCountLove() != null) {
            holder.txtLoveProductLove.setVisibility(View.VISIBLE);
            holder.txtLoveProductLove.setText(product.getCountLove());
        } else {
            holder.txtLoveProductLove.setVisibility(View.GONE);
        }
        holder.txtDescProductLove.setText(product.getDesc());
        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgProductLove);
    }

    @Override
    public int getItemCount() {
        return arrLoveProduct.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductLove;
        TextView txtNameProductLove;
        TextView txtPriceProductLove;
        TextView txtLoveProductLove;
        TextView txtSale1ProductLove;
        TextView txtCostProductLove;
        TextView txtDescProductLove;
        LinearLayout layoutSale1ProductLove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProductLove = itemView.findViewById(R.id.imgProductLove);
            txtNameProductLove = itemView.findViewById(R.id.txtNameProductLove);
            txtPriceProductLove = itemView.findViewById(R.id.txtPriceProductLove);
            txtLoveProductLove = itemView.findViewById(R.id.txtLoveProductLove);
            txtSale1ProductLove = itemView.findViewById(R.id.txtSale1ProductLove);
            txtCostProductLove = itemView.findViewById(R.id.txtCostProductLove);
            txtDescProductLove = itemView.findViewById(R.id.txtDescProductLove);
            layoutSale1ProductLove = itemView.findViewById(R.id.layoutSale1ProductLove);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailProductActivity.class);
                    intent.putExtra("detail_product", arrLoveProduct.get(getAdapterPosition()));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    public void deleteItem(int position, Activity activity) {
        mRecentlyDeletedItem = arrLoveProduct.get(position);
        mRecentlyDeletedItemPosition = position;
        arrLoveProduct.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(activity);
    }

    private void showUndoSnackbar(Activity activity) {
        View view = activity.findViewById(R.id.layout_user_love);
        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.snack_bar_text, mRecentlyDeletedItem.getName()),
                Snackbar.LENGTH_LONG);
        snackbar.setAction("thêm lại", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_delete = true;
                undoDelete();
            }
        });
        snackbar.show();
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                //see Snackbar.Callback docs for event details
                Log.d(TAG, "onDismissed: snackbar");
                Log.d(TAG, "is_delete: " + is_delete);
                if (is_delete == false) {
                    listener_delete.deleteItem(mRecentlyDeletedItem.getId());
                    is_delete = true;
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                Log.d(TAG, "onShown: snackbar");
                is_delete = false;
            }
        });
    }

    public void undoDelete() {
        arrLoveProduct.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

//    public void deleteItem(int position) {
//        arrLoveProduct.remove(position);
//        notifyItemRemoved(position);
//    }

    public void deleteAllItem() {
        arrLoveProduct.clear();
        notifyDataSetChanged();
    }

    public interface IDeleteLove {
        void deleteItem(String idProduct);
    }
}
