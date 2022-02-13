package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {

    ArrayList<Product> arrProduct;
    DecimalFormat decimalFormat;

    private static final String TAG = "NewProductAdapter";

    public NewProductAdapter(ArrayList<Product> arrProduct) {
        this.arrProduct = arrProduct;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    @NonNull
    @Override
    public NewProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_new_product, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewProductAdapter.ViewHolder holder, int position) {
        Product product = arrProduct.get(position);

        String sale1 = product.getSale1();
        String sale2 = product.getSale2();
        int gia = Integer.parseInt(product.getPrice());

        if (sale2.equals("")) {
            holder.txtSale2.setVisibility(View.GONE);
        } else {
            holder.txtSale2.setVisibility(View.VISIBLE);
        }

        if (sale1.equals("0")) {
            holder.layoutSale1.setVisibility(View.GONE);
        } else {
            int sale = Integer.parseInt(sale1);
            holder.layoutSale1.setVisibility(View.VISIBLE);
            gia = gia * sale / 100;
        }

        holder.txtNameProduct.setText(product.getName());
        holder.txtPriceProduct.setText(decimalFormat.format(gia) + " VNĐ");
        holder.txtSale1.setText(product.getSale1() + "%");
        holder.txtSale2.setText(product.getSale2());
        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return arrProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNameProduct;
        TextView txtPriceProduct;
        TextView txtSale1;
        TextView txtSale2;
        ImageView imgProduct;
        RelativeLayout layoutSale1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameProduct = itemView.findViewById(R.id.txtNameProduct);
            txtPriceProduct = itemView.findViewById(R.id.txtPriceProduct);
            txtSale1 = itemView.findViewById(R.id.txtSale1);
            txtSale2 = itemView.findViewById(R.id.txtSale2);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            layoutSale1 = itemView.findViewById(R.id.layout_sale1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailProductActivity.class);
                    intent.putExtra("detail_product", arrProduct.get(getAdapterPosition()));
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
