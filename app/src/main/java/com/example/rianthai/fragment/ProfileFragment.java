package com.example.rianthai.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.rianthai.R;
import com.example.rianthai.activity.EditProfileActivity;
import com.example.rianthai.activity.LoginActivity;
import com.example.rianthai.adapter.BarChartChapterListAdapter;
import com.example.rianthai.databinding.FragmentProfileBinding;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    DocumentReference docRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    PreferenceManager preferenceManager;
    String[] title,basicArr, grammarArr,vocabArr;
    private FragmentProfileBinding binding;
    private int[] bar_color,basicValue, grammarValue,vocabValue;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        firebaseAuth = FirebaseUtil.getAuth();
        db = FirebaseUtil.getFirestore();
        user = firebaseAuth.getCurrentUser();
        docRef = db.collection("users").document(user.getUid());
        title = new String[]{"Course Progress by Chapter(%)","Quiz Results by Chapter(%)"};
        binding.lblTitle.setText(title[0]);
        bar_color = getResources().getIntArray(R.array.bar_colors); //colors list

        preferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
        binding.etID.setText(preferenceManager.getString(Constants.USERNAME));
        binding.etEmail.setText(preferenceManager.getString(Constants.EMAIL));

        if(!preferenceManager.getString(Constants.PHOTO).equals("")) {
            Glide
                    .with(ProfileFragment.this)
                    .load(preferenceManager.getString(Constants.PHOTO))
                    .placeholder(R.color.white)
                    .into(binding.ivProfile);
        }
        else
            binding.ivProfile.setImageResource(R.drawable.no_image_available);

        binding.btnSignOut.setOnClickListener(v->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setIcon(R.drawable.ic_baseline_logout_24)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout this account?")
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .setPositiveButton("Yes",(dialogInterface, i) ->{
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        preferenceManager.clear();
                        getActivity().finish();
                    })
                    .show();
        });

        binding.btnEdit.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });
        binding.btnBasic.setOnClickListener(this);
        binding.btnVocabulary.setOnClickListener(this);
        binding.btnGrammar.setOnClickListener(this);
        binding.btnSwitch.setOnClickListener(this);

        getChapterList();

        return binding.getRoot();
    }

    public void getChapterList(){
        db.collection(Constants.COURSE).document("basic").collection(Constants.CHAPTER)
                .orderBy(Constants.CHAPTER_NAME, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size()>0) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        basicArr = new String[documentSnapshots.size()];
                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            basicArr[i] = (String) documentSnapshots.get(i).get(Constants.CHAPTER_NAME);
                        }
                        basicValue = new int[basicArr.length];
                        Arrays.fill(basicValue,0);

                        db.collection(Constants.COURSE).document("vocabulary").collection(Constants.CHAPTER)
                                .orderBy(Constants.CHAPTER_NAME, Query.Direction.ASCENDING)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful() && task1.getResult().size()>0) {
                                        List<DocumentSnapshot> document2 = task1.getResult().getDocuments();
                                        vocabArr = new String[document2.size()];
                                        for (int i = 0; i < document2.size(); i++) {
                                            vocabArr[i] = (String) document2.get(i).get(Constants.CHAPTER_NAME);
                                        }
                                        vocabValue = new int[vocabArr.length];
                                        Arrays.fill(vocabValue,0);
                                        db.collection(Constants.COURSE).document("grammar").collection(Constants.CHAPTER)
                                                .orderBy(Constants.CHAPTER_NAME, Query.Direction.ASCENDING)
                                                .get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful() && task2.getResult().size()>0) {
                                                        List<DocumentSnapshot> document3 = task2.getResult().getDocuments();
                                                        grammarArr = new String[document3.size()];
                                                        for (int i = 0; i < document3.size(); i++) {
                                                            grammarArr[i] = (String) document3.get(i).get(Constants.CHAPTER_NAME);
                                                        }
                                                        grammarValue = new int[grammarArr.length];
                                                        Arrays.fill(grammarValue,0);
                                                        getValues("chapterProgress");
                                                        binding.btnSwitch.setVisibility(View.VISIBLE);
                                                        binding.btnBasic.setVisibility(View.VISIBLE);
                                                        binding.btnVocabulary.setVisibility(View.VISIBLE);
                                                        binding.btnGrammar.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    public void getValues(String result){
        Arrays.fill(basicValue,0);
        Arrays.fill(vocabValue,0);
        Arrays.fill(grammarValue,0);
        db.collection(Constants.USERS).document(preferenceManager.getString(Constants.UserID)).collection(Constants.HISTORY)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size()>0) {
                        List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                        for (int i = 0; i < documentSnapshot.size(); i++) {
                            if(Objects.equals(documentSnapshot.get(i).get(Constants.COURSE_NAME), "basic")) {
                                for (int j = 0; j < basicArr.length; j++) {
                                    if (basicArr[j].equals(documentSnapshot.get(i).get(Constants.CHAPTER_NAME))) {
                                        basicValue[j] = Objects.requireNonNull(documentSnapshot.get(i).getLong(result)).intValue();
                                    }
                                }
                            }
                            else if(Objects.equals(documentSnapshot.get(i).get(Constants.COURSE_NAME), "vocabulary")){
                                for (int j = 0; j < vocabArr.length; j++) {
                                    if (vocabArr[j].equals(documentSnapshot.get(i).get(Constants.CHAPTER_NAME))) {
                                        vocabValue[j] = Objects.requireNonNull(documentSnapshot.get(i).getLong(result)).intValue();
                                    }
                                }
                            }
                            else if (Objects.equals(documentSnapshot.get(i).get(Constants.COURSE_NAME), "grammar")){
                                for (int j = 0; j < grammarArr.length; j++) {
                                    if (grammarArr[j].equals(documentSnapshot.get(i).get(Constants.CHAPTER_NAME))) {
                                        grammarValue[j] = Objects.requireNonNull(documentSnapshot.get(i).getLong(result)).intValue();
                                    }
                                }
                            }
                        }
                    }
                    initialGraphData();
                });
    }


    public void getBarChart(int[] value,String[] chapterArr){ //Create bar chart
        //remove unnecessary properties
        binding.barchartAnalysis.setDrawBarShadow(false);
        binding.barchartAnalysis.getDescription().setEnabled(false);
        binding.barchartAnalysis.setPinchZoom(false);
        binding.barchartAnalysis.setDrawGridBackground(false);
        binding.barchartAnalysis.getAxisRight().setEnabled(false);
        binding.barchartAnalysis.getAxisLeft().setEnabled(false);
        binding.barchartAnalysis.getLegend().setEnabled(false);

        // Set x-axis format
        XAxis xAxis = binding.barchartAnalysis.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1.0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawLabels(false);

        // Set y-axis format
        YAxis yAxis = binding.barchartAnalysis.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(12);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(101);

        //Declare Variables
        ArrayList<BarEntry> bars = new ArrayList<>();
        BarDataSet sets;
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();

        //Insert values
        for (int i=0;i<value.length;i++){
            bars.add(new BarEntry(i,value[i]));
        }

        //set datasets
        sets = new BarDataSet(bars,"");
        sets.setValueFormatter(new IntValueFormatter());
        sets.setHighlightEnabled(false);
        sets.setColors(bar_color);
        sets.setBarBorderWidth(1);
        sets.setValueTextSize(12);
        dataSets.add(sets);

        //set adapter for chapter lists
        BarChartChapterListAdapter chapterAdapter = new BarChartChapterListAdapter(getActivity(), chapterArr);
        binding.gridView.setAdapter(chapterAdapter);

        BarData data = new BarData(dataSets);
        float barWidth = 1f;
        data.setBarWidth(barWidth); //set Bar width

        // so that the entire chart is shown
        binding.barchartAnalysis.setData(data);
        binding.barchartAnalysis.setScaleEnabled(false);
        binding.barchartAnalysis.invalidate();
    }

    public static class IntValueFormatter implements IValueFormatter { //to convert x-axis value labels to integer

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) value);
        }
    }

    public void setDefaultButton(Button clickedButton){ //set default button
        clickedButton.setClickable(true);
        clickedButton.setBackgroundResource(R.drawable.btn_dark_green);
        clickedButton.setTextColor(Color.parseColor("#000000"));
    }
    public void setClickedButton(Button clickedButton){ //Set clicked button
        clickedButton.setClickable(false);
        clickedButton.setBackgroundResource(R.drawable.btn_light_green);
        clickedButton.setTextColor(Color.parseColor("#98000000"));
    }
    @Override
    public void onClick(View v) { //Onclick function
        if(binding.btnBasic.equals(v)){
            initialGraphData();
        }
        else if(binding.btnVocabulary.equals(v)){
            setClickedButton(binding.btnVocabulary);
            setDefaultButton(binding.btnBasic);
            setDefaultButton(binding.btnGrammar);
            getBarChart(vocabValue,vocabArr);
        }
        else if(binding.btnGrammar.equals(v)){
            setClickedButton(binding.btnGrammar);
            setDefaultButton(binding.btnBasic);
            setDefaultButton(binding.btnVocabulary);
            getBarChart(grammarValue,grammarArr);
        }
        else if(binding.btnSwitch.equals(v)){
            if(binding.lblTitle.getText().equals(title[0])) {
                binding.lblTitle.setText(title[1]);
                getValues("quizResults");
            }
            else{
                binding.lblTitle.setText(title[0]);
                getValues("chapterProgress");
            }
        }
    }

    public void initialGraphData(){
        setClickedButton(binding.btnBasic);
        setDefaultButton(binding.btnVocabulary);
        setDefaultButton(binding.btnGrammar);
        getBarChart(basicValue,basicArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!preferenceManager.getString(Constants.PHOTO).equals("")) {
            Glide
                    .with(ProfileFragment.this)
                    .load(preferenceManager.getString(Constants.PHOTO))
                    .placeholder(R.color.white)
                    .into(binding.ivProfile);
        }
        else
            binding.ivProfile.setImageResource(R.drawable.no_image_available);
    }
}