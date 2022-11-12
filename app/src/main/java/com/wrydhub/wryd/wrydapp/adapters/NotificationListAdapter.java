package com.wrydhub.wryd.wrydapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.models.User;

import java.util.ArrayList;

public class NotificationListAdapter extends ArrayAdapter<User> {


    public NotificationListAdapter(Context context, ArrayList<User> userArrayList){

        super(context, R.layout.list_item_notification,userArrayList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);

        if (convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notification,parent,false);

        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_pic);
        TextView userName = (TextView) convertView.findViewById(R.id.personName);
        TextView lastMsg = (TextView) convertView.findViewById(R.id.lastMessage);
        TextView time = (TextView) convertView.findViewById(R.id.msgtime);

        imageView.setImageResource(user.imageId);

        Glide
                .with(convertView)
                .load(user.imageUrl)
                .into(imageView);

        userName.setText(user.name);
        lastMsg.setText(user.lastMessage);
        time.setText(user.lastMsgTime);


        return convertView;
    }
}
