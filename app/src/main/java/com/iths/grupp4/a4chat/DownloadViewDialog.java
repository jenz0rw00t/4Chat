package com.iths.grupp4.a4chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DownloadViewDialog extends DialogFragment {

    private static final String TAG = "ChooseDownloadViewD";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_download_view, container, false);

        TextView downloadImage = (TextView) view.findViewById(R.id.dialogDownloadImage);
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");

            }
        });

        TextView viewImage = (TextView) view.findViewById(R.id.dialogViewFullscreen);
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");

            }
        });


        return view;
    }
}
