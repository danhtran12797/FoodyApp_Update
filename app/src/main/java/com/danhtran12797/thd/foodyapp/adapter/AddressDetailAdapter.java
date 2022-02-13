package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.model.AddressDetail;

import java.util.ArrayList;

public class AddressDetailAdapter extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<AddressDetail> arrayList;
    ArrayList<AddressDetail> exampleListFull;

    public AddressDetailAdapter(Context context, ArrayList<AddressDetail> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        exampleListFull = new ArrayList<>(arrayList);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_listview_select_address, null);

            viewHolder.textView = view.findViewById(R.id.txt_item_name_address_detail);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        AddressDetail addressDetail = arrayList.get(i);
        viewHolder.textView.setText(addressDetail.getTitle());
        return view;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AddressDetail> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (AddressDetail item : exampleListFull) {
                    if (item.toString().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public String getTitle(int position) {
        return arrayList.get(position).getTitle();
    }

    public Integer getId(int position) {
        return arrayList.get(position).getId();
    }

    class ViewHolder {
        TextView textView;
    }
}
