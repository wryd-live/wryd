package com.wrydhub.wryd.wrydapp.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wrydhub.wryd.wrydapp.ProfileView;
import com.wrydhub.wryd.wrydapp.R;

import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.Myholder>{

    Context context;
    ArrayList<ModelClass>arrayList;
    LayoutInflater layoutInflater;

    public FruitAdapter(Context context, ArrayList<ModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater=LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view=layoutInflater.inflate(R.layout.item_file,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        holder.fruitName.setText(arrayList.get(position).getFruitName());
        holder.fruitNum.setText(arrayList.get(position).getFruitNum());
        holder.img.setImageResource(arrayList.get(position).getImg());
        holder.personId = arrayList.get(position).personId;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Myholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fruitName,fruitNum;
        ImageView img;
        int personId;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            fruitName=itemView.findViewById(R.id.txt);
            fruitNum=itemView.findViewById(R.id.txt2);
            img=itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(view.getContext(), ProfileView.class);
            myIntent.putExtra("personid", personId); //Optional parameters
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(myIntent);
        }
    }
}
