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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuUpdateActivity extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    Uri image;
    StorageTask uploadTask;
    String menu_id;
    private EditText txtUpdateFoodName, txtUpdateDescription, numUpdatePrice;
    private Button btnUpdatePick, btnUpdate, btnCancel;
    private ImageView imgUpdateIcon, addToComboUpdate;
    TextView selected_foodUpdate;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("Menu");
    String category = "";
    private List<String> titleList = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_update);
        txtUpdateFoodName = findViewById(R.id.txtUpdateFoodName);
        txtUpdateDescription = findViewById(R.id.txtUpdateDescription);
        numUpdatePrice = findViewById(R.id.numUpdatePrice);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdatePick = findViewById(R.id.btnUpdatePick);
        imgUpdateIcon = findViewById(R.id.imgUpdateIcon);
        addToComboUpdate = findViewById(R.id.addToComboUpdate);
        selected_foodUpdate = findViewById(R.id.selected_foodUpdate);

        selectedList.replaceAll(String::trim);

        Bundle extras = getIntent().getExtras();
        menu_id = extras.getSerializable("menu_id").toString();


        db.child(menu_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category = snapshot.child("Category").getValue(String.class);
                String description = snapshot.child("Description").getValue(String.class);
                if (category.equals("Combo")){
                    addToComboUpdate.setVisibility(View.VISIBLE);
                    selected_foodUpdate.setVisibility(View.VISIBLE);
                    String[] parts = description.split("\n");
                    selectedList = new ArrayList<>(Arrays.asList(parts[0].replaceAll("(^\\[|\\]$)", "").split(",")));
                    selected_foodUpdate.setText(parts[0].replaceAll("(^\\[|\\]$)", "").trim());
                    description = parts[1];
                }
                Glide.with(getApplicationContext()).load(Uri.parse(snapshot.child("Image").getValue(String.class))).into(imgUpdateIcon);
                txtUpdateFoodName.setText(snapshot.child("Title").getValue(String.class));
                numUpdatePrice.setText(snapshot.child("Price").getValue(String.class));
                txtUpdateDescription.setText(description);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addToComboUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCombo();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = txtUpdateFoodName.getText().toString();
                String description = txtUpdateDescription.getText().toString();
                String price = numUpdatePrice.getText().toString();
                if (imageUri != null) {
                    StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("MenuImage");
                    StorageReference imagename = ImageFolder.child(menu_id + "/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    uploadTask = imagename.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            db.child(String.valueOf(menu_id)).child("Title").setValue(title);
                                            db.child(String.valueOf(menu_id)).child("Price").setValue(price);
                                            if (category.equals("Combo")){
                                                db.child(String.valueOf(menu_id)).child("Description").setValue(selectedList.toString().replaceAll("(^\\[|\\]$)","")+"\n"+description);
                                            }else{
                                                db.child(String.valueOf(menu_id)).child("Description").setValue(description);
                                            }
                                            db.child(String.valueOf(menu_id)).child("Image").setValue(uri.toString());
                                            finish();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MenuUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {
                    db.child(String.valueOf(menu_id)).child("Title").setValue(title);
                    db.child(String.valueOf(menu_id)).child("Price").setValue(price);
                    if (category.equals("Combo")){
                        db.child(String.valueOf(menu_id)).child("Description").setValue(selectedList.toString().replaceAll("(^\\[|\\]$)","")+"\n"+description);
                    }else{
                        db.child(String.valueOf(menu_id)).child("Description").setValue(description);
                    }
                    finish();
                }
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    String title = childSnapshot.child("Title").getValue(String.class);
                    String category = childSnapshot.child("Category").getValue(String.class);
                    if (category.equals("Combo")){

                    }else {
                        titleList.add(title);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCombo(){
        boolean[] checkedItems = new boolean[titleList.size()];
        selectedList.replaceAll(String::trim);
        for (String item : selectedList){
            for (int i = 0; i < titleList.size(); i++){
                if (titleList.get(i).equals(item)){
                    checkedItems[i] = true;
                }
            }
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuUpdateActivity.this);
        alertDialog.setMultiChoiceItems(titleList.toArray(new String[titleList.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b){
                    checkedItems[i] = true;
                    selectedList.add(titleList.get(i));
                }else {
                    checkedItems[i] = false;
                    selectedList.remove(titleList.get(i));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected_foodUpdate.setText(selectedList.toString());
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
            Glide.with(this).load(imageUri).into(imgUpdateIcon);
        }
    }


    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}