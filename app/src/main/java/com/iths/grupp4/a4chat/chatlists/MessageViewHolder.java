package com.iths.grupp4.a4chat.chatlists;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firestore.admin.v1beta1.Index;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.iths.grupp4.a4chat.allusers.AllUserProfileFragment;
import com.iths.grupp4.a4chat.photos.FullScreenDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "MessageViewHolder";

    public View itemView;
    public TextView textUser;
    public TextView textMessage;
    public ImageView imageUser;
    public ImageView imageMessage;
    public TextView textTime;
    public ProgressBar progressBar;
    public int viewType;
    String visit_user_id;
    Context mContext;

    final int radius = 25;
    final int margin = 25;
    final Transformation transformation = new RoundedCornersTransformation(radius, margin);

    public MessageViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        this.itemView = itemView;
        this.viewType = viewType;

        textUser = itemView.findViewById(R.id.textUser);
        textMessage = itemView.findViewById(R.id.textMessage);
        imageMessage = itemView.findViewById(R.id.imageMessage);
        imageUser = itemView.findViewById(R.id.imageUser);
        textTime = itemView.findViewById(R.id.textTime);
        progressBar = itemView.findViewById(R.id.progressBar);

    }

    public void setData(Message message) {
        String userStyle = message.user + ":";
        textUser.setText(userStyle);
        textMessage.setText(message.message);
    }

    public void setData(MessageUserRef messageUserRef) {
        if (viewType == 2) {
            messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    textUser.setText((String) documentSnapshot.get("name"));
                    String avatar = (String) documentSnapshot.get("avatar");
                    Picasso.get().load(avatar)
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CropCircleTransformation())
                            .into(imageUser);
                }
            });
        }
        textMessage.setText(messageUserRef.message);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }
    }

    public void setDataSent(MessageUserRef messageUserRef) {
        textMessage.setText(messageUserRef.message);
        textMessage.setTextIsSelectable(true);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }

       textMessage.setOnClickListener(view -> {
           if (!messageUserRef.message.equals("")) {
               textMessage.setCustomSelectionActionModeCallback(mCopyPasteCallBack);
           }
       });

    }

    public void setDataReceived(MessageUserRef messageUserRef) {
        messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textUser.setText((String) documentSnapshot.get("name"));
                String avatar = (String) documentSnapshot.get("avatar");
                visit_user_id = (String) documentSnapshot.get("userId");
                Picasso.get().load(avatar)
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CropCircleTransformation())
                        .into(imageUser);
            }
        });
        textMessage.setText(messageUserRef.message);
        textMessage.setTextIsSelectable(true);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }

        textMessage.setOnClickListener(view -> {
            if (!messageUserRef.message.equals("")) {
                Log.d(TAG, "setDataReceived: on clicxkkd");
                textMessage.setCustomSelectionActionModeCallback(mCopyPasteCallBack);
            }
        });

        imageUser.setOnClickListener(view -> {
            redirectToProfil(visit_user_id);
        });
    }

    public void setDataImageSent(MessageUserRef messageUserRef) {

        Date date = messageUserRef.timeStamp;
        Log.d(TAG, "setDataImageSent:  date is " + date);

        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }

        if (messageUserRef.message.equals("default")) {
            showLoader();
            Picasso.get()
                    .load(R.drawable.default_avatar)
                    .resize(600, 300).centerCrop()
                    .transform(transformation)
                    .into(imageMessage);
        } else {
            setChatImage(messageUserRef.message, imageMessage);
            hideLoader();
        }

        imageMessage.setOnClickListener(view -> displayFullsizeAvatar(messageUserRef.message));

    }

    public void setDataImageReceived(MessageUserRef messageUserRef) {
        messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textUser.setText((String) documentSnapshot.get("name"));
                String avatar = (String) documentSnapshot.get("avatar");
                visit_user_id = (String) documentSnapshot.get("userId");
                Picasso.get().load(avatar)
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CropCircleTransformation())
                        .into(imageUser);
            }
        });

        setChatImage(messageUserRef.message, imageMessage);

        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }
        if (imageMessage.getDrawable() != null) {
            imageMessage.setOnClickListener(view -> displayFullsizeAvatar(messageUserRef.message));
        }
        imageUser.setOnClickListener(view -> redirectToProfil(visit_user_id));
    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hides the progressbar when loading finished
    private void hideLoader() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void displayFullsizeAvatar(String imageUrl) {
        FullScreenDialog dialog = new FullScreenDialog();
        Bundle bundle = new Bundle();
        bundle.putString("image_url", imageUrl);
        dialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = MainActivity.sFragmentManager.beginTransaction();
        dialog.show(fragmentTransaction, FullScreenDialog.TAG);
    }

    private void setChatImage(String imageUrl, ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .resize(600, 300).centerCrop()
                .transform(transformation)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.errorimage)
                                .transform(transformation)
                                .resize(600, 300).centerCrop()
                                .into(imageView);

                    }
                });
    }

    private void redirectToProfil(String userToVisit) {
        Bundle bundle = new Bundle();
        bundle.putString("visit_user_id", visit_user_id);
        if (visit_user_id != null) {
            FragmentTransaction fragmentTransaction = MainActivity.sFragmentManager.beginTransaction();
            AllUserProfileFragment fragment = new AllUserProfileFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        }
    }

    private ActionMode.Callback mCopyPasteCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.removeItem(android.R.id.cut);
            menu.removeItem(android.R.id.replaceText);
            menu.removeItem(android.R.id.shareText);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.copy_paste, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_copy:
                    copyText();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
        }
    };

    private void copyText() {
        ClipboardManager clipboardManager = MainActivity.sClipboardManager;
        CharSequence selectedTxt = textMessage.getText().subSequence(textMessage.getSelectionStart(), textMessage.getSelectionEnd());
        ClipData clipData = ClipData.newPlainText("text", selectedTxt);
        clipboardManager.setPrimaryClip(clipData);
    }



}