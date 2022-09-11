package com.example.cardapp.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cardapp.constants.Constants;
import com.example.cardapp.utils.CardWorkerUtils;

import java.util.Objects;

public class CardWorker extends Worker {
    private static final String TAG = "CardWorker";
    public CardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification("Writing quote onto image",applicationContext);

        CardWorkerUtils.sleep();

        String imageResourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
        String quote = getInputData().getString(Constants.CUSTOM_QUOTE);

        ContentResolver contentResolver = applicationContext.getContentResolver();

        //Create the bitmap
        try {
            Bitmap photo = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(imageResourceUri)));

            // Write text quote on image
            assert quote != null;
            Bitmap output = CardWorkerUtils.overlayTextOnBitmap(photo,applicationContext,quote);

            // write bitmap to temp file
            Uri outputUri = CardWorkerUtils.writeBitmapToFile(applicationContext,output);

            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, Objects.requireNonNull(outputUri).toString())
                    .build();


            return Result.success(outputData);
        } catch (Throwable e) {
            Log.i(TAG, "doWork: Error writing quote on image");
            return Result.failure();
        }

    }
}
