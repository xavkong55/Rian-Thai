package com.example.rianthai.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivityQuizBinding;
import com.example.rianthai.model.Quiz;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityQuizBinding binding;
    private ArrayList<Quiz> quizModalArrayList;
    int currentScore =0, questionAttempted = 1, currentPos = 0, currentResult,lastScore;
    String result;
    Dialog dialog;
    PreferenceManager preferenceManager;
    Quiz[] quiz;
    FirebaseFirestore db;
    BroadcastReceiver broadcastReceiver = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        Intent intent = getIntent();
        quiz = (Quiz[]) intent.getExtras().getSerializable("quiz");
        preferenceManager = new PreferenceManager(getApplicationContext());
        quizModalArrayList = new ArrayList<>();
        db = FirebaseUtil.getFirestore();

        quizModalArrayList.addAll(Arrays.asList(quiz));

        binding.tvTopActionBar.setText(preferenceManager.getString(Constants.CHAPTER_NAME));
        dialog = new Dialog(this);

        setDataToViews();
        binding.btnSelection1.setOnClickListener(this);
        binding.btnSelection2.setOnClickListener(this);
        binding.btnSelection3.setOnClickListener(this);
        binding.btnSelection4.setOnClickListener(this);
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());
    }
    @SuppressLint("SetTextI18n")
    public void setDataToViews(){
        result = "Wrong";
        binding.quizQuestionNum.setText(Integer.toString(currentPos+1));
        binding.quizQuestionTitle.setText(quizModalArrayList.get(currentPos).getQuestion());
        binding.btnSelection1.setText(quizModalArrayList.get(currentPos).getSelection1());
        binding.btnSelection2.setText(quizModalArrayList.get(currentPos).getSelection2());
        binding.btnSelection3.setText(quizModalArrayList.get(currentPos).getSelection3());
        binding.btnSelection4.setText(quizModalArrayList.get(currentPos).getSelection4());
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        if(currentPos <= quizModalArrayList.size()-1) {
            switch (v.getId()) {
                case R.id.btn_selection_1:
                    if (quizModalArrayList.get(currentPos).getAnswer().trim().equalsIgnoreCase(binding.btnSelection1.getText().toString())) {
                        currentScore++;
                        result = "Correct";
                    }
                    break;
                case R.id.btn_selection_2:
                    if (quizModalArrayList.get(currentPos).getAnswer().trim().equalsIgnoreCase(binding.btnSelection2.getText().toString())) {
                        currentScore++;
                        result = "Correct";
                    }
                    break;
                case R.id.btn_selection_3:
                    if (quizModalArrayList.get(currentPos).getAnswer().trim().equalsIgnoreCase(binding.btnSelection3.getText().toString())) {
                        currentScore++;
                        result = "Correct";
                    }
                    break;
                case R.id.btn_selection_4:
                    if (quizModalArrayList.get(currentPos).getAnswer().trim().equalsIgnoreCase(binding.btnSelection4.getText().toString())) {
                        currentScore++;
                        result = "Correct";
                    }
                    break;
            }
            dialog.setContentView(R.layout.dialog_quiz_score);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView answer_details = dialog.findViewById(R.id.answer_details);
            TextView answer_num= dialog.findViewById(R.id.answer_num);
            Button btn_next_question = dialog.findViewById(R.id.btn_next_question);
            answer_details.setText(result+" Answer!");
            answer_num.setText(currentScore +"/"+ quizModalArrayList.size());
            if(result.equals("Correct"))
                answer_num.setTextColor(Color.GREEN);
            else
                answer_num.setTextColor(Color.RED);
            btn_next_question.setOnClickListener(v1 -> {
               if(currentPos != quizModalArrayList.size()-1) {
                   questionAttempted++;
                   currentPos++;
                   setDataToViews();
                   dialog.dismiss();
               }
               else{
                   dialog.dismiss();
                   endQuiz();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }
    @SuppressLint("SetTextI18n")
    public void endQuiz(){
        currentResult = (int) ((currentScore/(float)quizModalArrayList.size())*100);
        db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                .collection(Constants.HISTORY)
                .whereEqualTo(Constants.CHAPTER_NAME,preferenceManager.getString(Constants.CHAPTER_NAME))
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
                                            lastScore = Integer.parseInt (Objects.requireNonNull(documentSnapshots.get(Constants.QUIZ_RESULTS)).toString());
                                            if(currentResult > lastScore) {
                                                db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                                                        .collection(Constants.HISTORY)
                                                        .document(documentSnapshot.getId())
                                                        .update(Constants.QUIZ_RESULTS, currentResult);
                                            }
                                            resultBox(currentResult,lastScore);
                                        }
                                    });
                        }
                    }
                    else{
                        Map<String,Object> content = new HashMap<>();
                        content.put(Constants.CHAPTER_NAME,preferenceManager.getString(Constants.CHAPTER_NAME));
                        content.put(Constants.QUIZ_RESULTS, currentResult);
                        content.put(Constants.CHAPTER_PROGRESS, 0);
                        content.put(Constants.COURSE_NAME,preferenceManager.getString(Constants.COURSE_NAME));
                        db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID)).collection("history")
                                .add(content);
                        lastScore = 0;
                        resultBox(currentResult,lastScore);
                    }
                });
    }

    public void resultBox(int currentResult, int lastScore){
        if(!broadcastReceiver.toString().equals("disconnected")) {
            dialog.setContentView(R.layout.dialog_results);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView result_details = dialog.findViewById(R.id.result_details);
            TextView quiz_result_score = dialog.findViewById(R.id.quiz_result_score);
            TextView quiz_previous_result = dialog.findViewById(R.id.previous_result);
            TextView quiz_current_result = dialog.findViewById(R.id.current_result);
            TextView quiz_new_result = dialog.findViewById(R.id.new_result);
            Button btn_reattempt = dialog.findViewById(R.id.btn_reattempt);
            Button btn_done = dialog.findViewById(R.id.btn_done);

            String result_title = "Quiz Result";
            String score = currentScore + "/" + quizModalArrayList.size();
            String result_score = score + " (" + currentResult + "%)";
            String previous_result = lastScore + "%";
            String current_result = currentResult + "%";

            result_details.setText(result_title);

            quiz_result_score.setText(result_score);
            if (currentScore < (quizModalArrayList.size() / 2)) {
                quiz_result_score.setTextColor(Color.RED);
            } else
                quiz_result_score.setTextColor(Color.GREEN);
            quiz_previous_result.setText(previous_result);
            quiz_current_result.setText(current_result);

            if (lastScore < currentResult)
                quiz_new_result.setText(current_result);
            else
                quiz_new_result.setText(previous_result);

            btn_reattempt.setOnClickListener(v -> {
                currentScore = 0;
                questionAttempted = 1;
                currentPos = 0;
                setDataToViews();
                dialog.dismiss();
            });
            btn_done.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        else InternetStatus();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(QuizActivity.this)
                .setIcon(R.drawable.ic_baseline_remove_circle_24)
                .setTitle("Quiz isn't Finished Yet")
                .setMessage("Are you sure you want to end this quiz? (Result will not be Saved!)")
                .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
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