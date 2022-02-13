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

public class DealProductAdapter extends RecyclerView.Adapter<DealProductAdapter.ViewHolder> {

    ArrayList<Product> arrProduct;
    DecimalFormat decimalFormat;

    private static final String TAG = "DealProductAdapter";

    public DealProductAdapter(ArrayList<Product> arrProduct) {
        this.arrProduct = arrProduct;
        decimalFormat = new DecimalFormat("###,###,###");
    }

    @NonNull
    @Override
    public DealProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_deal_product, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealProductAdapter.ViewHolder holder, int position) {
        Product product = arrProduct.get(position);

        String sale1 = product.getSale1();
        String sale2 = product.getSale2();

        if (sale2.equals("")) {
            holder.txtSale2Deal.setVisibility(View.GONE);
        } else {
            holder.txtSale2Deal.setVisibility(View.VISIBLE);
        }

        if (sale1.equals("0")) {
            holder.layoutSale1Deal.setVisibility(View.GONE);
        } else {
            holder.layoutSale1Deal.setVisibility(View.VISIBLE);
        }

        holder.txtNameProductDeal.setText(product.getName());
        holder.txtPriceProductDeal.setText(decimalFormat.format(Integer.parseInt(product.getPrice())) + " VNĐ");
        holder.txtSale1Deal.setText(product.getSale1() + "%");
        holder.txtSale2Deal.setText(product.getSale2());
        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgProductDeal);
    }

    @Override
    public int getItemCount() {
        return arrProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNameProductDeal;
        TextView txtPriceProductDeal;
        TextView txtSale1Deal;
        TextView txtSale2Deal;
        ImageView imgProductDeal;
        RelativeLayout layoutSale1Deal;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameProductDeal = itemView.findViewById(R.id.txtNameProductDeal);
            txtPriceProductDeal = itemView.findViewById(R.id.txtPriceProductDeal);
            txtSale1Deal = itemView.findViewById(R.id.txtSale1Deal);
            txtSale2Deal = itemView.findViewById(R.id.txtSale2Deal);
            imgProductDeal = itemView.findViewById(R.id.imgProductDeal);
            layoutSale1Deal = itemView.findViewById(R.id.layout_sale1Deal);

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
