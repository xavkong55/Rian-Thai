package com.example.rianthai.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.R;
import com.example.rianthai.adapter.CommentAdapter;
import com.example.rianthai.databinding.ActivityQuestionViewBinding;
import com.example.rianthai.model.Comment;
import com.example.rianthai.model.CommentReport;
import com.example.rianthai.model.Question;
import com.example.rianthai.model.Report;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuestionViewActivity extends AppCompatActivity {

    private ActivityQuestionViewBinding binding;
    FirebaseFirestore db;
    PreferenceManager preferenceManager;
    Question question;
    Comment[] comments;
    Dialog dialog;
    StorageReference storageReference;
    int position;
    BroadcastReceiver broadcastReceiver = null;
    private long mLastClickTime = 0;
    String questionID;
    String[] commentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        Intent intent = getIntent();
        question = (Question)intent.getExtras().getSerializable("question");
        questionID = intent.getStringExtra(Constants.QUESTION_ID);

        db = FirebaseUtil.getFirestore();
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseStorage storage = FirebaseUtil.getStorage();
        storageReference = storage.getReference();

        if(!question.getUsername().equals(preferenceManager.getString(Constants.USERNAME))){
            binding.ivEditQuestion.setVisibility(View.INVISIBLE);
            binding.ivDeleteReportQuestion.setImageResource(R.drawable.ic_baseline_gpp_bad_24);
        }

        binding.ivQuestionImage.setOnClickListener(v->{
            dialog.setContentView(R.layout.dialog_picture);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView image_question = dialog.findViewById(R.id.iv_image_zoom);
            Glide
                    .with(getApplicationContext())
                    .load(question.getQuestionImage())
                    .placeholder(R.color.white)
                    .into(image_question);
            dialog.setOnCancelListener(DialogInterface::dismiss);
            dialog.show();
        });

        binding.ivDeleteReportQuestion.setOnClickListener(v -> {
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if(question.getUsername().equals(preferenceManager.getString(Constants.USERNAME))){
                    deleteQuestion();
                }
                else{
                    reportAction("Question");
                }
            }
            else InternetStatus();

        });
        binding.ivEditQuestion.setOnClickListener(v->{
            if(!broadcastReceiver.toString().equals("disconnected")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent1 = new Intent(this, QuestionCreateActivity.class);
                intent1.putExtra(Constants.TITLE, "Edit Question");
                intent1.putExtra(Constants.QUESTION, (Serializable) question);
                intent1.putExtra(Constants.QUESTION_ID, questionID);
                startActivity(intent1);
                finish();
            }
            else InternetStatus();
        });

        binding.gridListComments.setOnItemLongClickListener((parent, view, pos, id) -> {
            if(!broadcastReceiver.toString().equals("disconnected")) {
                position = pos;
                if (comments[position].getUsername().equals(preferenceManager.getString(Constants.USERNAME)))
                    deleteComment();
                else {
                    reportAction("Comment");
                }
            }
            else
                InternetStatus();
            return false;
        });

        binding.imgBtnBack.setOnClickListener(v->onBackPressed());
        binding.ivAddComment.setOnClickListener(v-> addComment());
        getQuestion();
        getComments();
        dialog = new Dialog(this);
    }

    public void deleteComment(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_remove_circle_24)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes",(dialogInterface, i) -> db.collection(Constants.FORUM).document(questionID)
                        .collection(Constants.COMMENT).document(commentID[position])
                        .delete()
                        .addOnCompleteListener(task -> {
                            Toast.makeText(getApplicationContext(), "Comment Deleted Successfully", Toast.LENGTH_SHORT).show();
                            getComments();
                        })
                        .addOnFailureListener(task -> Toast.makeText(getApplicationContext(), "Failed to Delete", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }

    public void getQuestion(){
        if(!question.getUserPhoto().equals("")) {
            Glide
                    .with(getApplicationContext())
                    .load(question.getUserPhoto())
                    .placeholder(R.color.white)
                    .into(binding.ivQuestionUserImage);
        }
        else{
            binding.ivQuestionUserImage.setImageResource(R.drawable.no_image_available);
        }

        if(!question.getQuestionImage().equals("")) {
            Glide
                    .with(getApplicationContext())
                    .load(question.getQuestionImage())
                    .placeholder(R.color.white)
                    .into(binding.ivQuestionImage);
        }
        else{
            binding.ivQuestionImage.setVisibility(View.GONE);
        }
        binding.ivQuestionUsername.setText(question.getUsername());
        binding.ivQuestionTitle.setText(question.getQuestionTitle());
        binding.ivQuestionDescription.setText(question.getQuestionDescription());
        binding.ivQuestionDate.setText(question.getQuestionDate());
    }

    public void getComments(){
        db.collection(Constants.FORUM).document(questionID).collection(Constants.COMMENT)
                .orderBy(Constants.COMMENT_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0){
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        commentID = new String[documents.size()];
                        comments = new Comment[documents.size()];
                        for (int i = 0; i < documents.size(); i++) {
                            comments[i] = new Comment(
                                    (String) documents.get(i).get(Constants.COMMENT_DESCRIPTION),
                                    (String)documents.get(i).get(Constants.COMMENT_DATE),
                                    (String) documents.get(i).get(Constants.USERNAME),
                                    ""
                            );
                            commentID[i] = documents.get(i).getId();
                        }
                        db.collection(Constants.USERS)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    List<DocumentSnapshot> documentSnapshot = task1.getResult().getDocuments();
                                    for (int i = 0; i < documentSnapshot.size(); i++) {
                                        for (Comment com : comments) {
                                            if (com.getUsername().equals(documentSnapshot.get(i).get(Constants.USERNAME))) {
                                                com.setUser_photo((String) documentSnapshot.get(i).get(Constants.PHOTO));
                                            }
                                        }
                                    }
                                    CommentAdapter commentAdapter = new CommentAdapter(this,comments);
                                    binding.gridListComments.setAdapter(commentAdapter);
                                    binding.gridListComments.setVisibility(View.VISIBLE);
                                    binding.tvComments.setVisibility(View.GONE);
                                });

                    }
                    else {
                        binding.gridListComments.setVisibility(View.GONE);
                        binding.tvComments.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(task->Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show());
    }

    public void addComment(){
        if(binding.etAddComment.getText().toString().equals("")){
            Toast.makeText(this, "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }
        long timeInMillis = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String commentDate = dateFormat.format(cal1.getTime());
        Comment comment = new Comment(
                binding.etAddComment.getText().toString(),
                commentDate,
                preferenceManager.getString(Constants.USERNAME),
                preferenceManager.getString(Constants.PHOTO)
        );
        db.collection(Constants.FORUM).document(questionID).collection(Constants.COMMENT)
                .add(comment)
                .addOnCompleteListener(task->{
                    Snackbar.make(findViewById(android.R.id.content), "Comment Created.", Snackbar.LENGTH_LONG).show();
                    binding.etAddComment.setText("");
                    getComments();
                })
                .addOnFailureListener(task-> Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show());
    }

    public void deleteQuestion(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_remove_circle_24)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this Question?")
                .setPositiveButton("Yes",(dialogInterface, i) -> db.collection(Constants.FORUM).document(questionID)
                        .delete()
                        .addOnCompleteListener(task -> {
                            // Create a storage reference from our app
                            StorageReference desertRef = storageReference.child("Forum/"+questionID);
                            desertRef.delete()
                            .addOnCompleteListener(task1 -> {
                                Toast.makeText(getApplicationContext(), "Question Deleted Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            })
                            .addOnFailureListener(task1 -> Toast.makeText(getApplicationContext(), "Failed to Delete", Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(task -> Toast.makeText(getApplicationContext(), "Failed to Delete", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }
    public void reportAction(String type){
        dialog.setContentView(R.layout.dialog_report);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_comment_question_id = dialog.findViewById(R.id.tv_comment_question_id);
        TextView tv_report_type = dialog.findViewById(R.id.tv_report_type);
        CheckBox checkbox_misleading = dialog.findViewById(R.id.checkbox_misleading);
        CheckBox checkbox_sexual = dialog.findViewById(R.id.checkbox_sexual);
        CheckBox checkbox_prohibited = dialog.findViewById(R.id.checkbox_prohibited);
        CheckBox checkbox_offensive = dialog.findViewById(R.id.checkbox_offensive);
        CheckBox checkbox_other = dialog.findViewById(R.id.checkbox_other);
        Button btn_send = dialog.findViewById(R.id.btn_send);
        ImageView btn_back = dialog.findViewById(R.id.iv_btn_back);

        if(type.equals("Question"))
            tv_comment_question_id.setText(questionID);
        else{
            tv_comment_question_id.setText(commentID[position]);
        }
        tv_report_type.setText(type);

        dialog.setOnCancelListener(DialogInterface::dismiss);

        btn_back.setOnClickListener(v1-> dialog.dismiss());

        btn_send.setOnClickListener(v1 -> {

            if(checkbox_misleading.isChecked() || checkbox_sexual.isChecked() || checkbox_prohibited.isChecked()
                    || checkbox_offensive.isChecked() || checkbox_other.isChecked()) {
                Timestamp timestamp = Timestamp.now();
                Date date = timestamp.toDate();
                String reportDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
                if(type.equals("Question")) {
                    Report report = new Report(
                            checkbox_misleading.isChecked(),
                            checkbox_sexual.isChecked(),
                            checkbox_prohibited.isChecked(),
                            checkbox_offensive.isChecked(),
                            checkbox_other.isChecked(),
                            type,
                            questionID,
                            reportDate,
                            question.getUsername()
                    );
                    db.collection("report")
                            .add(report)
                            .addOnCompleteListener(task -> {
                                Toast.makeText(this, "Report Sent!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
                }
                else{
                    CommentReport commentReport= new CommentReport(
                            checkbox_misleading.isChecked(),
                            checkbox_sexual.isChecked(),
                            checkbox_prohibited.isChecked(),
                            checkbox_offensive.isChecked(),
                            checkbox_other.isChecked(),
                            type,
                            questionID,
                            reportDate,
                            question.getUsername(),
                            commentID[position]
                    );
                    db.collection("report")
                            .add(commentReport)
                            .addOnCompleteListener(task -> {
                                Toast.makeText(this, "Report Sent!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
                }
            }
            else{
                Toast.makeText(this, "Please choose reasons", Toast.LENGTH_SHORT).show();
            }
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
        getQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}