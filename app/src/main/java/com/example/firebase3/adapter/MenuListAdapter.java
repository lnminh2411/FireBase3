package com.example.firebase3.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase3.R;
import com.example.firebase3.activity.MenuUpdateActivity;
import com.example.firebase3.model.Menu;

import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder>{
    private List<Menu> menuList;
    private Context context;

    public MenuListAdapter(List<Menu> menuList, Context context) {
        this.menuList = menuList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.food_list_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        Glide.with(holder.itemView.getContext()).load(menu.getImage()).into(holder.mImage);
        holder.mFoodName.setText(menu.getTitle());
        holder.mFoodPrice.setText(String.valueOf(menu.getPrice()));
//        holder.mOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(context,holder.mOption);
//                popupMenu.inflate(R.menu.option_menu);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()){
//                            case R.id.menu_update:
//                                Intent intent = new Intent(context, MenuUpdateActivity.class);
//                                intent.putExtra("UPDATE", me);
//                                break;
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mFoodName;
        public TextView mFoodPrice;
        public TextView mOption;
        public MyViewHolder (@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mFoodName = itemView.findViewById(R.id.mFoodName);
            mFoodPrice = itemView.findViewById(R.id.mFoodPrice);
            //mOption = itemView.findViewById(R.id.mOption);
        }
    }
}
