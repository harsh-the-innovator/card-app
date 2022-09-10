package com.example.cardapp.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.cardapp.constants.Constants;
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

    }
}
