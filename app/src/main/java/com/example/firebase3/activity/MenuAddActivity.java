package com.example.firebase3.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase3.MainActivity;
import com.example.firebase3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MenuAddActivity extends AppCompatActivity {
    String category = "";
    int PICK_IMAGE_REQUEST = 1;
    int maxId = 0;
    private int menu_id = 0;
    private EditText txtFoodName, txtDescription;
    private Spinner txtCategoryList;
    private EditText numPrice;
    private Button btnAddFood;
    private Button btnPick;
    private Button btnCancel;
    private List<String> categoryList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();
    private Uri imageUri;
    private ImageView imgIcon, addToCombo;
    TextView selected_food;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("Menu");
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_add);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtCategoryList = findViewById(R.id.txtCategoryList);
        txtDescription = findViewById(R.id.txtDescription);
        numPrice = findViewById(R.id.numPrice);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnPick = findViewById(R.id.btnPick);
        btnCancel = findViewById(R.id.btnCancel);
        imgIcon = findViewById(R.id.imgIcon);
        addToCombo = findViewById(R.id.addToCombo);
        selected_food = findViewById(R.id.selected_food);

        selectedList.replaceAll(String::trim);

        //("Chọn thể loại","Cơm","Lẩu","Phở","Mì","Súp","Đồ Uống","Tráng miệng","Ăn nhanh","Combo");
        categoryList.add(0, "Chọn thể loại");
        categoryList.add("Cơm");
        categoryList.add("Lẩu");
        categoryList.add("Phở");
        categoryList.add("Mì");
        categoryList.add("Súp");
        categoryList.add("Đồ Uống");
        categoryList.add("Tráng miệng");
        categoryList.add("Ăn nhanh");
        categoryList.add("Combo");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MenuAddActivity.this, android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        txtCategoryList.setAdapter(arrayAdapter);
        txtCategoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Chọn thể loại")) {
                    category = null;
                } else {
                    String item = adapterView.getItemAtPosition(i).toString();
                    category = item;
                    if (category.equals("Combo")) {
                        addToCombo.setVisibility(View.VISIBLE);
                    } else {
                        addToCombo.setVisibility(View.GONE);
                        selected_food.setVisibility(View.GONE);
                        selectedList.clear();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = null;
            }
        });

        Query query = db.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        maxId = Integer.parseInt(childSnapshot.getKey());
                    } catch (NumberFormatException ex) { // handle your exception

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addToCombo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCombo();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(MenuAddActivity.this, "Upload is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUri != null) {
                        menu_id = maxId + 1;
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String title = txtFoodName.getText().toString();
                        String description = txtDescription.getText().toString();
                        String price = numPrice.getText().toString();
                        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("MenuImage");
                        StorageReference imagename = ImageFolder.child(menu_id + "/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
                        if (txtFoodName.getText().toString().isEmpty() || numPrice.getText().toString().isEmpty()) {
                            Toast.makeText(MenuAddActivity.this, "Can not blanked", Toast.LENGTH_SHORT).show();

                        } else if (category == null) {
                            Toast.makeText(MenuAddActivity.this, "Mời chọn thể loại", Toast.LENGTH_SHORT).show();

                        } else if (category.equals("Combo")) {
                            uploadTask = imagename.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    db.child(String.valueOf(menu_id)).child("Title").setValue(title);
                                                    db.child(String.valueOf(menu_id)).child("Category").setValue(category);
                                                    db.child(String.valueOf(menu_id)).child("Price").setValue(price);
                                                    db.child(String.valueOf(menu_id)).child("Description").setValue(selectedList.toString().replaceAll("(^\\[|\\]$)", "").trim() + "\n" + description);
                                                    db.child(String.valueOf(menu_id)).child("Image").setValue(uri.toString());
                                                    db.child(String.valueOf(menu_id)).child("Date Add").setValue(dtf.format(now));
                                                    Toast.makeText(MenuAddActivity.this, "Upload Successfully!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MenuAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {

                            uploadTask = imagename.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    db.child(String.valueOf(menu_id)).child("Title").setValue(title);
                                                    db.child(String.valueOf(menu_id)).child("Category").setValue(category);
                                                    db.child(String.valueOf(menu_id)).child("Price").setValue(price);
                                                    db.child(String.valueOf(menu_id)).child("Description").setValue(description);
                                                    db.child(String.valueOf(menu_id)).child("Image").setValue(uri.toString());
                                                    db.child(String.valueOf(menu_id)).child("Date Add").setValue(dtf.format(now));
                                                    Toast.makeText(MenuAddActivity.this, "Upload Successfully!!!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MenuAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(MenuAddActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String title = childSnapshot.child("Title").getValue(String.class);
                    String category = childSnapshot.child("Category").getValue(String.class);
                    if (category.equals("Combo")) {

                    } else {
                        titleList.add(title);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToCombo() {
        boolean[] checkedItems = new boolean[titleList.size()];
        for (int i = 0; i < titleList.size(); i++) {
            checkedItems[i] = false;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuAddActivity.this);
        alertDialog.setMultiChoiceItems(titleList.toArray(new String[titleList.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    checkedItems[i] = true;
                    selectedList.add(titleList.get(i));
                } else {
                    checkedItems[i] = false;
                    selectedList.remove(titleList.get(i));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected_food.setVisibility(View.VISIBLE);
                selected_food.setText(selectedList.toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imgIcon);
        }
    }


    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}