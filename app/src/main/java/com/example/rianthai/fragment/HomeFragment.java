package com.example.rianthai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rianthai.R;
import com.example.rianthai.activity.QuestionForumActivity;
import com.example.rianthai.adapter.CourseAdapter;
import com.example.rianthai.databinding.FragmentHomeBinding;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeFragment extends Fragment{

    String[] courseName = {"Basic","Vocabulary","Grammar"};
    String[] courseDescription = {"Learn Basic Thai: Vocal and Consonants",
            "Learn Vocabulary through fun examples",
            "Learn Grammar to speak Thai more effectively"
    };
    int[] courseImage = {R.drawable.easy_course,R.drawable.vocabulary,R.drawable.advanced_course};
    FirebaseFirestore db;
    PreferenceManager preferenceManager;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.example.rianthai.databinding.FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(Objects.requireNonNull(getActivity()));

        CourseAdapter courseAdapter = new CourseAdapter(getActivity(), courseName,courseImage,courseDescription);
        binding.lvCourse.setAdapter(courseAdapter);

        binding.cardViewYourQuestions.setOnClickListener(v->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent = new Intent(getActivity(), QuestionForumActivity.class);
            intent.putExtra("title","Your Questions");
            startActivity(intent);
        });
        binding.cardViewQuestionList.setOnClickListener(v->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent = new Intent(getActivity(), QuestionForumActivity.class);
            intent.putExtra("title","Question List");
            startActivity(intent);
        });

        return binding.getRoot();
    }
}