package com.kmv.chatapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kmv.chatapp.Model.Chat;
import com.kmv.chatapp.R;

import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> list;
    private Context mContext;
    private String imageUrl;
    private FirebaseUser firebaseUser;

    public static final String SECRET_KEY
            = "my_super_secret_key_ho_ho_ho";

    public static final String SALT = "ssshhhhhhhhhhh!!!!";
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public ChatAdapter(Context mContext,List<Chat> list,String imageUrl) {
        this.mContext = mContext;
        this.list = list;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT){
            Log.d("T","left");
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        }
        else{
            Log.d("T","right");
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt)
    {
        try {

            // Default byte array
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0 };
            // Create IvParameterSpec object and assign with
            // constructor
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // Create SecretKeyFactory Object
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
                    "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    ivspec);
            // Return decrypted string
            return new String(cipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: "
                    + e.toString());
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat=list.get(position);

      //  Log.d("M","seen :"+chat.isIsseen());
        //String decryptMsg =decrypt(chat.getMessage());
        holder.show_message.setText(chat.getMessage());

        DateFormat simple = new SimpleDateFormat("hh:mm a");
       // String t = list.get(position).getTime();
        Date result = new java.sql.Date(Long.parseLong(list.get(position).getTime()));      // new Date(Long.parseLong(t));
        holder.message_time.setText(simple.format(result));
        //holder.message_time.setText(list.get(position).getTime());
        if(imageUrl.equals("default")){
            holder.profile_img.setImageResource(R.drawable.avatar);
        }
        else{
            Glide.with(mContext).load(imageUrl).into(holder.profile_img);
        }
        if(position == list.size()-1){
            Log.d("CH","seen: "+chat.isIsseen());
            if(chat.isIsseen()){
                holder.text_seen.setText("Seen");
            }
            else {
                holder.text_seen.setText("Delivered");
            }
        }
        else{
            holder.text_seen.setVisibility(View.GONE) ;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView show_message,message_time,text_seen;
        CircleImageView profile_img;
        public ViewHolder(View itemView){
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_img =itemView.findViewById(R.id.profile_img);
            message_time = itemView.findViewById(R.id.message_time);
            text_seen = itemView.findViewById(R.id.message_status);
        }
    }
}
