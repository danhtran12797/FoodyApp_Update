package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danhtran12797.thd.foodyapp.R;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<String> {

    ArrayList<String> results;
    ISearch listener;

    SearchApi searchApi = new SearchApi();

    public SearchAdapter(@NonNull Context context, int resource) {
        super(context, resource);
//        results=new ArrayList<>();
        listener = (ISearch) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_history_search, parent, false
            );
        }

        String nameItem = getItem(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(nameItem);
            }
        });
        TextView textViewName = convertView.findViewById(R.id.txtNameHistory);

        if (nameItem != null) {
            textViewName.setText(nameItem);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return results.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
//                    DataService dataService = APIService.getService();
//                    Call<List<Product>> callback = dataService.GetProductFromSearch(constraint.toString());
//                    callback.enqueue(new Callback<List<Product>>() {
//                        @Override
//                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
//                            Log.d("CCC", "onResponse: GOOD");
//                            results.clear();
//                            results = (ArrayList<Product>) response.body();
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<Product>> call, Throwable t) {
//                            Log.d("CCC", "onFailure: " + t.getMessage());
//                        }
//                    });

                    results = searchApi.autoComplete(constraint.toString());

                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
