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

public class ChatroomDialogNewChatroom extends DialogFragment {

    EditText editTextEditName;
    Button buttonOk;
    Button buttonCancel;

    private static final String TAG = "ChatNameDialog";
    String chatroomName;
    String chatroomId;

    public interface OnEditNameListener {
        void editName(String chatroomId, String chatroomName);
    }

    OnEditNameListener onEditNameListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chatroom_editname, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            chatroomId = bundle.getString("ChatroomId", null);
        }

        editTextEditName = (EditText) view.findViewById(R.id.dialog_chatroom_editname_edittext);
        buttonOk = (Button) view.findViewById(R.id.dialog_chatroom_editname_okButton);
        buttonCancel = (Button) view.findViewById(R.id.dialog_chatroom_editname_cancelButton);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatroomName = editTextEditName.getText().toString();
                onEditNameListener.editName(chatroomId, chatroomName);

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
            onEditNameListener = (ChatroomDialogNewChatroom.OnEditNameListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }
}
