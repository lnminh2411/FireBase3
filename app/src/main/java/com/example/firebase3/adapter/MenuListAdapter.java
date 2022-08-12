package com.example.firebase3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase3.R;
import com.example.firebase3.activity.MenuListActivity;
import com.example.firebase3.activity.MenuUpdateActivity;
import com.example.firebase3.model.Menu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> {
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
        holder.textView4.setText(String.valueOf(menuList.get(position).getMenu_id()));
        Glide.with(holder.itemView.getContext()).load(menuList.get(position).getImage()).into(holder.mImage);
        holder.mFoodName.setText(menuList.get(position).getTitle());
        holder.mFoodPrice.setText(String.valueOf(menuList.get(position).getPrice()));
        int menu_id = menuList.get(holder.getAdapterPosition()).getMenu_id();
//        holder.mOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClick(v);
//            }
//        });
        holder.button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuUpdateActivity.class);
                intent.putExtra("menu_id", menu_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]{
                        // select any from the value
                        "Yes",
                        "No",
                };
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("You want to delete: "+menuList.get(holder.getAdapterPosition()).getTitle());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if delete option is choosed
                        // then call delete function
                        if(which==0) {
                            DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Menu");
                            // we are use add listerner
                            // for event listener method
                            // which is called with query.
                            Query query= db.child(String.valueOf(menu_id));
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mFoodName;
        public TextView mFoodPrice;
        public TextView textView4;
        public TextView mOption;
        public Button button_update, button_delete;
        public MyViewHolder (@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mFoodName = itemView.findViewById(R.id.mFoodName);
            mFoodPrice = itemView.findViewById(R.id.mFoodPrice);
            textView4 = itemView.findViewById(R.id.textView4);
//            mOption = itemView.findViewById(R.id.mOption);
            button_update = itemView.findViewById(R.id.button_update);
            button_delete = itemView.findViewById(R.id.button_delete);
        }
    }

}
