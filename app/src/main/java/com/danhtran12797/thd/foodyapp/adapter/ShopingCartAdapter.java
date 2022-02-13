package com.danhtran12797.thd.foodyapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShopingCartAdapter extends RecyclerView.Adapter<ShopingCartAdapter.ViewHolder> {

    Context context;
    ArrayList<ShopingCart> arrShopingCarts;
    DecimalFormat decimalFormat;

    ShopingCart mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    boolean is_delete = true;

    IShopingCart listener;
    Activity activity;

    private static final String TAG = "ShopingCartAdapter";

    public interface IShopingCart {
        void changeQuantity();

        void hideLayoutShopingCart();
    }

    public ShopingCartAdapter(ArrayList<ShopingCart> arrayList, Context context) {
        listener = (IShopingCart) context;
        this.arrShopingCarts = arrayList;
        this.context = context;
        this.activity = (Activity) context;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    public void deleteAll() {
        arrShopingCarts.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_shoping_cart, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ShopingCart shopingCart = arrShopingCarts.get(position);

//        final double[] price = {shopingCart.getPrice()};
//        final int[] quantity = {shopingCart.getQuantity()};

        holder.txt_name.setText(shopingCart.getName());
        //holder.txt_price.setText(decimalFormat.format(price[0])+ " VNĐ");
        holder.txt_price.setText(decimalFormat.format(shopingCart.getPrice() * shopingCart.getQuantity()) + " VNĐ");
        Picasso.get().load(Ultil.url_image_product + shopingCart.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.img_shoping_cart);
        //holder.txt_quantity.setText(String.valueOf(quantity[0]));
        holder.txt_quantity.setText(String.valueOf(shopingCart.getQuantity()));
        holder.txt_category_shoping_cart.setText("Thuộc danh mục " + shopingCart.getCategoty());

        holder.img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sl = Integer.parseInt(holder.txt_quantity.getText().toString());
                if (sl < 10) {
                    sl += 1;
//                    double price = shopingCart.getPrice() * sl;
                    holder.txt_quantity.setText(sl + "");
//                    holder.txt_price.setText(decimalFormat.format(price) + " VNĐ");
                    shopingCart.setQuantity(sl);
                    listener.changeQuantity();
                } else {
                    Toast.makeText(context, "Bạn chỉ có thể mua tối đa 10 thực đơn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sl = Integer.parseInt(holder.txt_quantity.getText().toString());
                if (sl > 1) {
                    sl -= 1;
//                    double price = shopingCart.getPrice() * sl;
                    holder.txt_quantity.setText(sl + "");
//                    holder.txt_price.setText(decimalFormat.format(price) + " VNĐ");
                    shopingCart.setQuantity(sl);
                    listener.changeQuantity();
                } else {
                    Toast.makeText(context, "Không thể giảm được nữa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrShopingCarts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_shoping_cart;
        ImageView img_plus;
        ImageView img_minus;
        ImageView img_clos;
        TextView txt_seen_after;
        TextView txt_name;
        TextView txt_price;
        TextView txt_quantity;
        TextView txt_category_shoping_cart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_shoping_cart = itemView.findViewById(R.id.img_shoping_cart);
            img_plus = itemView.findViewById(R.id.img_plus_shoping_cart);
            img_minus = itemView.findViewById(R.id.img_minus_shoping_cart);
            img_clos = itemView.findViewById(R.id.img_close_shoping_cart);
            txt_seen_after = itemView.findViewById(R.id.txt_seen_after);
            txt_name = itemView.findViewById(R.id.txt_name_shoping_cart);
            txt_price = itemView.findViewById(R.id.txt_price_shoping_cart);
            txt_quantity = itemView.findViewById(R.id.txt_quantity_shoping_cart);
            txt_category_shoping_cart = itemView.findViewById(R.id.txt_category_shoping_cart);

            img_clos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "is_delete: " + is_delete);
                    if (is_delete) {
                        deleteItem(getAdapterPosition(), activity);
                    }
                }
            });
            txt_seen_after.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "is_delete: " + is_delete);
                    if (is_delete) {
                        deleteItem(getAdapterPosition(), activity);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, DetailProductActivity.class);
                    intent.putExtra("notify", arrShopingCarts.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void deleteItem(int position, Activity activity) {
        mRecentlyDeletedItem = arrShopingCarts.get(position);
        mRecentlyDeletedItemPosition = position;
        arrShopingCarts.remove(position);
        notifyItemRemoved(position);
        listener.changeQuantity();
        showUndoSnackbar(activity);
    }

    private void showUndoSnackbar(Activity activity) {
        View view = activity.findViewById(R.id.layout_show_shoping_cart);
        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.snack_bar_text, mRecentlyDeletedItem.getName()),
                Snackbar.LENGTH_LONG);
        snackbar.setAction("thêm lại", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_delete = true;
                undoDelete();
                listener.changeQuantity();
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
                    is_delete = true;
                    if (arrShopingCarts.size() == 0) {
                        listener.hideLayoutShopingCart();
                    }
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                Log.d(TAG, "onShown: snackbar");
                is_delete = false;
            }
        });
    }

    private void undoDelete() {
        arrShopingCarts.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
}
