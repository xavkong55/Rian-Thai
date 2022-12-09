package com.example.rianthai.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.databinding.ActivityChapterContentBinding;
import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.model.ConsonantChapter;
import com.example.rianthai.model.Quiz;
import com.example.rianthai.model.VocabGrammarChapter;
import com.example.rianthai.model.VowelChapter;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ChapterContentActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    PreferenceManager preferenceManager;
    DocumentReference docRef;
    FirebaseFirestore db;
    Intent intent;
    private long mLastClickTime = 0;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.rianthai.databinding.ActivityChapterContentBinding binding = ActivityChapterContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        preferenceManager = new PreferenceManager(getApplicationContext());
        firebaseAuth = FirebaseUtil.getAuth();
        db = FirebaseUtil.getFirestore();
        docRef = db.collection(Constants.COURSE).document(preferenceManager.getString(Constants.COURSE_NAME));
        intent = getIntent();

        binding.tvTopActionBar.setText(intent.getStringExtra(Constants.COURSE_NAME));
        binding.tvChapterName.setText(preferenceManager.getString(Constants.CHAPTER_NAME));
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());

        binding.btnFlashCard.setOnClickListener(v -> {
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                getChapterContent();
            }
            else
                InternetStatus();
        });
        binding.btnQuiz.setOnClickListener(v -> {
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                getQuiz();
            }
            else
                InternetStatus();
        });

    }

    public void getChapterContent(){
        docRef.collection(Constants.CHAPTER)
                .whereEqualTo(Constants.CHAPTER_NAME,preferenceManager.getString(Constants.CHAPTER_NAME))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if (!documentSnapshot.getId().isEmpty()) {
                            docRef.collection(Constants.CHAPTER).document(documentSnapshot.getId()).collection(Constants.CONTENT).orderBy("contentPron", Query.Direction.ASCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult().size() > 0) {
                                            QueryDocumentSnapshot[] queryDocumentSnapshots = new QueryDocumentSnapshot[task1.getResult().size()];
                                            int count = 0;
                                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                                queryDocumentSnapshots[count] = document;
                                                count++;
                                            }
                                            contentChecker(queryDocumentSnapshots);
                                            preferenceManager.putString("chapterID",documentSnapshot.getId());
                                        }
                                        else{
                                            Toast.makeText(ChapterContentActivity.this, "No Content Found", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
        .addOnFailureListener(e -> Toast.makeText(ChapterContentActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show());
    }

    public void contentChecker(QueryDocumentSnapshot[] document){
        String chapterName = preferenceManager.getString(Constants.CHAPTER_NAME);
        String courseName = preferenceManager.getString(Constants.COURSE_NAME);
        intent = new Intent(ChapterContentActivity.this,FlashCardActivity.class);
        if ("basic".equals(courseName)) {
            if (chapterName.contains("Vowel")) {
                VowelChapter[] vowelChapter = new VowelChapter[document.length];
                for (int i = 0; i < vowelChapter.length; i++) {
                    vowelChapter[i] = new VowelChapter(
                            (String) document[i].get("contentName"),
                            (String) document[i].get("contentPron"),
                            (String) document[i].get("contentAudio")
                    );
                    vowelChapter[i].setDesNote((String)document[i].get("desNote"));
                }
                intent.putExtra("vowel", (Serializable) vowelChapter);
            } else {
                ConsonantChapter[] consonantChapters = new ConsonantChapter[document.length];
                for (int i = 0; i < consonantChapters.length; i++) {
                    consonantChapters[i] = new ConsonantChapter(
                            (String) document[i].get("contentName"),
                            (String) document[i].get("contentPron"),
                            (String) document[i].get("contentAudio")
                    );
                    consonantChapters[i].setDesImage((String)document[i].get("desImage"));
                    consonantChapters[i].setDesName((String)document[i].get("desName"));
                    consonantChapters[i].setDesPron((String)document[i].get("desPron"));
                    consonantChapters[i].setDesMeaning((String)document[i].get("desMeaning"));
                }
                intent.putExtra("consonant", (Serializable) consonantChapters);
            }
        } else {
            VocabGrammarChapter[] vocabGrammarChapter = new VocabGrammarChapter[document.length];
            for (int i = 0; i < vocabGrammarChapter.length; i++) {
                vocabGrammarChapter[i] = new VocabGrammarChapter(
                        (String)document[i].get("contentName"),
                        (String)document[i].get("contentPron"),
                        (String)document[i].get("contentAudio"),
                        (String)document[i].get("contentMeaning")
                );
                vocabGrammarChapter[i].setExampleAudio((ArrayList<String>)  document[i].get("exampleAudio"));
                vocabGrammarChapter[i].setExampleMeaning((ArrayList<String>)  document[i].get("exampleMeaning"));
                vocabGrammarChapter[i].setExampleName((ArrayList<String>)  document[i].get("exampleName"));
                vocabGrammarChapter[i].setExampleNote((ArrayList<String>)  document[i].get("exampleNote"));
                vocabGrammarChapter[i].setExamplePron((ArrayList<String>)  document[i].get("examplePron"));
            }
            intent.putExtra("vocab_grammar", (Serializable) vocabGrammarChapter);
        }
        if(!broadcastReceiver.toString().equals("disconnected"))
            startActivity(intent);
        else
            InternetStatus();
    }

    public void getQuiz(){
        docRef.collection(Constants.CHAPTER)
                .whereEqualTo(Constants.CHAPTER_NAME,preferenceManager.getString(Constants.CHAPTER_NAME))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if (!documentSnapshot.getId().isEmpty()) {
                            docRef.collection(Constants.CHAPTER).document(documentSnapshot.getId()).collection(Constants.QUIZ)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult().size() > 0) {
                                            QueryDocumentSnapshot[] queryDocumentSnapshots = new QueryDocumentSnapshot[task1.getResult().size()];
                                            int count = 0;
                                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                                queryDocumentSnapshots[count] = document;
                                                count++;
                                            }
                                            quizChecker(queryDocumentSnapshots);
                                            preferenceManager.putString("quizID",documentSnapshot.getId());
                                        }
                                        else{
                                            Toast.makeText(ChapterContentActivity.this, "No Quiz Found", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ChapterContentActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show());
    }
    public void quizChecker(QueryDocumentSnapshot[] document){
        intent = new Intent(ChapterContentActivity.this, LoadingScreenActivity.class);

        Integer[] arr = new Integer[document.length];
        for (int i = 0; i < document.length; i++) {arr[i] = i;}
        Collections.shuffle(Arrays.asList(arr));

        Integer[] arr2 = new Integer[]{1,2,3,4};

        Quiz[] quiz = new Quiz[10];
        for (int i = 0; i < 10; i++) {
             Collections.shuffle(Arrays.asList(arr2));
             String question = (String) document[arr[i]].get("question");
             String selection1 = (String) document[arr[i]].get("selection"+arr2[0]);
             String selection2 = (String) document[arr[i]].get("selection"+arr2[1]);
             String selection3 = (String) document[arr[i]].get("selection"+arr2[2]);
             String selection4 = (String) document[arr[i]].get("selection"+arr2[3]);
             String answer = (String) document[arr[i]].get("answer");
             quiz[i] = new Quiz(
                     question,
                     selection1,
                     selection2,
                     selection3,
                     selection4,
                     answer
             );
        }

        intent.putExtra("quiz", (Serializable) quiz);
        startActivity(intent);
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