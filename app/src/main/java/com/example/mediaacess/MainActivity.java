package com.example.mediaacess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView uri_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uri_tv = findViewById(R.id.uri_tv);

    }

    public void getPermissionAndFiles(View view) {
        if (isPermissionGranted()){
            pickAllKindOfFilesFromStorage();

        }else {
            getPermissionsForAndroid11AboveAndBelowVersions();
        }
    }
    private boolean isPermissionGranted(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();

        }else{
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
    }
    private void getPermissionsForAndroid11AboveAndBelowVersions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.Default");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent,100);

            }catch (Exception exception){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,100);

            }

        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == 100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()){
                        pickAllKindOfFilesFromStorage();

                    }else{
                        getPermissionsForAndroid11AboveAndBelowVersions();
                    }
                }
            }else if(requestCode ==102){
                Uri uri = data.getData();
                if(uri != null){
                    uri_tv.setText(uri.getPath());
                }else {
                    return;
                }
            }
        }
    }

    private void pickAllKindOfFilesFromStorage() {
        if (Build.VERSION.SDK_INT < 19){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/* video/* audio/*");
            startActivityForResult(intent,102);
        }else{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/*","video/*","audio/*"});
            startActivityForResult(intent,102);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length > 0){
                boolean readExternalStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (readExternalStoragePermission){
                    pickAllKindOfFilesFromStorage();
                }else {
                    getPermissionsForAndroid11AboveAndBelowVersions();
                }
            }
        }
    }
}