package com.example.rianthai.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.R;
import com.example.rianthai.adapter.ConsonantAdapter;
import com.example.rianthai.adapter.VocabGrammarAdapter;
import com.example.rianthai.adapter.VowelAdapter;
import com.example.rianthai.databinding.ActivityFlashCardBinding;
import com.example.rianthai.model.ConsonantChapter;
import com.example.rianthai.model.VocabGrammarChapter;
import com.example.rianthai.model.VowelChapter;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlashCardActivity extends AppCompatActivity{

    ActivityFlashCardBinding binding;
    boolean flip_front = true;
    int num_content = 1;
    int total_content, noFragment, lastResult, currentResult;
    Dialog dialog;
    Intent intent;
    ConsonantChapter[] consonantChapters;
    VowelChapter[] vowelChapters;
    VocabGrammarChapter[] vocabGrammarChapters;
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    BroadcastReceiver broadcastReceiver = null;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();

        broadcastReceiver = new InternetReceiver();
        db = FirebaseUtil.getFirestore();
        dialog = new Dialog(this);
        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.tvTopActionBar.setText(preferenceManager.getString(Constants.CHAPTER_NAME));
        binding.imgBtnBack.setOnClickListener(v-> onBackPressed());

        binding.btnAudioMain.setOnClickListener(v -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try{
                if(noFragment == 1) {
                    mediaPlayer.setDataSource(consonantChapters[num_content - 1].getContentAudio());
                }
                else if (noFragment == 2){
                    mediaPlayer.setDataSource(vowelChapters[num_content - 1].getContentAudio());
                }
                else{
                    mediaPlayer.setDataSource(vocabGrammarChapters[num_content - 1].getContentAudio());
                }
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepare();
            }catch (IOException e){
                Toast.makeText(FlashCardActivity.this, "No Audio Found!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btmNavigationBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id){
                case R.id.nav_back:
                    setContentPosition(false);
                    backButtonVisibility();
                    setContent(num_content-1);
                    break;
                case R.id.nav_flip:
                    setFlip();
                    break;
                case R.id.nav_next:
                    if (num_content < total_content) {
                        setContentPosition(true);
                        backButtonVisibility();
                        setContent(num_content-1);
                    }
                    else {
                        endCourse("You're All Done!");
                    }
                    break;
            }
            return true;
        });

        getChapterContent();
    }

    @SuppressLint("SetTextI18n")
    public void getChapterContent(){
        String chapterName = preferenceManager.getString(Constants.CHAPTER_NAME);
        String courseName = preferenceManager.getString(Constants.COURSE_NAME);
        try{
            if (courseName.equals("basic")) {
                if (chapterName.contains("Vowel")) {
                    noFragment = 2;
                    vowelChapters = (VowelChapter[]) intent.getExtras().getSerializable("vowel");
                    total_content = vowelChapters.length;
                } else {
                    noFragment = 1;
                    consonantChapters = (ConsonantChapter[]) intent.getExtras().getSerializable("consonant");
                    total_content = consonantChapters.length;
                }
                binding.tvContentMeaningBack.setVisibility(View.GONE);
            } else {
                vocabGrammarChapters = (VocabGrammarChapter[]) intent.getExtras().getSerializable("vocab_grammar");
                total_content = vocabGrammarChapters.length;
                binding.tvContentMeaningFront.setVisibility(View.VISIBLE);
            }
            binding.btmNavigationBar.getMenu().findItem(R.id.nav_back).setVisible(false);
            binding.tvNumContent.setText(""+num_content);
            binding.tvTotalContent.setText(""+total_content);
            setContent(0);
        }
        catch (Exception e){
            Toast.makeText(this, "Sorry! There are some problems!", Toast.LENGTH_SHORT).show();
        }
    }

    public void backButtonVisibility(){
        binding.btmNavigationBar.getMenu().findItem(R.id.nav_back).setVisible(num_content != 1);
    }

    @SuppressLint("SetTextI18n")
    public void setContentPosition(boolean add) {
        if (add) {
            num_content++;
        } else
            num_content--;
        binding.tvNumContent.setText(""+num_content);
    }

    public void setContent(int numContent){
        if(noFragment == 1) {
            binding.tvContentNameFront.setText(consonantChapters[numContent].getContentName());
            binding.tvContentNameBack.setText(consonantChapters[numContent].getContentName());
            binding.tvContentPronFront.setText(consonantChapters[numContent].getContentPron());
            binding.tvContentPronBack.setText(consonantChapters[numContent].getContentPron());
            ConsonantAdapter consonantAdapter = new ConsonantAdapter(
                    FlashCardActivity.this,
                    consonantChapters[numContent].getDesImage(),
                    consonantChapters[numContent].getDesName(),
                    consonantChapters[numContent].getDesPron(),
                    consonantChapters[numContent].getDesMeaning()
            );
            binding.gridViewContent.setAdapter(consonantAdapter);
        }
        else if(noFragment == 2){
            binding.tvContentNameFront.setText(vowelChapters[numContent].getContentName());
            binding.tvContentNameBack.setText(vowelChapters[numContent].getContentName());
            binding.tvContentPronFront.setText(vowelChapters[numContent].getContentPron());
            binding.tvContentPronBack.setText(vowelChapters[numContent].getContentPron());
            VowelAdapter vowelAdapter = new VowelAdapter(
                    FlashCardActivity.this,
                    vowelChapters[numContent].getDesNote()
            );
            binding.gridViewContent.setAdapter(vowelAdapter);
        }
        else {
            binding.tvContentNameFront.setText(vocabGrammarChapters[numContent].getContentName());
            binding.tvContentNameBack.setText(vocabGrammarChapters[numContent].getContentName());
            binding.tvContentPronFront.setText(vocabGrammarChapters[numContent].getContentPron());
            binding.tvContentPronBack.setText(vocabGrammarChapters[numContent].getContentPron());
            binding.tvContentMeaningFront.setText(vocabGrammarChapters[numContent].getContentMeaning());
            binding.tvContentMeaningBack.setText(vocabGrammarChapters[numContent].getContentMeaning());
            VocabGrammarAdapter vocabGrammarAdapter = new VocabGrammarAdapter(
                    FlashCardActivity.this,
                    vocabGrammarChapters[numContent].getExampleName().toArray(new String[0]),
                    vocabGrammarChapters[numContent].getExamplePron().toArray(new String[0]),
                    vocabGrammarChapters[numContent].getExampleMeaning().toArray(new String[0]),
                    vocabGrammarChapters[numContent].getExampleAudio().toArray(new String[0]),
                    vocabGrammarChapters[numContent].getExampleNote().toArray(new String[0])
            );
            binding.gridViewContent.setAdapter(vocabGrammarAdapter);
        }
    }

    public void setFlip(){
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(binding.CLFlipCardFront, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(binding.CLFlipCardFront, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(flip_front){
                    binding.CLFlipCardFront.setVisibility(View.GONE);
                    binding.CLFlipCardBack.setVisibility(View.VISIBLE);
                    flip_front = false;
                }
                else {
                    binding.CLFlipCardFront.setVisibility(View.VISIBLE);
                    binding.CLFlipCardBack.setVisibility(View.GONE);
                    flip_front = true;
                }
                oa2.start();
            }
        });
        oa1.start();
    }

    public void endCourse(String title){
        if(!broadcastReceiver.toString().equals("disconnected")) {
            currentResult = (int) ((num_content / (float) total_content) * 100);
            new AlertDialog.Builder(FlashCardActivity.this)
                    .setIcon(R.drawable.ic_baseline_gpp_good_24)
                    .setTitle(title)
                    .setMessage("Are you sure you want to end this chapter?")
                    .setPositiveButton("Yes", (dialogInterface, i) ->
                            db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                                    .collection(Constants.HISTORY)
                                    .whereEqualTo(Constants.CHAPTER_NAME, preferenceManager.getString(Constants.CHAPTER_NAME))
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            if (!documentSnapshot.getId().isEmpty()) {
                                                db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                                                        .collection(Constants.HISTORY)
                                                        .document(documentSnapshot.getId())
                                                        .get()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                DocumentSnapshot documentSnapshots = task1.getResult();
                                                                lastResult = Integer.parseInt(Objects.requireNonNull(documentSnapshots.get(Constants.CHAPTER_PROGRESS)).toString());
                                                                if (currentResult > lastResult) {
                                                                    db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                                                                            .collection(Constants.HISTORY)
                                                                            .document(documentSnapshot.getId())
                                                                            .update(Constants.CHAPTER_PROGRESS, currentResult);
                                                                }
                                                                resultBox(currentResult, lastResult);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Map<String, Object> content = new HashMap<>();
                                            content.put(Constants.CHAPTER_NAME, preferenceManager.getString(Constants.CHAPTER_NAME));
                                            content.put(Constants.CHAPTER_PROGRESS, currentResult);
                                            content.put(Constants.QUIZ_RESULTS, 0);
                                            content.put(Constants.COURSE_NAME, preferenceManager.getString(Constants.COURSE_NAME));
                                            db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID)).collection(Constants.HISTORY)
                                                    .add(content);
                                            lastResult = 0;
                                            resultBox(currentResult, lastResult);
                                        }
                                    }))
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        }
        else{
            InternetStatus();
        }
    }

    @Override
    public void onBackPressed() {
            endCourse("You're almost there!");
    }

    @SuppressLint("SetTextI18n")
    public void resultBox(int currentResult, int lastResult){

        dialog.setContentView(R.layout.dialog_results);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_result_details = dialog.findViewById(R.id.result_details);
        TextView tv_result_score = dialog.findViewById(R.id.quiz_result_score);
        TextView tv_previous_result= dialog.findViewById(R.id.previous_result);
        TextView tv_current_result= dialog.findViewById(R.id.current_result);
        TextView tv_new_result= dialog.findViewById(R.id.new_result);
        Button btn_reattempt = dialog.findViewById(R.id.btn_reattempt);
        Button btn_done = dialog.findViewById(R.id.btn_done);

        String progress = "Chapter Progress";
        String score = num_content +"/"+total_content;
        String result_score = score +" ("+currentResult+"%)";
        String previous_result = lastResult+"%";
        String current_result = currentResult+"%";

        tv_result_details.setText(progress);

        tv_result_score.setText(result_score);
        if(num_content < (total_content/2)){
            tv_result_score.setTextColor(Color.RED);
        }
        else
            tv_result_score.setTextColor(Color.GREEN);
        tv_previous_result.setText(previous_result);
        tv_current_result.setText(current_result);

        if(currentResult > lastResult){
            tv_new_result.setText(current_result);
        }
        else{
            tv_new_result.setText(previous_result);
        }

        btn_reattempt.setOnClickListener(v -> {
            dialog.dismiss();
            num_content = 1;
            setContent(0);
            binding.tvNumContent.setText(""+num_content);
            binding.btmNavigationBar.getMenu().findItem(R.id.nav_back).setVisible(false);
        });
        btn_done.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        dialog.show();
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