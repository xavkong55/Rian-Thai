package com.example.rianthai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivitySignUpBinding;
import com.example.rianthai.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnConfirm.setOnClickListener(v -> {
            String username = binding.etNameSignup.getText().toString().trim();
            String email = binding.etEmailSignup.getText().toString().trim();
            String password = binding.etPasswordSignup.getText().toString().trim();
            String confirmPassword = binding.etConfirmPasswordSignup.getText().toString().trim();
            if(inputValidation(username,email,password,confirmPassword)) {
                new AlertDialog.Builder(SignUpActivity.this)
                        .setIcon(R.drawable.ic_baseline_account_circle_24)
                        .setTitle("Sign Up")
                        .setMessage("Are you sure you want to proceed?")
                        .setPositiveButton("Yes", (dialogInterface, a) -> {
                            db.collection("users")
                                    .whereEqualTo("username",username)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            if(!documentSnapshot.getId().isEmpty()){
                                                Toast.makeText(SignUpActivity.this, "Username existed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {

                                                    //send verification link
                                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                    assert firebaseUser != null;
                                                    firebaseUser.sendEmailVerification().addOnSuccessListener(task2 -> {
                                                        Toast.makeText(SignUpActivity.this, "Account created. We have sent email to "+email+
                                                                " to confirm your email. Kindly check your inbox!", Toast.LENGTH_SHORT).show();

                                                        uid = firebaseAuth.getUid();
                                                        User newUser = new User(
                                                                uid,
                                                                username,
                                                                email
                                                        );
                                                        db.collection("users").document(uid).set(newUser);

                                                        binding.etNameSignup.setText("");
                                                        binding.etEmailSignup.setText("");
                                                        binding.etPasswordSignup.setText("");
                                                        binding.etConfirmPasswordSignup.setText("");

                                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                        firebaseAuth.signOut();
                                                        finish();
                                                    });
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(SignUpActivity.this, "Sign up failed. This E-mail has been used",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                        .show();
            }
        });

    }
    public boolean inputValidation(String username, String email, String password, String confirmPassword){
        if(TextUtils.isEmpty(username) || username.length() > 12){
            binding.layoutName.setError("Username is required and not more than 12.");
            return false;
        }else{
            binding.layoutName.setError(null);
        }
        if(TextUtils.isEmpty(email)){
            binding.layoutEmail.setError("Email is required.");
            return false;
        }
        else{
            binding.layoutEmail.setError(null);
        }
        if(TextUtils.isEmpty(password)){
            binding.layoutPasswordSignup.setError("Password is required.");
            return false;
        }
        if(password.length()<8 || (!password.matches("^(?=.*[_.()$&@]).*$")) || (!password.matches("(.*[A-Z].*)"))){
            binding.layoutPasswordSignup.setError("Password must be >= 8 characters and it must contain at least 1 upper case, 1 number and 1 special symbol");
            return false;
        }
        if(!password.equals(confirmPassword)||TextUtils.isEmpty(confirmPassword)){
            binding.layoutPasswordSignup.setError("Password is not matched with confirm password");
            return false;
        }else{
            binding.layoutPasswordSignup.setError(null);
            return true;
        }
    }
}