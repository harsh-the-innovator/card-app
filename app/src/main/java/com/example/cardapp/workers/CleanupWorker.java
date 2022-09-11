package com.example.cardapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cardapp.utils.CardWorkerUtils;

public class CleanupWorker extends Worker {
    public static final String TAG = CleanupWorker.class.getSimpleName();
    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification("Cleaning up old temporary files",applicationContext);
        CardWorkerUtils.sleep();

        return null;
    }
}
