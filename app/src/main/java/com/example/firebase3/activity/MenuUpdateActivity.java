package com.example.firebase3.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class MenuUpdateActivity extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    Uri image;
    StorageTask uploadTask;
    String menu_id;
    private EditText txtUpdateFoodName, txtUpdateDescription, numUpdatePrice;
    private Button btnUpdatePick, btnUpdate, btnCancel;
    private ImageView imgUpdateIcon;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("Menu");

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
        Bundle extras = getIntent().getExtras();
        menu_id = extras.getSerializable("menu_id").toString();

        db.child(menu_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Glide.with(getApplicationContext()).load(Uri.parse(snapshot.child("Image").getValue(String.class))).into(imgUpdateIcon);
                txtUpdateFoodName.setText(snapshot.child("Title").getValue(String.class));
                numUpdatePrice.setText(snapshot.child("Price").getValue(String.class));
                txtUpdateDescription.setText(snapshot.child("Description").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                            db.child(String.valueOf(menu_id)).child("Description").setValue(description);
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
                    db.child(String.valueOf(menu_id)).child("Description").setValue(description);
                    finish();
                }
            }

        });
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