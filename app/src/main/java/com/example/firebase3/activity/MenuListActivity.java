package com.example.firebase3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.firebase3.R;
import com.example.firebase3.adapter.MenuListAdapter;
import com.example.firebase3.model.Menu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity {
    Menu getMenu;
    String categoryName = "";
    private List<Menu> menuList = new ArrayList<>();
    MenuListAdapter adapter;
    int menu_id;
    ArrayList<String> imgList = new ArrayList<>();
    RecyclerView food_recycler;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("Menu");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        food_recycler = findViewById(R.id.food_recycler);

        adapter = new MenuListAdapter(menuList, getApplicationContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        food_recycler.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        Intent data = getIntent();
        String[] category = (String[]) data.getSerializableExtra("category");
        if (category == null) {
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        menu_id = Integer.parseInt(childSnapshot.getKey());
                        menuList.clear();
                        db.child(String.valueOf(menu_id)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                getMenu = new Menu(Integer.parseInt(childSnapshot.getKey()), snapshot.child("Title").getValue(String.class), Integer.parseInt(snapshot.child("Price").getValue(String.class)), snapshot.child("Image").getValue(String.class));
                                menuList.add(getMenu);
                                food_recycler.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            for (String item : category) {
                categoryName = item;
                db.orderByChild("Category").equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            menu_id = Integer.parseInt(childSnapshot.getKey());
                            menuList.clear();
                            db.child(String.valueOf(menu_id)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    getMenu = new Menu(Integer.parseInt(childSnapshot.getKey()), snapshot.child("Title").getValue(String.class), Integer.valueOf(snapshot.child("Price").getValue(String.class)), snapshot.child("Image").getValue(String.class));
                                    menuList.add(getMenu);
                                    food_recycler.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
}