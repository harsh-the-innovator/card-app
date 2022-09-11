package com.example.cardapp.workers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cardapp.constants.Constants;
import com.example.cardapp.utils.CardWorkerUtils;

import java.io.File;

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
        
        try {

            File outputDirectory = new File(applicationContext.getDataDir(), Constants.OUTPUT_PATH);
            if(outputDirectory.exists()){
                File[] entries = outputDirectory.listFiles();

                if(entries!=null && entries.length>0){
                    for(File entry :  entries){
                        String name = entry.getName();
                        if(!TextUtils.isEmpty(name) && name.endsWith(".png")){
                            boolean deleted = entry.delete();
                            Log.i(TAG, String.format("Deleted %s - %s",name ,deleted));
                        }
                    }
                }
            }

            return Result.success();
            
        }catch (Exception e){
            Log.i(TAG, "doWork: Error Cleaning Up" + e);
            return Result.failure();
        }
    }
}
