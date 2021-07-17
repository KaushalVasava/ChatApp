package com.kmv.chatapp.Adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmv.chatapp.Model.Chat;
import com.kmv.chatapp.Model.User;
import com.kmv.chatapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewholder>{

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private List<User> userList;
    private String last_message,message_time;
    private RecyclerListener listener;
    private Boolean isChat;

    public UserAdapter(ArrayList userList,RecyclerListener listener,Boolean isChat){
        this.userList = userList;
        this.listener=listener;
        this.isChat=isChat;
    }
    public void updatelist(ArrayList<User> item)
    {
        item.clear();
        item.addAll(userList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewholder viewHolder = new UserViewholder(view);
        return new UserViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewholder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getUsername());
        //messageTime(user.getUid(),holder.timestamp);
        if (user.getProfileImage().equals("default")) {
            holder.profile_img.setImageResource(R.drawable.avatar);
        }
        else {
            Glide.with(holder.profile_img.getContext()).load(user.getProfileImage()).into(holder.profile_img);
        }
        if(isChat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
            }
            else{
                holder.img_on.setVisibility(View.GONE);
             }
            lastMessage(user.getUid(),holder.last_msg);
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.last_msg.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(position,holder.name.getText().toString(),holder.profile_img.toString(),userList.get(position).getUid());
            }
        });
        holder.profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClick(position,holder.name.getText().toString(),holder.profile_img.toString(),userList.get(position).getPhoneNumber(),userList.get(position).getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewholder extends RecyclerView.ViewHolder {
        CircleImageView profile_img,img_on;
        TextView name,timestamp,last_msg;

        public UserViewholder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.userprofile);
            name = itemView.findViewById(R.id.username);
            last_msg = itemView.findViewById(R.id.lastMsg);
            img_on = itemView.findViewById(R.id.img_on);
          //  timestamp  = itemView.findViewById(R.id.msgTime);
        }
    }

    private void messageTime(final String userid,final TextView msg_time){
        //message_time ="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        message_time = chat.getTime();
                    }
                }
                if(message_time!=null){
                message_time = getTimeAgo(Long.parseLong(message_time));
                }
                msg_time.setText(message_time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        // String currentTime = Calendar.getInstance().getTimeInMillis();//getCurrentTime(ctx);
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }
        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";//MINUTE_MILLIS/SECOND_MILLIS+" seconds ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
    private void lastMessage(final String userid,final TextView last_msg){
        last_message="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()) ){
                        last_message = chat.getMessage();//ChatAdapter.decrypt(chat.getMessage());
                    }
                }
                switch (last_message){
                    case "default":last_msg.setText(""); break;
                    default:last_msg.setText(last_message); break;
                }
                last_message="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public interface RecyclerListener {
        void onClickItem(int pos,String name, String image,String uid);
        void onImageClick(int pos,String name,String image,String phoneNumber,String uid);
    }
}