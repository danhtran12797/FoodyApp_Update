package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<String> lstHistory;
    IHistory listener;

    public HistoryAdapter(List<String> lstHistory, Context context) {
        this.lstHistory = lstHistory;
        listener = (IHistory) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_search, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtHistory.setText(lstHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return lstHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHistory = itemView.findViewById(R.id.txtNameHistory);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClickHistory(lstHistory.get(getAdapterPosition()));
                }
            });
        }
    }

//    public void addItem(){
//        notifyItemInserted(0);
//    }

//    public void deleteAllItem(String name, int position){
//        lstHistory.remove(position);
//        notifyItemRemoved(position);
//    }

}
