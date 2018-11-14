package com.example.grupp4.a4chat.allusers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grupp4.a4chat.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class AllUserAdapter extends FirestoreRecyclerAdapter<AllUsers, AllUserAdapter.AllUsersHolder> {

    public AllUserAdapter(@NonNull FirestoreRecyclerOptions<AllUsers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AllUsersHolder holder, int position, @NonNull AllUsers model) {
        holder.textViewUsername.setText(model.getName());
        holder.textViewEmail.setText(model.getEmail());

    }

    @NonNull
    @Override
    public AllUsersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_user_list_display, viewGroup, false);
        return new AllUsersHolder(view);
    }

    class AllUsersHolder extends RecyclerView.ViewHolder{
        TextView textViewUsername;
        TextView textViewEmail;
        ImageView imageViewAvatar;

        public AllUsersHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.all_user_username);
            textViewEmail = itemView.findViewById(R.id.all_user_userEmail);
        }
    }

}
