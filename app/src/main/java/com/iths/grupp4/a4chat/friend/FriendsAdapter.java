package com.example.grupp4.a4chat.friend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grupp4.a4chat.R;
import com.example.grupp4.a4chat.allusers.AllUsers;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FriendsAdapter extends FirestoreRecyclerAdapter<AllUsers, FriendsAdapter.FriendsHolder> {

    private OnItemClicklistener onItemClicklistener;

    public FriendsAdapter(@NonNull FirestoreRecyclerOptions<AllUsers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendsHolder holder, int position, @NonNull AllUsers model) {
        holder.textViewUsername.setText(String.valueOf(model.getName()));
        holder.textViewEmail.setText(model.getEmail());
        holder.setAvatar(model.getAvatar());

    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_user_list_display, viewGroup, false);
        return new FriendsAdapter.FriendsHolder(view);
    }

    class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewEmail;
        ImageView imageViewAvatar;

        public FriendsHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsername = itemView.findViewById(R.id.all_user_username);
            textViewEmail = itemView.findViewById(R.id.all_user_userEmail);
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
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.all_user_profile_image);
            Picasso.get().load(avatar).into(imageViewAvatar);
        }
    }

    public interface OnItemClicklistener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(FriendsAdapter.OnItemClicklistener listener) {
        onItemClicklistener = listener;
    }
}
