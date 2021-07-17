package com.kmv.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmv.chatapp.Adapter.ChatAdapter;
import com.kmv.chatapp.Model.Chat;
import com.kmv.chatapp.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    private static final String SECRET_KEY
            = "my_super_secret_key_ho_ho_ho";

    private static final String SALT = "ssshhhhhhhhhhh!!!!";

    String TAG = "MessageActivity";
    public final String SERVER_KEY="AAAAPk5_Nus:APA91bEaT_bRrqLlSbf-9ziekuW6E505OLJwFAAmBHUUpNbtPTbOhjQht0VA3yya_stRl4i78EUU-5AZUPEgfPvxUpXQRij2D-vLjSlaktudiERTxp_OR5SYlFHLDyiKKNZ8YygPFifF";
    String currentImagePath = null;
    private static final int IMAGE_REQUEST = 1;
    TextView username;
    EditText messagebox;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView btnsend;
    CircleImageView profile_image,img_on,img_off;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    ChatAdapter adapter;
    List<Chat> list;
    String name,userImage,receiverId;
    ValueEventListener eventListener;
    boolean isChat =true,notify=false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //t=findViewById(R.id.receiverMsg);
        username = findViewById(R.id.username); //username
        profile_image=findViewById(R.id.userImage);//profile image
        btnsend = findViewById(R.id.sendBtn);//send button
        messagebox=findViewById(R.id.messageBox); //send text
        img_on = findViewById(R.id.img_on);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");
        username.setText(name);
        receiverId=getIntent().getStringExtra("uid");

        isChat = isConnected();
        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        recyclerView= findViewById(R.id.recyclerViewbaat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManage=new LinearLayoutManager(getApplicationContext());
        linearLayoutManage.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManage) ;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                //intent.putExtra("images url","ne");
                intent.putExtra("name", name);
                intent.putExtra("image", userImage);
                intent.putExtra("number",firebaseUser.getPhoneNumber());
                intent.putExtra("uid",receiverId);
                startActivity(intent);
            }
        });

        btnsend.setOnClickListener(v -> {
            notify=true;
            String msg = messagebox.getText().toString();
            Log.d("M","message : "+msg);

        //    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        //    String msgTime = dateFormat.format(new Date());
            String msgTime = String.valueOf(System.currentTimeMillis());
            // String msgTime = getTimeAgo(time);
            if(!msg.equals("")){
                sendMessage(firebaseUser.getUid(),receiverId,msg,msgTime);
               // getToken(msg,receiverId,userImage,receiverId);
            }
            else{
                 Toast.makeText(MessageActivity.this,"You can't send empty message",Toast.LENGTH_SHORT).show();
            }
            messagebox.setText("");
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if(isChat){
                    if(user.getStatus().equals("online")){
                        img_on.setVisibility(View.VISIBLE);
                    }
                    else{
                        img_on.setVisibility(View.GONE);
                    }
                }else {
                    img_on.setVisibility(View.GONE);
                }
                if(user.getProfileImage().equals("default")){
                   profile_image.setImageResource(R.drawable.avatar);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profile_image);
                }
                readMessage(firebaseUser.getUid(),receiverId,user.getProfileImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageStatus(receiverId);
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
    public void clickImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.kmv.android.fileprovider-chatapp", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);
            }
        }
        // Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        //imageView.setImageBitmap(bitmap);
    }
    private File getImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
    private void setImage(String imageUrl, String imageName){
        Log.d("TAG", "setImage: setting te image and name to widgets.");

        //TextView name = findViewById(R.id.lastMsg);
        //name.setText(imageName);

        ImageView image = findViewById(R.id.image);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String encrypt(String strToEncrypt)
    {
        try {

            // Create default byte array
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // Create SecretKeyFactory object
            SecretKeyFactory factory
                    = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256");

            // Create KeySpec object and assign with
            // constructor
            KeySpec spec = new PBEKeySpec(
                    SECRET_KEY.toCharArray(), SALT.getBytes(),
                    65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(
                    tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(
                    "AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    ivspec);
            // Return encrypted string
            return Base64.getEncoder().encodeToString(
                    cipher.doFinal(strToEncrypt.getBytes(
                            StandardCharsets.UTF_8)));
        }
        catch (Exception e) {
            System.out.println("Error while encrypting: "
                    + e.toString());
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(String sender, String receiver, String message, String msgTime){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        Notification notification=new Notification();
        notification.onMes(getApplicationContext(),receiverId,"NEW MESSAGE",message);

        String enMsg = encrypt(message);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("time",msgTime);
        hashMap.put("isSeen",false);

        reference.child("Chats").push().setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiverId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("receiverId").setValue(receiverId);
                    chatRef.child("senderId").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiverId)
                .child(firebaseUser.getUid());

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef2.child("receiverId").setValue(firebaseUser.getUid());
                    chatRef2.child("senderId").setValue(receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg=message;
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotifiaction(receiver, user.getUsername(), msg);
//                }
//                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    private void sendNotifiaction(String receiver,String username,String message){
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
//                            receiverId);
//
//                    Log.d(TAG,"data : "+data.getTitle()+message);
//                    Sender sender = new Sender(data, token.getToken());
//
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if (response.code() == 200){
//                                        if (response.body().success != 1){
//                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    private void readMessage(String myid,String userid,String imageurl){
        list=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        list.add(chat);
                    }
                    adapter = new ChatAdapter(MessageActivity.this, list, imageurl);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                for(int i=0;i<list.size();i++){
                    Log.d("M","list "+list.get(i).isIsseen());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setStatus(String status){
        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    private void messageStatus(final String userid)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        //Log.d("B","seen ");
                        Log.d("M","seen");
                        hashMap.put("isSeen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getToken(String message,String receiverId,String receiverImage,String chatId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(receiverId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String name = snapshot.child("name").getValue().toString();

                JSONObject to=new JSONObject();
                JSONObject data=new JSONObject();
                try{
                    data.put("title",name);
                    data.put("message",message);
                    data.put("receiverId",receiverId);
                    data.put("receiverImage",receiverImage);
                    data.put("chatId",chatId);

                    to.put("to",token);
                    to.put("data",data);

                  //  sendNotification(to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    private void sendNotification(JSONObject to) {
//        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,"https://fcm.googleapis.com/fcm/send",to,response -> {
//            Log.d("NO","sendNotification : "+response);
//        },error -> {
//            Log.d("NO","sendNotification : "+error);
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String,String> map = new HashMap<>();
//                map.put("Authorixation","key="+SERVER_KEY);
//                map.put("Content-Type","application/json");
//                return super.getHeaders();
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//              //  return super.getBodyContentType();
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        request.setRetryPolicy(new DefaultRetryPolicy(30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
//        requestQueue.add(request);
//    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
   //     currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(eventListener);
        status("offline");
     //   currentUser("none");
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if(String.valueOf(isConnected()).isEmpty())
//        isChat = isConnected();
//        if(isChat) {
//            Log.d("N","OnRestart-2 - true");
//            setStatus("online");
//        }
//        else{
//            Log.d("N","OnRestart-2 - false");
//            setStatus("offline");
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users");
//        reference2.removeEventListener(eventListener);
//        setStatus("offline");
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        setStatus("offline");
//        //adapter.stopListening();
//    }
}