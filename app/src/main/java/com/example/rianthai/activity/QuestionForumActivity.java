package com.example.rianthai.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.adapter.QuestionAdapter;
import com.example.rianthai.databinding.ActivityQuestionForumBinding;
import com.example.rianthai.model.Question;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionForumActivity extends AppCompatActivity {

    private ActivityQuestionForumBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    String title;
    Question[] question,questionList;
    Intent intent;
    int count;
    private long mLastClickTime = 0;
    QuestionAdapter questionAdapter;
    BroadcastReceiver broadcastReceiver = null;
    int listType = 0;
    String[] questionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionForumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        title = intent.getStringExtra("title");

        broadcastReceiver = new InternetReceiver();
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseUtil.getFirestore();

        binding.tvTopActionBar.setText(title);
        binding.imgBtnBack.setOnClickListener(v->onBackPressed());
        binding.ivSearchIcon.setOnClickListener(v-> {
            if(!broadcastReceiver.toString().equals("disconnected"))
                searchQuestion(binding.etSearch.getText().toString());
            else
                InternetStatus();
        });

        binding.ivCreateQuestion.setOnClickListener(v->{
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                intent = new Intent(this, QuestionCreateActivity.class);
                intent.putExtra(Constants.TITLE, "Create Question");
                startActivity(intent);
            }
            else
                InternetStatus();
        });

        binding.gridForumQuestions.setOnItemClickListener((parent, view, position, id) -> {
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                intent = new Intent(QuestionForumActivity.this, QuestionViewActivity.class);
                intent.putExtra(Constants.QUESTION_ID, questionID[position]);
                if(listType == 1) {
                    intent.putExtra(Constants.QUESTION, (Serializable) questionList[position]);
                    listType = 0;
                    binding.etSearch.setText("");
                }
                else
                    intent.putExtra(Constants.QUESTION, (Serializable) question[position]);
                startActivity(intent);
            }
            else
                InternetStatus();
        });
        getQuestion();
    }

    public void searchQuestion(String word){
        word = word.trim().toLowerCase();
        if(question != null) {
            if (!word.equals("")) {
                ArrayList<Question> questionSearchList = new ArrayList<>();
                for (Question que : question) {
                    String title = que.getQuestionTitle().toLowerCase();
                    if (title.contains(word)) {
                        questionSearchList.add(que);
                    }

                }
                questionList = questionSearchList.toArray(new Question[0]);
                if(!questionSearchList.isEmpty()) {
                    listType = 1;
                    binding.gridForumQuestions.setAdapter(new QuestionAdapter(this, questionList));
                }
                Toast.makeText(this, "Results: " + questionList.length, Toast.LENGTH_SHORT).show();
            } else {
                getQuestion();
                listType = 0;
            }
        }
    }

    public void getQuestion(){
        if(title.equals("Question List")) {
            db.collection(Constants.FORUM)
                    .whereNotEqualTo(Constants.USERNAME, preferenceManager.getString(Constants.USERNAME))
                    .get()
                    .addOnCompleteListener(this::getDocument)
                    .addOnFailureListener(task -> Toast.makeText(QuestionForumActivity.this, "No Questions Found!", Toast.LENGTH_SHORT).show());
        }
        else{
            binding.ivCreateQuestion.setVisibility(View.VISIBLE);
            db.collection(Constants.FORUM)
                    .whereEqualTo(Constants.USERNAME, preferenceManager.getString(Constants.USERNAME))
                    .get()
                    .addOnCompleteListener(this::getDocument)
                    .addOnFailureListener(task -> Toast.makeText(QuestionForumActivity.this, "No Questions Found!", Toast.LENGTH_SHORT).show());
        }
    }

    public void getDocument(Task<QuerySnapshot> task) {
        count = 0;
        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
            List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
            question = new Question[documentSnapshot.size()];
            questionID = new String[documentSnapshot.size()];
            for (int i = 0; i < documentSnapshot.size(); i++) {
                question[i] = new Question(
                        (String) documentSnapshot.get(i).get(Constants.QUESTION_IMAGE),
                        (String) documentSnapshot.get(i).get(Constants.QUESTION_TITLE),
                        (String) documentSnapshot.get(i).get(Constants.QUESTION_DES),
                        (String) documentSnapshot.get(i).get(Constants.QUESTION_DATE),
                        (String) documentSnapshot.get(i).get(Constants.USERNAME),
                        (String) documentSnapshot.get(i).get(Constants.QUESTION_USER_PHOTO)
                );
                questionID[i] = documentSnapshot.get(i).getId();
            }
            if(title.equals("Question List")) {
                db.collection(Constants.USERS)
                        .get()
                        .addOnCompleteListener(task1 -> {
                            List<DocumentSnapshot> documents = task1.getResult().getDocuments();
                            for (int i = 0; i < documents.size(); i++) {
                                for (Question ques : question) {
                                    if (ques.getUsername().equals(documents.get(i).get(Constants.USERNAME))) {
                                        ques.setUserPhoto((String) documents.get(i).get(Constants.PHOTO));
                                    }
                                }
                            }
                            questionAdapter = new QuestionAdapter(this, question);
                            binding.gridForumQuestions.setAdapter(questionAdapter);
                            binding.gridForumQuestions.setVisibility(View.VISIBLE);
                            binding.clSearch.setVisibility(View.VISIBLE);
                            binding.tvNoQuestion.setVisibility(View.GONE);
                        });
            }
            else{
                for (Question ques : question) {
                    ques.setUserPhoto(preferenceManager.getString(Constants.PHOTO));
                }
                questionAdapter = new QuestionAdapter(this, question);
                binding.gridForumQuestions.setAdapter(questionAdapter);
                binding.gridForumQuestions.setVisibility(View.VISIBLE);
                binding.clSearch.setVisibility(View.VISIBLE);
                binding.tvNoQuestion.setVisibility(View.GONE);
            }
        }
        else{
            binding.gridForumQuestions.setVisibility(View.INVISIBLE);
            binding.tvNoQuestion.setVisibility(View.VISIBLE);
            binding.clSearch.setVisibility(View.VISIBLE);
            binding.clSearch.setClickable(false);
        }
    }
    public void InternetStatus(){
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetStatus();
        getQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}