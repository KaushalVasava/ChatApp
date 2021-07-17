 package com.kmv.chatapp;

 import android.content.Context;
 import android.content.Intent;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.Bundle;
 import android.util.Log;

 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.kmv.chatapp.Adapter.UserAdapter;
 import com.kmv.chatapp.Model.User;

 import java.util.ArrayList;
 import java.util.HashMap;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 public class UserActivity extends AppCompatActivity {
     private RecyclerView recyclerView;
     private UserAdapter adapter;
     private Toolbar toolbar;
//     private TextView userName;
//     private CircleImageView userProfile;
     private ArrayList<User> userList;
     boolean isChat=true;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_user);

//         userName = findViewById(R.id.userName);
//         userProfile = findViewById(R.id.userProfile);
         recyclerView = findViewById(R.id.recyclerview);
         recyclerView.setHasFixedSize(true);
         toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         isChat = isConnected();

         userList = new ArrayList<>();
//         FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
//         reference.keepSynced(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//         reference.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 User user= snapshot.getValue(User.class);
//                 userName.setText(user.getUsername());
//                 if(user.getProfileImage().equals("default")){
//                     userProfile.setImageResource(R.drawable.avatar);
//                 }else{
//                     Glide.with(getApplicationContext()).load(user.getProfileImage()).into(userProfile);
//                 }
//             }
//             @Override
//             public void onCancelled(@NonNull DatabaseError error) {
//
//             }
//         });
         FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users");

         reference2.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 userList.clear();
                 for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                     User user2= dataSnapshot.getValue(User.class);
                     assert user2!=null;
                     //assert firebaseUser!=null;
                     if(!user2.getUid().equals(firebaseUser.getUid())){
                        userList.add(user2);
                     }
                 }
                 adapter = new UserAdapter(userList,new UserAdapter.RecyclerListener() {
                     @Override
                     public void onClickItem(int pos,String name, String image, String uid) {
                         Intent intent = new Intent(UserActivity.this, MessageActivity.class);
                         //intent.putExtra("images url","ne");
                         intent.putExtra("name", name);
                         intent.putExtra("image", image);
                         intent.putExtra("uid",uid);
                         startActivity(intent);
                     }

                     @Override
                     public void onImageClick(int pos, String name, String image, String phoneNumber,String uid) {

                     }
                 },isChat);
                 recyclerView.setAdapter(adapter);
                 adapter.notifyDataSetChanged();
                 //Log.d("M","end");
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

//         FirebaseRecyclerOptions<User> options =
//                 new FirebaseRecyclerOptions.Builder<User>()
//                         .setQuery(FirebaseDatabase.getInstance().getReference().child("users"),User.class)
//                         .build();
//
//         adapter = new UserAdapter(options, new UserAdapter.RecyclerListener() {
//             @Override
//             public void onClickItem(int pos,String name, String image, String uid) {
//                 Intent intent = new Intent(UserActivity.this, MessageActivity.class);
//                 //intent.putExtra("images url","ne");
//                 intent.putExtra("name", name);
//                 intent.putExtra("image", image);
//                 intent.putExtra("uid",uid);
//                 //Log.d("U",uid);
//                 startActivity(intent);
//             }
//         }, true){
//
//         };
//         recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//         recyclerView.setAdapter(adapter);
//         adapter.notifyDataSetChanged();
       }
     private void showStatus(String status){
         FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

         HashMap<String,Object> hashMap = new HashMap<>();
         hashMap.put("status",status);

         reference.updateChildren(hashMap);
     }
     private boolean isConnected(){
         try {
             ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
             NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
             return networkInfo!=null || networkInfo.isConnected();
         }
         catch (NullPointerException e){
             e.printStackTrace();
             return false;
         }
     }
     @Override
     protected void onResume() {
         super.onResume();
         isChat = isConnected();
         if(isChat) {
             Log.d("N","OnRestart - true");
             showStatus("online");
         }
         else{      Log.d("N","OnRestart - false");
             showStatus("offline");
         }
     }

     @Override
     protected void onRestart() {
         super.onRestart();
//         if(String.valueOf(isConnected()).isEmpty())
         isChat = isConnected();
         if(isChat) {
             Log.d("N","OnRestart - true");
             showStatus("online");
         }
         else{
             Log.d("N","onRestart - false");
             showStatus("offline");
         }
//         adapter.startListening();
     }
     @Override
     protected void onPause() {
         super.onPause();
         showStatus("offline");
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         showStatus("offline");
         //adapter.stopListening();
     }
 }