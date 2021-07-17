package com.kmv.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.kmv.chatapp.MainActivity;
import com.kmv.chatapp.R;
import com.kmv.chatapp.Model.User;

/**
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    EditText editUserName;
    CircleImageView editUserProfile;
    TextView editName;
    Button save;
    Uri selectedImage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        save = view.findViewById(R.id.saveName);
        editName = view.findViewById(R.id.editName);
        editUserName = view.findViewById(R.id.editUserName);
        editUserProfile = view.findViewById(R.id.editUserProfile);

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                editUserName.setText(user.getUsername());
                if(user.getProfileImage().equals("default")){
                    editUserProfile.setImageResource(R.drawable.avatar);
                }
                else {
                  //  Glide.with(getContext()).load(user.getProfileImage()).into(editUserProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });
        save.setOnClickListener(v -> {
            String name=editUserName.getText().toString();
            if(name.isEmpty() ){
                editUserName.setHint("Please type your name");
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
                            String name1 = editUserName.getText().toString();
                            String image = uri.toString();

                            User user = new User(uid, name1, phoneNumber, image,"online",name1);
                            database.getReference()
                                    .child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        //                          progressDialog.show();
                                        Intent intent = new Intent(view.getContext(), MainActivity.class);
//                                        intent.putExtra("name2", name1);
//                                        intent.putExtra("pImage",image);
                                        // Log.d("L",image);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        //finish();
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

                User user=new User(uid,name,phoneNumber,"default","online",name);
                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.show();
                                Intent intent=new Intent(view.getContext(), MainActivity.class);
//                                intent.putExtra("name2",binding.nameBox.getText().toString());
//                                intent.putExtra("pImage",R.drawable.avatar);
                                startActivity(intent);
                                progressDialog.dismiss();
                               // finish();
                            }
                        });
            }
        });
       return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                editUserProfile.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }
}

   /*

    EditText editUserName;
    CircleImageView editUserProfile;
    TextView editName;
    ProgressBar progressBar;
    Button save;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri selectedImage;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        save = view.findViewById(R.id.saveName);
        editName = view.findViewById(R.id.editName);
        editUserName = view.findViewById(R.id.editUserName);
        editUserProfile = view.findViewById(R.id.editUserProfile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               User user=snapshot.getValue(User.class);
               editUserName.setText(user.getUsername());
               if(user.getProfileImage().equals("default")){
                   editUserProfile.setImageResource(R.drawable.avatar);
               }
               else {
                   Glide.with(getContext()).load(user.getProfileImage()).into(editUserProfile);
                  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference storageReference = storage.getReference().child("profile").child(auth.getUid());
                storageReference.putFile(selectedImage).addOnCompleteListener((Task<UploadTask.TaskSnapshot> task) -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(),"Change Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading..");
        pd.show();
        if(selectedImage!=null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(selectedImage));
            uploadTask = fileReference.putFile(selectedImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                editUserProfile.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
                  data!=null && data.getData()!=null){
                editUserProfile.setImageURI(data.getData());
                selectedImage=data.getData();
                if(uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(getContext(),"Upload is in progress",Toast.LENGTH_SHORT).show();
                }
                else {
                   uploadImage();
                }
                Glide.with(getContext()).load(selectedImage).into(editUserProfile);
        }
    }
}*/