package com.example.cardapp.workers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cardapp.R;
import com.example.cardapp.constants.Constants;
import com.example.cardapp.utils.CardWorkerUtils;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveCardToFileWorker extends Worker {
    private static final String TAG = "SaveCardToFileWorker";
    public static final String TITLE = "Card Image";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss z", Locale.ENGLISH);

    public SaveCardToFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification("Saving Image",applicationContext);
        CardWorkerUtils.sleep();

        ContentResolver contentResolver = applicationContext.getContentResolver();
        
        try {
            String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
            Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(resourceUri)));

            Uri outputUri = saveImage(bitmap,contentResolver,applicationContext);
            if(outputUri==null || TextUtils.isEmpty(outputUri.toString())){
                return Result.failure();
            }

            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI,outputUri.toString())
                    .build();


            return Result.success(outputData);
        }catch (Exception e) {
            Log.i(TAG, "doWork: Unable to save image to Gallery", e);
            return Result.failure();
        }
    }

    private Uri saveImage(Bitmap bitmap,ContentResolver contentResolver,Context context) {
        ContentValues values = getContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + context.getString(R.string.app_name));
        values.put(MediaStore.Images.Media.IS_PENDING, true);

        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try {
                saveImageToStream(bitmap, contentResolver.openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                contentResolver.update(uri, values, null, null);
                return uri;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED,DATE_FORMATTER.format(new Date()));
        values.put(MediaStore.Images.Media.DATE_TAKEN,DATE_FORMATTER.format(new Date()));
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
