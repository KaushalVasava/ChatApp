package com.kmv.chatapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kmv.chatapp.Model.Chat;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.databinding.ActivityMainBinding;
import com.kmv.chatapp.Adapter.FragmentAdapter;
import com.kmv.chatapp.utils.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FragmentAdapter adapter;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    public static final int REQUEST_READ_CONTACTS = 79;
    ArrayList mobileArray;

    String userName;

    boolean isChat=true;
//    BroadcastReceiver broadcastReceiver;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarmain.toolbarmain);
        isChat = isConnected();

        FirebaseMessaging.getInstance().subscribeToTopic("userABC");
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null)
            FirebaseMessaging.getInstance().subscribeToTopic(uid);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllContacts();
//            Log.d("N","size :"+mobileArray.size() );
//            for(int i =0;i<mobileArray.size();i++){
//                Log.d("N","phone : "+mobileArray.get(i));
//            }
        } else {
            requestPermission();
        }
        //broadcastReceiver = new NetworkChangeReceiver();
        //registerNetworkBroadcastReceiver();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                SharedPref shared = new SharedPref(MainActivity.this);
                shared.setUserName( user.getUsername());
                binding.toolbarmain.userName.setText(user.getUsername());
                if(user.getProfileImage().equals("default")){
                    binding.toolbarmain.userProfile.setImageResource(R.drawable.avatar);
                }else{
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(binding.toolbarmain.userProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.toolbarmain.toolbarmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle(),mobileArray);

//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                adapter = new FragmentAdapter(fm, getLifecycle());
//                int unread = 0;
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
//                        binding.viewPager2
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        binding.viewPager2.setAdapter(adapter);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chats"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Users"));
      //  binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Settings"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
               // if(tab.getPosition()!=2){
                    changeFabIcon(tab.getPosition());
                //}

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }
//    protected void registerNetworkBroadcastReceiver(){
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
//            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
//            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//    }
public void requestPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS)) {
        // show UI part if you want here to show some rationale !!!
    } else {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                REQUEST_READ_CONTACTS);
    }
    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS)) {
    } else {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                REQUEST_READ_CONTACTS);
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //if("sonal".equals(name)){
                //nameList.add(name);
                //}
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.d("P","Ph no: "+phoneNo);

                        phoneNo = phoneNo.replaceAll("\\s", "");

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

    protected void unregisterNetwork(){
        try{
       //     unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
      //  SearchView searchView = (SearchView) searchItem.getActionView();

//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.geFilter().filter(newText);
//                return false;
//            }
//        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText( this,"Search", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.settings:
//                Toast.makeText( this,"Setting", Toast.LENGTH_SHORT).show();

              //  break;
            default:super.onOptionsItemSelected(item);
        }

        return true;
    }
    private void changeFabIcon(final int index){
        binding.fabAction.hide();
        //   binding.btnAddStatus.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                switch (index){
//                    case 0 :      binding.fabAction.hide(); break;
                    case 0 :
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_add_chat));
                        binding.fabAction.show();
                        break;
                    case 1:
                    //    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_add_call));
                      //  binding.fabAction.show();
                        binding.fabAction.setVisibility(View.GONE);
                        break;
//                    case 2:
//                        binding.fabAction.setVisibility(View.GONE);
//                        break;
                }

            }
        },300);
        //performOnClick(index);
    }
    private void showStatus(String status){
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }
    boolean isConnected(){
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
//    @Override
//    protected void onResume() {
//        super.onResume();
//        showStatus("online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        showStatus("online");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        showStatus("offline");
//    }
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
        isChat = isConnected();
        if(isChat) {
            Log.d("N","OnRestart - true");
            showStatus("online");
        }
        else{
            Log.d("N","onRestart - false");
            showStatus("offline");
        }
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
        //unregisterNetwork();
    }
}