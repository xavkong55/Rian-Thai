package com.example.rianthai.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivityHomepageBinding;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomepageActivity extends AppCompatActivity {

    private ActivityHomepageBinding binding;
    FirebaseAuth firebaseAuth;
    PreferenceManager preferenceManager;
    FirebaseUser user;
    DocumentReference userDocRef;
    FirebaseFirestore db;
    Dialog dialog;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(this);
        broadcastReceiver = new InternetReceiver();
        firebaseAuth = FirebaseUtil.getAuth();
        db = FirebaseUtil.getFirestore();
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = firebaseAuth.getCurrentUser();
        userDocRef = db.collection(Constants.USERS).document(user.getUid());
        NavController navController = Navigation.findNavController(
                this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(binding.btmNavigationBar, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> binding.tvTopActionBar.setText(destination.getLabel()));
    }

    private void initInfo() {
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = (String) Objects.requireNonNull(document.getData()).get(Constants.USERNAME);
                    String email = (String)document.getData().get(Constants.EMAIL);
                    String gender = (String)document.getData().get(Constants.GENDER);
                    String occ = (String)document.getData().get(Constants.OCCUPATION);
                    String imgURL = (String)document.getData().get(Constants.PHOTO);
                    String contactNum = (String)document.getData().get(Constants.CONTACT_NUMBER);

                    preferenceManager.putString(Constants.UserID,document.getId());
                    preferenceManager.putString(Constants.USERNAME,username);
                    preferenceManager.putString(Constants.EMAIL,email);
                    preferenceManager.putString(Constants.GENDER,gender);
                    preferenceManager.putString(Constants.OCCUPATION,occ);
                    preferenceManager.putString(Constants.PHOTO,imgURL);
                    preferenceManager.putString(Constants.CONTACT_NUMBER,contactNum);
                }
            }
        });
    }

    public void InternetStatus(){
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initInfo();
        InternetStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}