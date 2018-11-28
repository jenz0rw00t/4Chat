package com.iths.grupp4.a4chat.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class PhotoUploader {

    public PhotoUploader(String userId, Context context, int imageWidth, int imageHeight) {
        mUserid = userId;
        mContext = context;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
    }

    private static String TAG = "PhotoUploader";
    private static double MB_THRESHHOLD = 5.0;
    private static double MB = 1000000.0;

    private Context mContext;
    private String mUserid;
    private byte[] mFullSizeBytes;
    private byte[] mSmallSizeBytes;
    private BackgroundConversion mConvert;
    private double progress;
    private int mImageWidth;
    private int mImageHeight;


    public void uploadNewPhoto(Uri imageUri) {
        Log.d(TAG, "uploadNewPhoto: uploading new image" + imageUri);
        BackgroundImageResize uploadResizedImage = new BackgroundImageResize(null);


        uploadResizedImage.execute(imageUri);
        uploadFullSizeNewPhoto(imageUri);
    }

    public void uploadFullSizeNewPhoto(Uri imageUri) {
        Log.d(TAG, "uploadFullSizeNewPhoto: uploading new image" + imageUri);

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded, then interrupt
        BackgroundConversion uploadFullsizeImage = new BackgroundConversion(null);
       // if (uploadFullsizeImage != null) {
       //     uploadFullsizeImage.cancel(true);
      //  }
        uploadFullsizeImage.execute(imageUri);
    }






    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(mContext, "compressing image", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPreExecute: Compress image");
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "BackgroundImageResize -> doInBackground: started.");

            if (mBitmap == null) {
                try {
                    Log.d(TAG, "BackgroundImageResize -> doInBackground: bitmap is null");
                    mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), params[0]);

                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            Bitmap mBitmapResized = getResizedBitmap(mBitmap, mImageWidth, mImageWidth);
            byte[] bytes = null;
            bytes = getBytesFromBitmap(mBitmapResized, 100);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mSmallSizeBytes = bytes;
            //hideProgressBar();
            //execute the upload task
            executeUploadTask();
        }
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }



    private void executeUploadTask() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/users" + "/" + mUserid + "/profile_image");

        if (mSmallSizeBytes.length / MB < MB_THRESHHOLD) {
            Log.d(TAG, "BackgroundImageResize -> executeUploadTask: ");

            UploadTask uploadTask;
            uploadTask = storageReference.putBytes(mSmallSizeBytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Now insert the download url into the firebase database
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri firebaseURL = urlTask.getResult();


                    Log.d(TAG, "uploadNewPhoto: uploading new image " + firebaseURL.toString());

                    updateSmallProfilePicture(firebaseURL.toString());
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "BackgroundImageResize -> onFailure: TASK FAILED " + exception);
                }
            });
            }

        }

    //Updates the new URL that points to Firebase Storage to database FireStore Cloud
    private void updateSmallProfilePicture(String downloadUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("avatar", downloadUrl);
        updates.put("fullSizeAvatar", downloadUrl); // will get overridden by the real fullsize once its uploaded

        db.collection("users")
                .document(mUserid)
                .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: avatar is updated");
                } else {
                    Log.d(TAG, "onComplete: avatar update failed");
                }
            }
        });
    }








    public class BackgroundConversion extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;

        public BackgroundConversion(Bitmap bitmap) {
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        //this class is for compressing the image in iterations
        //If the compressed image is not under 5 MB, its compresses and reduces the quality of the
        //image by 10% for each iteration, until the picture is less than 5 MB.
        @Override
        protected byte[] doInBackground(Uri... params) {

            if (mBitmap == null) {

                try {
                    Log.d(TAG, "BackgroundFullSizeImage -> doInBackground: bitmap is null");
                    mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), params[0]);
                    Log.d(TAG, "doInBackground: bitmap size: megabytes: " + mBitmap.getByteCount()/MB + "MB");
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            for (int i = 1; i < 11; i++) {
                if (i == 10) {
                  Toast.makeText(mContext, "Image is too Large", Toast.LENGTH_SHORT).show();
                  break;
                  }
                bytes = getBytesFromBitmap(mBitmap, 100 / i);
                Log.d(TAG, "doInBackground: megabytes(" + (11 - i) + "0%) " + bytes.length / MB + "MB");
                if (bytes.length / MB < MB_THRESHHOLD) {
                    return bytes;
                }
            }
            return bytes;
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

           mFullSizeBytes = bytes;
            Log.d(TAG, "onPostExecute: mFullbyteSize is " + mFullSizeBytes);

            //execute the upload
            executeUploadTask();
        }

        private void executeUploadTask() {
            Toast.makeText(mContext, "Uploading image...", Toast.LENGTH_SHORT).show();
            String uploadPath = "";


            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("images/users" + "/" + mUserid + "/profile_image_fullsize");


           // Log.d(TAG, "BackgroundFullSizeImage -> executeUploadTask: " + mFullSizeBytes.length);
            //if the image size is valid then we can submit to database
            if (mFullSizeBytes.length / MB < MB_THRESHHOLD) {

                UploadTask uploadTask;
                uploadTask = storageReference.putBytes(mFullSizeBytes);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Now insert the download url into the firebase database

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri firebaseURL = urlTask.getResult();

                        Toast.makeText(mContext, "Upload Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "uploadBIGSIZEPhoto: uploading new image " + firebaseURL.toString());

                        updateFullProfilePicture(firebaseURL.toString());
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                      //  Toast.makeText(mContext, "could not upload photo", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        if (currentProgress > (progress + 15)) {
                            progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                            Toast.makeText(mContext, progress + "%", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                ;
            } else {
                Toast.makeText(mContext, "Image is too Large", Toast.LENGTH_SHORT).show();
            }
        }


        private void updateFullProfilePicture(String downloadUrl) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("fullSizeAvatar", downloadUrl);

            db.collection("users")
                    .document(mUserid)
                    .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: fullsizeavatar is updated");

                    } else {
                        Log.d(TAG, "onComplete: fullsizeavatar update failed");
                    }
                }
            });
        }
    }
}


