package com.kmv.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Uri selectedImage;
    ProgressDialog progressDialog;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String uname = getIntent().getStringExtra("name");
        String uimage =getIntent().getStringExtra("image");
        String unumber =getIntent().getStringExtra("number");
        String receiverId =getIntent().getStringExtra("uid");


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = auth.getCurrentUser();
//        Log.d("P","id1 :"+id);
//        Log.d("P","id2 :"+firebaseUser.getUid());

        if(receiverId!=null) {
//            binding.editUserName.setText(uname);
//            binding.editPhoneNumber.setText(unumber);
            binding.changeImage.setVisibility(View.GONE);
            binding.saveName.setVisibility(View.GONE);
            binding.changePhoneNumber.setVisibility(View.GONE);
            binding.changeName.setVisibility(View.GONE);
            UID=receiverId;
        }
        else {
            binding.changeImage.setVisibility(View.VISIBLE);
            binding.saveName.setVisibility(View.VISIBLE);
            UID= firebaseUser.getUid();
        }
        reference = FirebaseDatabase.getInstance().getReference("users").child(UID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    binding.editUserName.setText(user.getUsername());
                    binding.editPhoneNumber.setText(user.getPhoneNumber());
                    binding.textUserName.setText(user.getUsername());
                    binding.textPhoneNumber.setText(user.getPhoneNumber());
                    if (user.getProfileImage().equals("default")) {
                        binding.editUserProfile.setImageResource(R.drawable.avatar);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getProfileImage()).into(binding.editUserProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


//        binding.editUserProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(ProfileActivity.this,ViewActivity.class);
//                startActivity(intent);
//            }
//        });

        binding.changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textUserName.setVisibility(View.GONE);
                binding.editUserName.setVisibility(View.VISIBLE);
            }
        });
        binding.changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textPhoneNumber.setVisibility(View.GONE);
                binding.editPhoneNumber.setVisibility(View.VISIBLE);
            }
        });
        binding.changeImage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            });

            binding.saveName.setOnClickListener(v -> {
                String name = binding.editUserName.getText().toString();
                if (name.isEmpty()) {
                    binding.editUserName.setHint("Please type your name");
                    return;
                }
                progressDialog.show();
                if (selectedImage != null) {
                    StorageReference storageReference = storage.getReference().child("profile").child(auth.getUid());
                    storageReference.putFile(selectedImage).addOnCompleteListener((Task<UploadTask.TaskSnapshot> task) -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String uid = auth.getUid();
                                String phoneNumber = binding.editPhoneNumber.getText().toString();
                                String name1 = binding.editUserName.getText().toString();
                                String image = uri.toString();

                                User user = new User(uid, name1, phoneNumber, image, "online",name1);
                                database.getReference()
                                        .child("users")
                                        .child(uid)
                                        .setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            //                          progressDialog.show();
                                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                                        intent.putExtra("name2", name1);
//                                        intent.putExtra("pImage",image);
                                            // Log.d("L",image);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            finish();
                                        });
                            });
                        } else {
                            Log.d("T", "Else block execute");
                        }
                    });
                } else {
                    String uid = auth.getUid();
                    String phoneNumber = auth.getCurrentUser().getPhoneNumber();

                    User user = new User(uid, name, phoneNumber, "default", "online",name);
                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.show();
                                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                                intent.putExtra("name2",binding.nameBox.getText().toString());
//                                intent.putExtra("pImage",R.drawable.avatar);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            });
                }
            });
//        else {
//                binding.editUserName.setText(uname);
//                binding.editPhoneNumber.setText(unumber);
//
//                Glide.with(getApplicationContext()).load(uimage).into(binding.editUserProfile);
//                binding.changeImage.setVisibility(View.GONE);
//                binding.saveName.setVisibility(View.GONE);
//
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {
                binding.editUserProfile.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}