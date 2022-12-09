package com.example.rianthai.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.databinding.ActivityQuestionCreateBinding;
import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.model.Question;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class QuestionCreateActivity extends AppCompatActivity {

    private ActivityQuestionCreateBinding binding;
    PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    Uri imageUri;
    String title;
    Question question;
    BroadcastReceiver broadcastReceiver = null;
    String questionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        Intent intent = getIntent();
        db = FirebaseUtil.getFirestore();
        FirebaseStorage storage = FirebaseUtil.getStorage();
        storageReference = storage.getReference();
        preferenceManager = new PreferenceManager(getApplicationContext());
        title = intent.getStringExtra(Constants.TITLE);

        if (title.equals("Edit Question")) {
            question = (Question) intent.getExtras().getSerializable("question");
            binding.etQuestionTitle.setText(question.getQuestionTitle());
            binding.etQuestionDescription.setText(question.getQuestionDescription());
            questionID = intent.getStringExtra(Constants.QUESTION_ID);
        }

        binding.tvTopActionBar.setText(title);
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());
        binding.btnInsertPhoto.setOnClickListener(v -> selectImage());

        binding.btnQuestionConfirm.setOnClickListener(v -> {
            if (binding.etQuestionTitle.getText().length() > 255 || binding.etQuestionTitle.getText().toString().equals("")) {
                binding.etQuestionTitle.setError("Please fill in and not more than 255 characters");
                return;
            }
            if (binding.etQuestionDescription.getText().toString().equals("")) {
                binding.etQuestionDescription.setError("Please fill in");
                return;
            }
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (title.equals("Create Question")) {
                    createQuestion();
                } else {
                    updateQuestion();
                }
            }
            else InternetStatus();
        });
    }
    public void createQuestion(){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            long timeInMillis = System.currentTimeMillis();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timeInMillis);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            String questionDate = dateFormat.format(cal1.getTime());
            Question question = new Question(
                    "",
                    binding.etQuestionTitle.getText().toString(),
                    binding.etQuestionDescription.getText().toString(),
                    questionDate,
                    preferenceManager.getString(Constants.USERNAME),
                    preferenceManager.getString(Constants.PHOTO)
            );
            db.collection(Constants.FORUM)
                    .add(question)
                    .addOnCompleteListener(task -> {
                        binding.etQuestionTitle.setText("");
                        binding.etQuestionDescription.setText("");
                        if(imageUri != null ) {
                            StorageReference newQuestionPic =  storageReference.child("Forum/"+task.getResult().getId());
                            newQuestionPic.putFile(imageUri)
                                    .addOnCompleteListener(task1 -> newQuestionPic.getDownloadUrl().addOnSuccessListener(uri ->
                                            db.collection(Constants.FORUM).document(task.getResult().getId())
                                            .update("questionImage",uri.toString())
                                            .addOnCompleteListener(task2 ->{
                                                progressDialog.dismiss();
                                                binding.ivPhotoName.setText("");
                                                Snackbar.make(findViewById(android.R.id.content), "Question Created.", Snackbar.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                                            })))
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnProgressListener(snapshot -> {
                                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                        progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
                                    });
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Question Created.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                    });
    }

    public void updateQuestion(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();

        StorageReference newQuestionPic =  storageReference.child("Forum/"+questionID);
        Map<String, Object> updates = new HashMap<>();
        updates.put("questionTitle",binding.etQuestionTitle.getText().toString());
        updates.put("questionDescription",binding.etQuestionDescription.getText().toString());
        db.collection(Constants.FORUM).document(questionID)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if(imageUri != null) {
                        newQuestionPic.putFile(imageUri)
                                .addOnCompleteListener(task1 -> newQuestionPic.getDownloadUrl().addOnSuccessListener(uri -> db.collection(Constants.FORUM).document(questionID)
                                        .update("questionImage",uri.toString())
                                        .addOnCompleteListener(task2 ->{
                                            question.setQuestionImage(uri.toString());
                                            progressDialog.dismiss();
                                            Snackbar.make(findViewById(android.R.id.content), "Question Updated.", Snackbar.LENGTH_LONG).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_LONG).show();
                                        })))
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_LONG).show();
                                })
                                .addOnProgressListener(snapshot -> {
                                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
                                });
                    }else {
                        question.setQuestionTitle(binding.etQuestionTitle.getText().toString());
                        question.setQuestionDescription(binding.etQuestionDescription.getText().toString());
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Question Updated.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_LONG).show();
                });
    }

    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.ivPhotoName.setText(imageUri.getLastPathSegment());
        }
    }

    @Override
    public void onBackPressed() {
        if(title.equals("Edit Question")){
            Intent intent = new Intent(this,QuestionViewActivity.class);
            intent.putExtra("question",(Serializable) question);
            intent.putExtra(Constants.QUESTION_ID, questionID);
            startActivity(intent);
            finish();
        }
        else{
            super.onBackPressed();
        }
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