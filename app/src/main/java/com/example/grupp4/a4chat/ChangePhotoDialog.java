package com.example.grupp4.a4chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.example.grupp4.a4chat.camera.Camera;

public class ChangePhotoDialog extends DialogFragment  {

    private static final String TAG = "ChangePhotoDialog";

    public static final int  CAMERA_REQUEST_CODE = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    //private static final Object ChangePhotoDialog;

    //For communicating with UserProfileFragment
    public interface OnPhotoReceivedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoReceivedListener mOnPhotoReceived;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changephoto, container, false);


        //Initialize the textview for choosing an image from memory

        TextView selectPhoto =  view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phones memory.");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //opens galleri intent
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
       TextView selectCamera = view.findViewById(R.id.dialogOpenCamera);
        selectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera.");
                ChangePhotoDialog changePhotoDialog = new ChangePhotoDialog.this.onClick();
                //Intent intent = new Intent(getContext(), ChangePhotoDialog.this);
               ////intent = new Intent(getContext())
                //        startActivityForResult(intent);
                //Context.getSystemService(CameraManager.class);
                //CameraManager cameraManager = new CameraManager;
                //MainActivity.CAMERA_POSITION_FRONT;

                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                //Intent intent = new Intent(Intent.ACTION_VIEW );
                //startActivityForResult(intent, 1234);
                //CameraManager manager = new CameraManager(Context.CAMERA_SERVICE);
                //String cameraId = manager.getCameraIdList()[0];
                //syncTask.THREAD_POOL_EXECUTOR.

            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

          //Results when selecting new image from phone memory
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);

            //send the fragment to the interface
            mOnPhotoReceived.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }

        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking a photo.");

           /*Bitmat bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            mOnPhotoReceived.getImageBitmap(bitmap);
            getDialog().dismiss();*/

        }
    }

    @Override
        public void onAttach(Context context) {
        try{
            mOnPhotoReceived = (OnPhotoReceivedListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException", e.getCause() );
        }
        super.onAttach(context);
    }

}