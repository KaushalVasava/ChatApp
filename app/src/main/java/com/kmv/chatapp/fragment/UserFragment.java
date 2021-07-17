package com.kmv.chatapp.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kmv.chatapp.MainActivity;
import com.kmv.chatapp.MessageActivity;
import com.kmv.chatapp.ProfileActivity;
import com.kmv.chatapp.R;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.Adapter.UserAdapter;

import java.util.ArrayList;

/**
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    public static final int REQUEST_READ_CONTACTS = 79;
    ArrayList mobileArray;

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> userList;
    EditText search_user;
    Context context;

    public UserFragment(ArrayList phoneNumbers){
        mobileArray=phoneNumbers;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        search_user = view.findViewById(R.id.searchUser);
        recyclerView = view.findViewById(R.id.user_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

//        MainActivity mainActivity =new MainActivity();
//
//        if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.READ_CONTACTS)
//                == PackageManager.PERMISSION_GRANTED) {
//            mobileArray = getAllContacts();
//            Log.d("N","size :"+mobileArray.size() );
//            for(int i =0;i<mobileArray.size();i++){
//                Log.d("N","phone number : "+mobileArray.get(i));
//            }
//        } else {
//            mainActivity.requestPermission();
//        }
//        mobileArray = getAllContacts();
//        Log.d("M","size :"+mobileArray.size() );
//        for(int i =0;i<mobileArray.size();i++){
//            Log.d("M","phone : "+mobileArray.get(i));
//        }
        readUsers();

        userList = new ArrayList<>();
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
    //getcontacts methods
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                   // nameList.add(name);
                   Log.d("D","P NAME : "+name);
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d("D","Pno. : "+phoneNo);
                        nameList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    //users methods
    private void readUsers(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user2= dataSnapshot.getValue(User.class);
                    assert user2!=null;
                   // for(int i =0;i<mobileArray.size();i++) {
                        //assert firebaseUser!=null;
                        Log.d("A", "phone1 : " + user2.getPhoneNumber());

                      //  Log.d("A", "phone2 :" + mobileArray.get(i));
//                    Log.d("A","phone2 :"+mobileArray.get(i));
                    //    if (user2.getPhoneNumber().equals(mobileArray.get(i))) {
                            if (!user2.getUid().equals(firebaseUser.getUid())) {
                                //         for(int i=0;i<mobileArray.size();i++) {
                      //          Log.d("A", "phone2 :" + mobileArray.get(i));
                                //           if(user2.getPhoneNumber().equals(mobileArray.get(i)))
                                userList.add(user2);
                              //  break;
                            }
                        //}
                    //}
                }

                adapter = new UserAdapter(userList,new UserAdapter.RecyclerListener() {
                    @Override
                    public void onClickItem(int pos,String name, String image, String uid) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        //intent.putExtra("images url","ne");
                        intent.putExtra("name", name);
                        intent.putExtra("image", image);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }

                    @Override
                    public void onImageClick(int pos, String name, String image, String phoneNumber,String uid) {
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
              //  adapter.updatelist(userList);
                adapter.notifyDataSetChanged();
                //Log.d("M","end");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void searchUsers(String s){
       FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              //if(search_user.getText().toString().equals("")) {
                  userList.clear();
                  for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                      User user = dataSnapshot.getValue(User.class);
                      assert user != null;
                      assert firebaseUser!=null;
                      if (!user.getUid().equals(firebaseUser.getUid())) {
                          userList.add(user);
                      }
                  }
                  adapter = new UserAdapter(userList, new UserAdapter.RecyclerListener() {
                      @Override
                      public void onClickItem(int pos, String name, String image, String uid) {

                      }

                      @Override
                      public void onImageClick(int pos, String name, String image, String phoneNumber, String uid) {

                      }
                  }, false);
                  recyclerView.setAdapter(adapter);
              //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void showStatus(String status){
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
//
//        HashMap<String,Object> hashMap = new HashMap<>();
//        hashMap.put("status",status);
//
//        reference.updateChildren(hashMap);
//    }
}