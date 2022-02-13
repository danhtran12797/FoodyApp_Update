package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.AllProductActivity;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    ArrayList<Category> categoryArrayList;
    private static final String TAG = "CategoryAdapter";

    public CategoryAdapter(ArrayList<Category> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_category, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryArrayList.get(position);

        Log.d(TAG, Ultil.url_image_category + category.getImage());

        holder.txtNameCatrgory.setText(category.getName());
        holder.txtCount.setText(category.getCountProduct() + " thực đơn");
        Picasso.get().load(Ultil.url_image_category + category.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(holder.imgCategory);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameCatrgory;
        TextView txtCount;
        ImageView imgCategory;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtNameCatrgory = itemView.findViewById(R.id.txtNameCategory);
            txtCount = itemView.findViewById(R.id.txtCountPro);
            imgCategory = itemView.findViewById(R.id.imgCategory);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), AllProductActivity.class);
                    intent.putExtra("id_category", categoryArrayList.get(getAdapterPosition()).getId());
                    intent.putExtra("name_category", categoryArrayList.get(getAdapterPosition()).getName());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
}
