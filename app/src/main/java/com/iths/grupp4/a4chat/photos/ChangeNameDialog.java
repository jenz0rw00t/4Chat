package com.iths.grupp4.a4chat.photos;

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

public class ChangeNameDialog extends DialogFragment {

    private static final String TAG = "ChangeNameDialog";
    String name;
    Button buttonOk;
    Button buttonCancel;

    public interface OnNameReceivedListener {
        void getName(String name);
    }

    OnNameReceivedListener mOnNameReceivedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changename, container, false);

        EditText nameInput = view.findViewById(R.id.dialog_profile_editname_edittext);
        buttonOk = view.findViewById(R.id.dialog_profile_editname_okButton);
        buttonCancel = view.findViewById(R.id.dialog_profile_editname_cancelButton);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dialog_profile_editname_okButton:
                        name = nameInput.getText().toString();
                        mOnNameReceivedListener.getName(name);

                        getDialog().dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dialog_profile_editname_cancelButton:

                        getDialog().dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnNameReceivedListener = (OnNameReceivedListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }

}


