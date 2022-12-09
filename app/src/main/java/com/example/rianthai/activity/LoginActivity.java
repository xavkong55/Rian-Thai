package com.example.rianthai.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivityLoginBinding;
import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    Dialog dialog;
    BroadcastReceiver broadcastReceiver = null;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        firebaseAuth = FirebaseUtil.getAuth();
        db = FirebaseUtil.getFirestore();
        dialog = new Dialog(this);
        preferenceManager = new PreferenceManager(getApplicationContext());

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
            finish();
        }

        binding.txtForgetPassword.setOnClickListener(v -> forgetPassword());
        binding.txtSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        binding.btnLogin.setOnClickListener(v -> loginAccount());
    }

    public void forgetPassword(){
        dialog.setContentView(R.layout.dialog_reset_password);
        EditText et_confirmation_email = dialog.findViewById(R.id.et_confirmation_email);
        Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        ImageView iv_btn_back = dialog.findViewById(R.id.iv_btn_back);

        iv_btn_back.setOnClickListener(v1 -> dialog.dismiss());
        dialog.setOnCancelListener(DialogInterface::dismiss);

        btn_confirm.setOnClickListener(v12 -> {
            String reset_email = et_confirmation_email.getText().toString().trim();
            if(!reset_email.equals("")) {
                db.collection("users")
                        .whereEqualTo("email", reset_email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                user = firebaseAuth.getCurrentUser();
                                if (!documentSnapshot.getId().isEmpty() && task.getResult().getDocuments().size() > 0) {
                                    firebaseAuth.sendPasswordResetEmail(reset_email)
                                            .addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error! Reset Link Is Not Sent", Toast.LENGTH_SHORT).show());
                                    btn_confirm.setText("");
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                            }

                        });
            }
            else{
                Toast.makeText(LoginActivity.this, "Please write your email", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }

    public void loginAccount(){

        String email = Objects.requireNonNull(binding.etEmailLogin.getText()).toString().trim();
        String userPassword = Objects.requireNonNull(binding.etPasswordLogin.getText()).toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(userPassword)) {
            firebaseAuth.signInWithEmailAndPassword(email, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                        user = firebaseAuth.getCurrentUser();
                        if(user!=null) {
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                                finish();
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                            }
                        }
                } else
                    Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            });
        }
        else{
            Toast.makeText(this, "Insert your email and password", Toast.LENGTH_SHORT).show();
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