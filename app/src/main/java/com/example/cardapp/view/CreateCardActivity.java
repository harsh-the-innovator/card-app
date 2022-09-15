package com.example.cardapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.cardapp.constants.Constants;
import com.example.cardapp.databinding.ActivityCreateCardBinding;
import com.example.cardapp.viewmodel.CustomCardViewModel;

import java.util.List;

public class CreateCardActivity extends AppCompatActivity {

    private static final String TAG = "CreateCardActivity";
    private ActivityCreateCardBinding binding;
    private CustomCardViewModel customCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        customCardViewModel = new ViewModelProvider.AndroidViewModelFactory((Application) getApplicationContext()).create(CustomCardViewModel.class);

        Intent intent = getIntent();
        String imageUri = intent.getStringExtra(Constants.KEY_IMAGE_URI);

        customCardViewModel.setImageUri(imageUri);

        if(customCardViewModel.getImageUri()!=null){
            Log.i(TAG, "onCreate: IMAGE URI = " + customCardViewModel.getImageUri());
            Glide.with(this)
                    .load(customCardViewModel.getImageUri())
                    .into(binding.imageView);
        }

        customCardViewModel.getSavedWorkInfo().observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if(workInfos==null || workInfos.isEmpty()){
                    return;
                }

                WorkInfo workInfo = workInfos.get(0);
                boolean finished = workInfo.getState().isFinished();
                if(!finished){
                    showWorkInProgress();
                }else {
                    showWorkFinished();
                    Data outputData = workInfo.getOutputData();
                    String outputImageUri = outputData.getString(Constants.KEY_IMAGE_URI);

                    // If we have the output file, then show the "checkout image button"
                    if(!TextUtils.isEmpty(outputImageUri)){
                        customCardViewModel.setOutputUri(outputImageUri);
                        binding.seeCardButton.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        binding.processButton.setOnClickListener(view -> {
            String quote = binding.customQuoteEdtx.getText().toString().trim();
            if(!TextUtils.isEmpty(quote)){
                customCardViewModel.processImageToCard(quote);
            }
        });

        binding.seeCardButton.setOnClickListener(view -> {
            Log.i(TAG, "onCreate: SEE CARD");
            Uri currentUri = customCardViewModel.getOutputUri();
            if(currentUri!=null){
                Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                if(actionView.resolveActivity(getPackageManager())!=null){
                    startActivity(actionView);
                }
                binding.processButton.setVisibility(View.VISIBLE);
            }
        });

        binding.cancelButton.setOnClickListener(view -> customCardViewModel.cancelWork());
    }

    private void showWorkFinished() {
        binding.progressBar.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.seeCardButton.setVisibility(View.VISIBLE);
    }

    private void showWorkInProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.processButton.setVisibility(View.GONE);
        binding.seeCardButton.setVisibility(View.GONE);
    }
}