package com.iths.grupp4.a4chat.chatroomlists;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iths.grupp4.a4chat.R;

public class ChatroomDialogRemove extends DialogFragment {

    TextView textViewRemove;
    Button buttonOk;
    Button buttonCancel;

    private static final String TAG = "ChatroomDialogRemove";
    int position;

    public interface OnRemoveChatroomListener {
        void removeChatroom(int position);
    }

    OnRemoveChatroomListener onRemoveChatroomListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chatroom_remove, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("Position", 0);
        }

        textViewRemove = (TextView) view.findViewById(R.id.dialog_chatroom_remove_textview);
        buttonOk = (Button) view.findViewById(R.id.dialog_chatroom_remove_okButton);
        buttonCancel = (Button) view.findViewById(R.id.dialog_chatroom_remove_cancelButton);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRemoveChatroomListener.removeChatroom(position);

                getDialog().dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try {
            onRemoveChatroomListener = (ChatroomDialogRemove.OnRemoveChatroomListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }
}
