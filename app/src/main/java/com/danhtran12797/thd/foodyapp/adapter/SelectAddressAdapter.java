package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.AddLocationOrderActivity;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.ViewHolder> {

    private static final String TAG = "SelectAddressAdapter";

    Context context;
    ArrayList<AddressShipping> arrayList;
    int lastCheckedPos = 0;
    RadioButton radio = null;
    OnEditAddressShippingListener listener;

    public SelectAddressAdapter(Context context, ArrayList<AddressShipping> arrayList, int checked) {
        this.context = context;
        this.arrayList = arrayList;
        lastCheckedPos = checked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_select_address, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("ddd", "onBindViewHolder: " + position);
        AddressShipping addressShipping = arrayList.get(position);

        if (lastCheckedPos == position) {
            holder.radioButton.setChecked(true);
            holder.radioButton.setTag(true);
            radio = holder.radioButton;
        } else {
            holder.radioButton.setChecked(false);
            holder.radioButton.setTag(false);
        }

        holder.txtName.setText(addressShipping.getName());
        holder.txtPhone.setText(addressShipping.getPhone());
        holder.txtAddress.setText(addressShipping.getAddress());

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = (RadioButton) view;
                if (radioButton.getTag().equals(false)) {
                    radio.setChecked(false);
                    radio.setTag(false);
                    radioButton.setTag(true);
                    radioButton.setChecked(true);
                    radio = radioButton;

                    arrayList.get(lastCheckedPos).setCheck(false);
                    arrayList.get(position).setCheck(true);
                    Log.d(TAG, "arrayList: " + arrayList.get(position).isCheck());
                    Log.d(TAG, "arrAddressShipping: " + Ultil.arrAddressShipping.get(position).isCheck());
                    Ultil.setAddressShipping(context);
                    lastCheckedPos = position;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgeEdit;
        ImageView imgeClose;
        TextView txtName;
        TextView txtPhone;
        TextView txtAddress;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgeEdit = itemView.findViewById(R.id.img_edit_address);
            imgeClose = itemView.findViewById(R.id.img_close_address);
            txtName = itemView.findViewById(R.id.txt_name_user_address);
            txtPhone = itemView.findViewById(R.id.txt_phone_user_address);
            txtAddress = itemView.findViewById(R.id.txt_address_user_address);
            radioButton = itemView.findViewById(R.id.radio_select_address);

            imgeClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (arrayList.get(getAdapterPosition()).isCheck()) {
                        Toast.makeText(context, "Không thể xóa địa chỉ được chọn làm địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    } else {
                        arrayList.remove(getAdapterPosition());
                        Ultil.setAddressShipping(context);
                        notifyItemRemoved(getAdapterPosition());
                    }
                }
            });

            imgeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.editAddressShipping(getAdapterPosition(), arrayList.get(getAdapterPosition()));
//                    Intent intent = new Intent(context, AddLocationOrderActivity.class);
//                    intent.putExtra("edit_location_order", arrayList.get(getAdapterPosition()));
//                    intent.putExtra("position_location_order", getAdapterPosition());
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
                }
            });
        }
    }
    public interface OnEditAddressShippingListener {
        void editAddressShipping(int position, AddressShipping addressShipping);
    }

    public void setOnEditAddressShippingListener(OnEditAddressShippingListener listener){
        this.listener=listener;
    }

}
