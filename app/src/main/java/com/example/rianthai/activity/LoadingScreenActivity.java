package com.example.rianthai.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.animation.ProgressBarAnimation;
import com.example.rianthai.databinding.ActivityLoadingScreenBinding;
import com.example.rianthai.model.Quiz;

public class LoadingScreenActivity extends AppCompatActivity {

    private ActivityLoadingScreenBinding binding;
    Quiz[] quiz;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        broadcastReceiver = new InternetReceiver();
        Intent intent = getIntent();
        quiz = (Quiz[]) intent.getExtras().getSerializable("quiz");

        binding.progressBar.setMax(10);
        binding.progressBar.setScaleX(3f);

        progressAnimation();
    }
    public void progressAnimation(){
        ProgressBarAnimation animation = new ProgressBarAnimation(this,binding.progressBar,binding.loadingDetails,0,100, quiz);
        animation.setDuration(1000);
        binding.progressBar.setAnimation(animation);
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