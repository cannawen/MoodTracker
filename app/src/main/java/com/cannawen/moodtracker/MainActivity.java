package com.cannawen.moodtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final int CODE_EXTERNAL_STORAGE_PERMISSION = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMoodRatingView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize();
        }
    }

    private void initialize() {
        if (!hasStoragePermission()) {
            requestStoragePermission();
        } else if (!hasDrawOverlayPermission()) {
            requestDrawOverlayPermission();
        } else {
            initializeView();
        }
    }

    private boolean hasStoragePermission() {
        return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_EXTERNAL_STORAGE_PERMISSION);
    }

    private boolean hasDrawOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    private void requestDrawOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
    }

    private void initializeView() {
        findViewById(R.id.rate_mood_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoodRatingView();
            }
        });
    }

    private void showMoodRatingView() {
        startService(new Intent(MainActivity.this, RatingViewService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION && resultCode == RESULT_OK) {
            initialize();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
