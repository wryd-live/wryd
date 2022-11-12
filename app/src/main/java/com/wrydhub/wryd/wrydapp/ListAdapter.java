package com.wrydhub.wryd.wrydapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<User> {


    public ListAdapter(Context context, ArrayList<User> userArrayList){

        super(context,R.layout.list_item_home,userArrayList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);

        if (convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_home,parent,false);

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
