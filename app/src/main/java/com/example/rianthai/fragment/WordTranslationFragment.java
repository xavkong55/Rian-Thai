package com.example.rianthai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rianthai.activity.TranslationResultActivity;
import com.example.rianthai.databinding.FragmentWordTranslationBinding;
import com.example.rianthai.util.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;

public class WordTranslationFragment extends Fragment {

    private FragmentWordTranslationBinding binding;
    private long mLastClickTime = 0;
    FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWordTranslationBinding.inflate(inflater,container,false);
        db = FirebaseUtil.getFirestore();
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !binding.etSearch.getText().toString().equals("")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                   return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getActivity(), TranslationResultActivity.class);
                intent.putExtra("search",binding.etSearch.getText().toString());
                binding.etSearch.setText("");
                startActivity(intent);
                handled = true;
            }
            else{
                Toast.makeText(getActivity(), "Please search with English words~", Toast.LENGTH_SHORT).show();
            }
            return handled;
        });

        binding.ivSearchIcon.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if(!binding.etSearch.getText().toString().equals("")) {
                Intent intent = new Intent(getActivity(), TranslationResultActivity.class);
                intent.putExtra("search", binding.etSearch.getText().toString());
                startActivity(intent);
            }
            else
                Toast.makeText(getActivity(), "Please search with English words~", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }
}