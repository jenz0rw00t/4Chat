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
import android.widget.EditText;

import com.iths.grupp4.a4chat.R;

public class ChatroomNameDialog extends DialogFragment {

    EditText editTextChatName;
    Button buttonSaveButton;

    private static final String TAG = "ChatNameDialog";
    String chatroomName;
    String chatroomId;

    public interface OnNameReceivedListener {
        void getName(String chatroomId, String chatroomName);
    }

    OnNameReceivedListener mOnNameReceivedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chatname, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            chatroomId = bundle.getString("ChatroomId", null);
        }

        editTextChatName = (EditText) view.findViewById(R.id.dialog_chatname_chatName);
        buttonSaveButton = (Button) view.findViewById(R.id.dialog_chatname_saveButton);

        buttonSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dialog_chatname_saveButton:
                        chatroomName = editTextChatName.getText().toString();
                        mOnNameReceivedListener.getName(chatroomId, chatroomName);
                        getDialog().dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnNameReceivedListener = (ChatroomNameDialog.OnNameReceivedListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }
}
