package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.iths.grupp4.a4chat.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PmViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    private TextView textViewName;
    private TextView textViewCreatedOn;
    private ImageView imageUser;
    private ImageView onlineIcon;

    PmViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewName = itemView.findViewById(R.id.chatroom_item_name);
        textViewCreatedOn = itemView.findViewById(R.id.chatroom_item_createdOn);
        imageUser = itemView.findViewById(R.id.imageUser);
        onlineIcon = itemView.findViewById(R.id.statusIcon);
    }

    public void setData(Chatroom chatroom) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (chatroom.user2 != null) {
            chatroom.user2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String user2Id = (String) documentSnapshot.get("userId");
                    if (!userId.equals(user2Id)) {
                        String name = documentSnapshot.getString("name");
                        String avatar = documentSnapshot.getString("avatar");
                        textViewName.setText(name);
                        Picasso.get().load(avatar)
                                .placeholder(R.drawable.default_avatar)
                                .transform(new CropCircleTransformation())
                                .into(imageUser);
                        if (documentSnapshot.getBoolean("online") != null){
                            setOnline(documentSnapshot.getBoolean("online"));
                        }
                    }
                }
            });
        }
        if (chatroom.user1 != null) {
            chatroom.user1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String user1Id = documentSnapshot.getString("userId");
                    if (!userId.equals(user1Id)) {
                        String name = documentSnapshot.getString("name");
                        String avatar = documentSnapshot.getString("avatar");
                        textViewName.setText(name);
                        Picasso.get().load(avatar)
                                .placeholder(R.drawable.default_avatar)
                                .transform(new CropCircleTransformation())
                                .into(imageUser);
                        if (documentSnapshot.getBoolean("online") != null){
                            setOnline(documentSnapshot.getBoolean("online"));
                        }
                    }
                }
            });
        }
        Date date = chatroom.recentMessage;
        if (date == null){
            date = chatroom.timeStamp;
        }
        textViewCreatedOn.setText("");
        if(date !=null) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
        String time = dateFormat.format(date);
        textViewCreatedOn.setText(time);
    }

}
    public void setOnline(boolean online){
        if (online == true){
            onlineIcon.setImageResource(R.drawable.ic_online_icon);
        }else {
            onlineIcon.setImageResource(R.drawable.ic_offline_icon);
        }
    }
}
