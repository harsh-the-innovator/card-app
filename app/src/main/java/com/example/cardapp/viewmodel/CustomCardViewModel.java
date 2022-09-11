package com.example.cardapp.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.cardapp.constants.Constants;
import com.example.cardapp.workers.CardWorker;
import com.example.cardapp.workers.CleanupWorker;
import com.example.cardapp.workers.SaveCardToFileWorker;

import java.util.List;

public class CustomCardViewModel extends ViewModel {
    private Uri imageUri;
    private WorkManager workManager;
    private LiveData<List<WorkInfo>> savedWorkInfo;
    private Uri outputUri;

    public CustomCardViewModel(){

    }

    public CustomCardViewModel(@NonNull Application application) {
        workManager = WorkManager.getInstance(application);
        savedWorkInfo = workManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT);
    }

    public LiveData<List<WorkInfo>> getSavedWorkInfo() {
        if(savedWorkInfo==null){
            savedWorkInfo = workManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT);
        }
        return savedWorkInfo;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = uriOrNull(outputUri);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = uriOrNull(imageUri);
    }

    private Uri uriOrNull(String uriString) {
        if(!TextUtils.isEmpty(uriString)){
            return Uri.parse(uriString);
        }

        return null;
    }

    private void processImageToCard(String quote){
        // heavy works happen
        WorkContinuation continuation = workManager
                .beginUniqueWork(Constants.IMAGE_PROCESSING_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker.class)
                        );

        // building our card
        OneTimeWorkRequest.Builder cardBuilder = new OneTimeWorkRequest.Builder(CardWorker.class);
        cardBuilder.setInputData(createInputDataForUri(quote));

        continuation = continuation.then(cardBuilder.build());

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        // work request to save the image to file system
        OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(SaveCardToFileWorker.class)
                .setConstraints(constraints)
                .addTag(Constants.TAG_OUTPUT)
                .build();

        continuation = continuation.then(saveRequest);

        // start the work
        continuation.enqueue();
    }

    public void cancelWork(){
        workManager.cancelUniqueWork(Constants.IMAGE_PROCESSING_WORK_NAME);
    }

    private Data createInputDataForUri(String quote) {
        Data.Builder builder = new Data.Builder();
        if(imageUri!=null){
            builder.putString(Constants.KEY_IMAGE_URI,imageUri.toString());
            builder.putString(Constants.CUSTOM_QUOTE,quote);
        }

        return builder.build();
    }
}
