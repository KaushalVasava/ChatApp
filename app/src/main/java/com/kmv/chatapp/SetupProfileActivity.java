package com.kmv.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.databinding.ActivitySetupProfileBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);

        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });

        binding.continueBtn.setOnClickListener(v -> {
          String name=binding.nameBox.getText().toString();
            if(name.isEmpty() ){
                binding.nameBox.setHint("Please type your name");
                return;
            }
            progressDialog.show();
            if(selectedImage!=null) {
                StorageReference storageReference = storage.getReference().child("profile").child(auth.getUid());
                storageReference.putFile(selectedImage).addOnCompleteListener((Task<UploadTask.TaskSnapshot> task) -> {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String uid = auth.getUid();
                            String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                            String name1 = binding.nameBox.getText().toString();
                            String image = uri.toString();

                            User user = new User(uid, name1, phoneNumber, image,"online",name1.toLowerCase());
                            database.getReference()
                                    .child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        //                          progressDialog.show();
                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
//                                        intent.putExtra("name2", name1);
//                                        intent.putExtra("pImage",image);
                                       // Log.d("L",image);
                                        startActivity(intent);
                                        finish();
                                    });
                        });
                    }
                    else {
                        Log.d("T","Else block execute");
                    }
                });
            }else{
                String uid=auth.getUid();
                String phoneNumber =auth.getCurrentUser().getPhoneNumber();

                User user=new User(uid,name,phoneNumber,"default","online",name.toLowerCase());
                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.show();
                                Intent intent=new Intent(SetupProfileActivity.this, MainActivity.class);
//                                intent.putExtra("name2",binding.nameBox.getText().toString());
//                                intent.putExtra("pImage",R.drawable.avatar);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                binding.profileImage.setImageURI(data.getData());
               selectedImage=data.getData();
            }
        }
    }
}