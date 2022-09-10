package com.example.cardapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cardapp.R;
import com.example.cardapp.databinding.ActivityCreateCardBinding;

public class CreateCardActivity extends AppCompatActivity {

    ActivityCreateCardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}