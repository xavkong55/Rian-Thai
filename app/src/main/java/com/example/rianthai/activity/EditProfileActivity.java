package com.example.rianthai.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.R;
import com.example.rianthai.databinding.ActivityEditprofileBinding;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity{

    private ActivityEditprofileBinding binding;
    PreferenceManager preferenceManager;
    Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    String[] genderSelection = {"Male", "Female", "Prefer Not to Say"};
    String[] occupationSelection = {"Student", "Teacher", "Employee","Employer","Blogger","Others"};
    private long mLastClickTime = 0;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        db = FirebaseUtil.getFirestore();
        FirebaseStorage storage = FirebaseUtil.getStorage();
        storageReference = storage.getReference();

        broadcastReceiver = new InternetReceiver();
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.imgBtnBack.setOnClickListener(v -> onBackPressed());
        binding.btnUpload.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            selectImage();
        });
        binding.btnUpdate.setOnClickListener(v -> {
            if(!broadcastReceiver.toString().equals("disconnected")){
                uploadProfile();
            }
            else
                InternetStatus();
        });
        binding.etContactNumber.setText(preferenceManager.getString(Constants.CONTACT_NUMBER));

        setGenderSpinner();
        setOccupationSpinner();

        if(!preferenceManager.getString(Constants.PHOTO).equals("")) {
            Glide
                    .with(EditProfileActivity.this)
                    .load(preferenceManager.getString(Constants.PHOTO))
                    .placeholder(R.color.white)
                    .into(binding.ivProfile);
        }
    }
    public void setOccupationSpinner(){
        ArrayAdapter<String>adapter = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_item, occupationSelection);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerOccupation.setAdapter(adapter);

        if(preferenceManager.getString(Constants.OCCUPATION).equals(occupationSelection[0])){
            binding.spinnerOccupation.setSelection(0);
        }
        else if(preferenceManager.getString(Constants.OCCUPATION).equals(occupationSelection[1])){
            binding.spinnerOccupation.setSelection(1);
        }
        else if(preferenceManager.getString(Constants.OCCUPATION).equals(occupationSelection[2])){
            binding.spinnerOccupation.setSelection(2);
        }
        else if(preferenceManager.getString(Constants.OCCUPATION).equals(occupationSelection[3])){
            binding.spinnerOccupation.setSelection(3);
        }
        else if (preferenceManager.getString(Constants.OCCUPATION).equals(occupationSelection[4])){
            binding.spinnerOccupation.setSelection(4);
        }
        else
            binding.spinnerOccupation.setSelection(5);
    }

    public void setGenderSpinner(){
        ArrayAdapter<String>adapter2 = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_item, genderSelection);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(adapter2);

        if (preferenceManager.getString(Constants.GENDER).equals(genderSelection[0]))
            binding.spinnerGender.setSelection(0);
        else if (preferenceManager.getString(Constants.GENDER).equals(genderSelection[1]))
            binding.spinnerGender.setSelection(1);
        else
            binding.spinnerGender.setSelection(2);
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
            Glide
                    .with(EditProfileActivity.this)
                    .load(imageUri)
                    .placeholder(R.color.white)
                    .into(binding.ivProfile);
        }
    }

    private void uploadProfile(){

        String contactNum = binding.etContactNumber.getText().toString();

        if(!contactNum.equals("") && (contactNum.length() == 10 || contactNum.length() == 11)){
            Toast.makeText(getApplicationContext(), "Please write correct contact number", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference profilePic =  storageReference.child("Profile/"+preferenceManager.getString(Constants.UserID));
        if(imageUri != null) {
            profilePic.putFile(imageUri)
                    .addOnCompleteListener(task -> profilePic.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(Constants.PHOTO, uri.toString());
                        updates.put(Constants.CONTACT_NUMBER, binding.etContactNumber.getText().toString());
                        updates.put(Constants.GENDER, binding.spinnerGender.getSelectedItem().toString());
                        updates.put(Constants.OCCUPATION, binding.spinnerOccupation.getSelectedItem().toString());
                        db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                                .update(updates)
                                .addOnCompleteListener(task1 -> {
                                    progressDialog.dismiss();
                                    preferenceManager.putString(Constants.PHOTO, uri.toString());
                                    preferenceManager.putString(Constants.OCCUPATION, binding.spinnerOccupation.getSelectedItem().toString());
                                    preferenceManager.putString(Constants.CONTACT_NUMBER, binding.etContactNumber.getText().toString());
                                    preferenceManager.putString(Constants.GENDER, binding.spinnerGender.getSelectedItem().toString());
                                    Snackbar.make(findViewById(android.R.id.content), "Profile Updated.", Snackbar.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_LONG).show();
                                });
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Percentage: " + (int) progressPercent + "%");
                    });
        }
        else {
            Map<String, Object> updates = new HashMap<>();
            updates.put(Constants.CONTACT_NUMBER, binding.etContactNumber.getText().toString());
            updates.put(Constants.GENDER, binding.spinnerGender.getSelectedItem().toString());
            updates.put(Constants.OCCUPATION, binding.spinnerOccupation.getSelectedItem().toString());
            db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID))
                    .update(updates)
                    .addOnCompleteListener(task1 -> {
                        progressDialog.dismiss();
                        preferenceManager.putString(Constants.OCCUPATION, binding.spinnerOccupation.getSelectedItem().toString());
                        preferenceManager.putString(Constants.CONTACT_NUMBER, binding.etContactNumber.getText().toString());
                        preferenceManager.putString(Constants.GENDER, binding.spinnerGender.getSelectedItem().toString());
                        Snackbar.make(findViewById(android.R.id.content), "Profile Updated.", Snackbar.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_LONG).show();
                    });
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