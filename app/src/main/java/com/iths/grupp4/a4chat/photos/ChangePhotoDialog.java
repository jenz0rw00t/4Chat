package com.iths.grupp4.a4chat.photos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.UserProfileFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangePhotoDialog extends DialogFragment {

    private static final String TAG = "ChangePhotoDialog";

    private static final int REQUEST_CODE_PREMISSION = 1234;
    public static final int  CAMERA_REQUEST_CODE = 2;
    private static final int RESULT_LOAD_IMAGE = 1;

    //For communicating with other Fragments
    public interface OnPhotoReceivedListener{
        void getImagePath(Uri imagePath);
    }

    OnPhotoReceivedListener mOnPhotoReceived;

    private String mCurrentPhotoPath;
    private boolean mStoragePermission;
    private boolean mCameraPermission;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changephoto, container, false);

        if (getArguments() != null) {
            bottomPosition();
        }


        TextView selectPhoto = (TextView) view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(v -> {

            String requiredPermission = "android.permission.READ_EXTERNAL_STORAGE";
            int checkVal = getContext().checkCallingOrSelfPermission(requiredPermission);
            if (checkVal==PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //opens camera intent
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            } else {
                verifyStoragePermission();
            }

            });


        TextView takePhoto = (TextView) view.findViewById(R.id.dialogOpenCamera);
        takePhoto.setOnClickListener(view1 -> {

            String externalStoragePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
            String cameraPermission = "android.permission.CAMERA";
            int permission1 = getContext().checkCallingOrSelfPermission(externalStoragePermission);
            int permission2 = getContext().checkCallingOrSelfPermission(cameraPermission);

            if (permission1==PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        //File Provider
                        //https://developer.android.com/reference/android/support/v4/content/FileProvider.html
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                    }
                }
            } else {
                verifyCameraPermission();
            }
        });
        return view;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
            Log.d(TAG, "onActivityResult: image uri: " + mCurrentPhotoPath);
            mOnPhotoReceived.getImagePath(Uri.fromFile(new File(mCurrentPhotoPath)));
            getDialog().dismiss();
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


    private void bottomPosition() {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = (Gravity.LEFT | Gravity.BOTTOM);
        wlp.y = 100; // The new position of the Y coordinates
        wlp.width = 300; // Width
        wlp.height = 300; // Height
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);
    }

    public void verifyStoragePermission(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissions,
                    REQUEST_CODE_PREMISSION
                    //Verkar som att man inte kommer åt OnRequestPermissionCode i DialogFragment. Därför tung kod för att direkt öppna
                    //telefonens minne när "Allow" väljs. Nu behöver man trycka allow och sen trycka en gång till... inte snyggt men men.
            );
        }

    public void verifyCameraPermission(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permission = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

            ActivityCompat.requestPermissions(
                    getActivity(),
                    permission,
                    REQUEST_CODE_PREMISSION
                    //Verkar som att man inte kommer åt OnRequestPermissionCode i DialogFragment. Därför tung kod för att direkt öppna
                    //telefonens minne när "Allow" väljs. Nu behöver man trycka allow och sen trycka en gång till... inte snyggt men men.
            );
        }
    }

