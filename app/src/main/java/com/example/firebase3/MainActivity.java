package com.example.firebase3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebase3.activity.MenuListActivity;
import com.example.firebase3.adapter.MainAdapter;
import com.example.firebase3.model.Menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    int menu_id = 0;
    Button btn_rice, btn_hot_pot, btn_noodle_soup, btn_soup, btn_dessert, btn_fast_food, btn_combo, btn_drink;
    RecyclerView popularFood, recentAddFood;
    MainAdapter recentAddAdapter;
    MainAdapter popularAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("Menu");
    List<Menu> recentAddList = new ArrayList<>();
    List<Menu> popularList = new ArrayList<>();
    Menu getRecentAddMenu;
    Menu getPopularMenu;
    String[] category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("sdjfksdfhksjdhfjksdhjkfhksjdh");
        btn_rice = findViewById(R.id.button_rice);
        btn_hot_pot = findViewById(R.id.button_hot_pot);
        btn_noodle_soup = findViewById(R.id.button_noodle_soup);
        btn_soup = findViewById(R.id.button_soup);
        btn_dessert = findViewById(R.id.button_dessert);
        btn_fast_food = findViewById(R.id.button_fast_food);
        btn_combo = findViewById(R.id.button_combo);
        btn_drink = findViewById(R.id.button_drink);
        popularFood = findViewById(R.id.popularFood);
        recentAddFood = findViewById(R.id.recentAddFood);

        Intent intent = new Intent(MainActivity.this, MenuListActivity.class);

        recentAddAdapter = new MainAdapter(recentAddList, getApplicationContext());
        LinearLayoutManager recentAddLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recentAddFood.setLayoutManager(recentAddLayout);

        popularAdapter = new MainAdapter(popularList, getApplicationContext());
        LinearLayoutManager popularLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        popularFood.setLayoutManager(popularLayout);

        btn_rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Cơm"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_hot_pot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Lẩu"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_noodle_soup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Mì", "Phở"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_soup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Súp"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Tráng miệng"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_fast_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Ăn nhanh"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Combo"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        btn_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = new String[]{"Đồ Uống"};
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        ArrayList<String> categoryList = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String category = childSnapshot.child("Category").getValue(String.class);
                    categoryList.add(category);
                }
                ArrayList<String> categoryList2 = (ArrayList<String>) categoryList.stream().distinct().collect(Collectors.toList());
                for (String item : categoryList2) {
                    Query getPopular = db.orderByChild("Category").equalTo(item).limitToFirst(1);
                    getPopular.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                menu_id = Integer.parseInt(childSnapshot.getKey());
                                popularList.clear();
                                db.child(String.valueOf(menu_id)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        getPopularMenu = new Menu(Integer.parseInt(childSnapshot.getKey()), snapshot.child("Title").getValue(String.class), Integer.valueOf(snapshot.child("Price").getValue(String.class)), snapshot.child("Image").getValue(String.class));
                                        popularList.add(getPopularMenu);
                                        popularFood.setAdapter(popularAdapter);
                                        popularAdapter.notifyDataSetChanged();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query getRecentAdd = db.orderByChild("Date Add").limitToLast(5);
        getRecentAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    menu_id = Integer.parseInt(childSnapshot.getKey());
                    recentAddList.clear();
                    db.child(String.valueOf(menu_id)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            getRecentAddMenu = new Menu(Integer.parseInt(childSnapshot.getKey()), snapshot.child("Title").getValue(String.class), Integer.valueOf(snapshot.child("Price").getValue(String.class)), snapshot.child("Image").getValue(String.class));
                            recentAddList.add(getRecentAddMenu);
                            recentAddFood.setAdapter(recentAddAdapter);
                            recentAddAdapter.notifyDataSetChanged();
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