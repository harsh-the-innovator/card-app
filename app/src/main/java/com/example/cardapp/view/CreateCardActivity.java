package com.example.cardapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.cardapp.constants.Constants;
import com.example.cardapp.databinding.ActivityCreateCardBinding;
import com.example.cardapp.viewmodel.CustomCardViewModel;

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
    }
}