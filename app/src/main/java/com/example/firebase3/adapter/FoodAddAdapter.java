package com.example.firebase3.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase3.R;
import java.util.ArrayList;
import java.util.List;

public class FoodAddAdapter extends RecyclerView.Adapter<FoodAddAdapter.MyViewHolder>{
    public List<Uri> imageList;
    public FoodAddAdapter(List<Uri> imageList) {
        this.imageList = imageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.image_items,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.imgViewIcon.setImageURI(imageList.get(position));
        Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imgViewIcon);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgViewIcon;
        public MyViewHolder (@NonNull View itemView){
            super(itemView);
            imgViewIcon = itemView.findViewById(R.id.imgViewIcon);
        }
    }
}
