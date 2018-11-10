package com.example.grupp4.a4chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    Adapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ListItem listItem = listItems.get(i);

        viewHolder.imageViewAvatar.setImageDrawable(listItem.getAvatar());
        viewHolder.textViewUser.setText(listItem.getUser());
        viewHolder.textViewDate.setText(listItem.getDate());
        viewHolder.textViewMessage.setText(listItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewAvatar;
        public TextView textViewUser;
        public TextView textViewDate;
        public TextView textViewMessage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatar);
            textViewUser = (TextView) itemView.findViewById(R.id.textViewUser);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
        }
    }

}
