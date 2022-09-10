package com.example.cardapp.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.cardapp.R;
import com.example.cardapp.constants.Constants;
import com.example.cardapp.databinding.ActivitySelectImageBinding;

import java.util.Arrays;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {

    private static final String TAG = "SelectImageActivity";
    private static final String KEY_PERMISSION_REQUEST_COUNT = "KEY_PERMISSION_REQUEST_COUNT";
    public static final int MAX_NUMBER_REQUEST_PERMISSION = 2;
    private static final int REQUEST_CODE_PERMISSIONS = 1010;
    private ActivitySelectImageBinding binding;
    private int mPermissionRequest = 0;
    private static final List<String> sPermissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState!=null){
            mPermissionRequest = savedInstanceState.getInt(KEY_PERMISSION_REQUEST_COUNT,0);
        }

        // request permissions
//        requestPermissionIfNecessary();

        binding.selectImageButton.setOnClickListener(view -> onSelectImage());
    }

    private void requestPermissionIfNecessary(){
        if(!checkCallingPermission()){
            if(mPermissionRequest < MAX_NUMBER_REQUEST_PERMISSION){
                mPermissionRequest+=1;
                ActivityCompat.requestPermissions(this,sPermissions.toArray(new String[0]),REQUEST_CODE_PERMISSIONS);
            }else{
                Toast.makeText(this, R.string.go_set_permissions, Toast.LENGTH_LONG).show();
                binding.selectImageButton.setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissionIfNecessary();
    }

    private boolean checkCallingPermission() {
        boolean hasPermissions = true;
        for(String permission : sPermissions){
            hasPermissions = hasPermissions && (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED);
        }

        return hasPermissions;
    }

    private void onSelectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        handleImageRequestResult(result.getData());
                    }
                }

            });

    private void handleImageRequestResult(Intent data) {
        Uri imageUri = null;
        if(data.getClipData()!=null){
            imageUri = data.getClipData().getItemAt(0).getUri();
        }else if(data.getData()!=null){
            imageUri = data.getData();
        }

        if(imageUri==null){
            Log.e(TAG, "handleImageRequestResult: Invalid Image Input");
            return;
        }

        Intent filterIntent = new Intent(this,CreateCardActivity.class);
        filterIntent.putExtra(Constants.KEY_IMAGE_URI,imageUri.toString());
        startActivity(filterIntent);
    }
}