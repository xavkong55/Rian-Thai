package com.example.rianthai.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.adapter.ChapterAdapter;
import com.example.rianthai.databinding.ActivityChapterBinding;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.PreferenceManager;

public class ChapterActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;
    String[] chapterList, chapterImages;
    String courseName;
    Intent intent;
    private long mLastClickTime = 0;
    Dialog dialog;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.rianthai.databinding.ActivityChapterBinding binding = ActivityChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        dialog = new Dialog(this);
        preferenceManager = new PreferenceManager(getApplicationContext());
        intent = getIntent();
        chapterList = intent.getStringArrayExtra(Constants.CHAPTER_LIST);
        chapterImages = intent.getStringArrayExtra(Constants.CHAPTER_IMAGE);
        courseName = intent.getStringExtra(Constants.COURSE_NAME);

        binding.tvTopActionBar.setText(courseName);
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());

        ChapterAdapter chapterAdapter = new ChapterAdapter(ChapterActivity.this, chapterList,chapterImages);
        binding.gridView.setAdapter(chapterAdapter);

        binding.gridView.setOnItemClickListener((parent, view, position, id) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            preferenceManager.putString(Constants.CHAPTER_NAME,chapterList[position]);
            intent = new Intent(ChapterActivity.this, ChapterContentActivity.class);
            intent.putExtra(Constants.COURSE_NAME,courseName);
            startActivity(intent);
        });
    }

    public void InternetStatus(){
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}