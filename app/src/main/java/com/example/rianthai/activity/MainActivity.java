package com.example.rianthai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivityMainBinding;
import com.example.rianthai.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth firebaseAuth = FirebaseUtil.getAuth();

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {
                    if(firebaseAuth.getCurrentUser() != null){
                        startActivity(new Intent(this, HomepageActivity.class));
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    finish();
        }, SPLASH_TIME_OUT);

        //Animation
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        binding.imageProfile.setAnimation(topAnim);
        binding.progressBar.setAnimation(bottomAnim);

    }
}