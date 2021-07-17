package com.kmv.chatapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallations;
import com.kmv.chatapp.MessageActivity;
import com.kmv.chatapp.Model.Chat;
import com.kmv.chatapp.Model.Chatlist;
import com.kmv.chatapp.ProfileActivity;
import com.kmv.chatapp.R;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.Adapter.UserAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> mUsers;
    private List<String> userList;
    private List<Chatlist> usersList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private boolean isChat = true;
//    ChatFragment(boolean isChat){
//        this.isChat=isChat;
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        isChat = isConnected(view.getContext());

        usersList = new ArrayList<>();
        userList = new ArrayList<>();
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //updateToken(FirebaseInstanceId.getInstance().getToken());
//        updateToken(String.valueOf(FirebaseInstallations.getInstance().getToken(true)));
//        reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid());
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                userList.clear();
//
//                for(DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//
//                    if(chat.getSender().equals(firebaseUser.getUid())){
//                        userList.add(chat.getReceiver());
//                    }
//                    if(chat.getReceiver().equals(firebaseUser.getUid())){
//                        userList.add(chat.getSender());
//                    }
//                }
//                chatList();
//                // readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return view;
    }
    private void readChats()
    {
       mUsers = new ArrayList<>();
       reference = FirebaseDatabase.getInstance().getReference("users");

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               mUsers.clear();
               for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                   User user = dataSnapshot.getValue(User.class);
                   for (String id : userList) {
                       if (user.getUid().equals(id)) {
                           if (mUsers.size() != 0) {
                               for (User user1 : mUsers) {
                                   if (!user.getUid().equals(user1.getUid())) {
                                       mUsers.add(user);
                                   }
                               }
                           }
                           else {
                               mUsers.add(user);
                           }
                       }
                   }
               }
               adapter = new UserAdapter(mUsers, new UserAdapter.RecyclerListener() {
                   @Override
                   public void onClickItem(int pos, String name, String image, String uid) {
                       Intent intent = new Intent(getContext(), MessageActivity.class);
                       //intent.putExtra("images url","ne");
                       intent.putExtra("name", name);
                       intent.putExtra("image", image);
                       intent.putExtra("uid",uid);
                       startActivity(intent);
                   }

                   @Override
                   public void onImageClick(int pos, String name, String image, String phoneNumber, String uid) {
                       Intent intent = new Intent(getContext(), ProfileActivity.class);
                       //intent.putExtra("images url","ne");
                       intent.putExtra("name", name);
                       intent.putExtra("image", image);
                       intent.putExtra("number",phoneNumber);
                       intent.putExtra("uid",uid);
                       startActivity(intent);
                   }
               },true);
               recyclerView.setAdapter(adapter);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    boolean isConnected(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo!=null || networkInfo.isConnected();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }
    private void showStatus(String status){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }
    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList) {
                        Log.d("IU", "id1 : " + user.getUid());
                        Log.d("IU", "id2 : " + chatlist.getReceiverId());
                        Log.d("IU","id3 :"+firebaseUser.getUid());
                        Log.d("IU","id4:"+chatlist.getSenderId());
                        if (user.getUid().equals(chatlist.getReceiverId())){ //&& firebaseUser.getUid().equals(chatlist.getSenderId())) {
                            Log.d("IF","if 1");
                            mUsers.add(user);
                        }

//                        if(!firebaseUser.getUid().equals(chatlist.getSenderId()) && user.getUid().equals(chatlist.getSenderId())){
//                            Log.d("IF","if 2");
//                            mUsers.add(user);
//                        }
                    }
                }

                adapter = new UserAdapter(mUsers, new UserAdapter.RecyclerListener() {
                    @Override
                    public void onClickItem(int pos, String name, String image, String uid) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        //intent.putExtra("images url","ne");
                        intent.putExtra("name", name);
                        intent.putExtra("image", image);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }

                    @Override
                    public void onImageClick(int pos, String name, String image, String phoneNumber, String uid) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        //intent.putExtra("images url","ne");
                        intent.putExtra("name", name);
                        intent.putExtra("image", image);
                        intent.putExtra("number",phoneNumber);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }
                },true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void updateToken(String token){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        reference.child(firebaseUser.getUid()).setValue(token1);
//    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PR","onResume");

    }

    @Override
    public void onStart() {
        super.onStart();
       // Log.d("PR","onStart");
    }

}



//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        isChat = isConnected(getContext());
//        if(isChat) {
//            Log.d("N","OnRestart - true");
//            showStatus("online");
//        }
//        else{
//            Log.d("N","onRestart - false");
//            showStatus("offline");
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        showStatus("offline");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        showStatus("offline");
//    }
//}