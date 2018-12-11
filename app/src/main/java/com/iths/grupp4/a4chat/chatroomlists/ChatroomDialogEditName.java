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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.iths.grupp4.a4chat.R;

public class ChatroomDialogEditName extends DialogFragment {

    EditText editTextEditName;
    Button buttonOk;
    Button buttonCancel;
    View view;

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
        view = inflater.inflate(R.layout.dialog_chatroom_editname, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            chatroomId = bundle.getString("ChatroomId", null);
        }

        editTextEditName = (EditText) view.findViewById(R.id.dialog_chatroom_editname_edittext);
        buttonOk = (Button) view.findViewById(R.id.dialog_chatroom_editname_okButton);
        buttonCancel = (Button) view.findViewById(R.id.dialog_chatroom_editname_cancelButton);
        showKeyboard();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatroomName = editTextEditName.getText().toString();
                onEditNameListener.editName(chatroomId, chatroomName);

                closeKeyboard();
                getDialog().dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();
                getDialog().dismiss();
            }
        });

        return view;
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onAttach(Context context) {
        try {
            onEditNameListener = (ChatroomDialogEditName.OnEditNameListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }
}
