package com.iths.grupp4.a4chat.friend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUsers;
import com.squareup.picasso.Picasso;

public class RequestAdapter extends FirestoreRecyclerAdapter<Friends, RequestAdapter.RequestHolder> {

    private OnItemClicklistener onItemClicklistener;

    public RequestAdapter(@NonNull FirestoreRecyclerOptions<Friends> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder holder, int position, @NonNull Friends model) {
        holder.textViewUsername.setText(String.valueOf(model.getName()));
        holder.textViewEmail.setText(model.getEmail());
        holder.setAvatar(model.getAvatar());
        holder.setOnline(model.isOnline());

    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_display, viewGroup, false);
        return new RequestAdapter.RequestHolder(view);
    }

    class RequestHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewEmail;
        ImageView imageViewAvatar;
        ImageView onlineIcon;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsername = itemView.findViewById(R.id.request_user_username);
            textViewEmail = itemView.findViewById(R.id.request_user_userEmail);
            onlineIcon = itemView.findViewById(R.id.statusIcon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClicklistener != null) {
                        onItemClicklistener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        public void setAvatar(String avatar) {
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.request_user_profile_image);
            Picasso.get().load(avatar).into(imageViewAvatar);
        }

        public void setOnline(boolean online){
            if (online == true){
                onlineIcon.setImageResource(R.drawable.ic_online_icon);
            }else {
                onlineIcon.setImageResource(R.drawable.ic_offline_icon);
            }
        }
    }

    public interface OnItemClicklistener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(RequestAdapter.OnItemClicklistener listener) {
        onItemClicklistener = listener;
    }
}
