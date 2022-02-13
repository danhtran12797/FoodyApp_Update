package com.danhtran12797.thd.foodyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DialogUserLoveAdapter extends BaseAdapter {

    Context context;
    ArrayList<User> arrayList;

    public DialogUserLoveAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
            view = layoutInflater.inflate(R.layout.item_list_user_love, null);
            viewHolder.imgAvatar = view.findViewById(R.id.img_dialog_user_love);
            viewHolder.txtName = view.findViewById(R.id.txt_name_dialog_user_love);
            viewHolder.txtEmail = view.findViewById(R.id.txt_email_dialog_user_love);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        User user = arrayList.get(i);
        viewHolder.txtName.setText(user.getName());
        viewHolder.txtEmail.setText(user.getEmail());
        Picasso.get().load(Ultil.url_image_avatar + user.getAvatar())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(viewHolder.imgAvatar);

        return view;
    }

    public class ViewHolder {
        CircleImageView imgAvatar;
        TextView txtName;
        TextView txtEmail;
    }
}
