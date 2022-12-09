package com.example.rianthai.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rianthai.internet.InternetReceiver;
import com.example.rianthai.databinding.ActivityTranslationResultBinding;
import com.example.rianthai.model.Translation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class TranslationResultActivity extends AppCompatActivity {

    private ActivityTranslationResultBinding binding;
    Document doc;
    Dialog dialog;
    String search;
    Translation trans;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranslationResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        broadcastReceiver = new InternetReceiver();
        Intent intent = getIntent();
        search = intent.getStringExtra("search").trim();
        binding.etSearch.setText(search);
        binding.tvWord.setText(search);

        binding.imgBtnBack.setOnClickListener(v-> onBackPressed());
        binding.ivSearch.setOnClickListener(v -> {
            InternetStatus();
            searchWord();
        });
        dialog = new Dialog(this);
        getWebsite(search);
    }

    public void searchWord(){
        if(!binding.etSearch.getText().toString().equals("")) {
            search = binding.etSearch.getText().toString().trim();
            binding.tvWord.setText(search);
            getWebsite(search);
        }
        else
            Toast.makeText(TranslationResultActivity.this, "Please search with English words~", Toast.LENGTH_SHORT).show();
    }

    public void getWebsite(String search){
        doc = null;
        trans = new Translation(search.toUpperCase());
        new Thread(() -> {
            StringBuilder translation = new StringBuilder();
            StringBuilder suggestion = new StringBuilder();
            String html = "https://dict.longdo.com/mobile.php?search="+search; //web page with search param
            try {
                doc = Jsoup.connect(html).get(); //Connect to the web page
                Elements tableRowElements = doc.select("table[class=result-table] :not(thead) tr"); //Take all tables
                for (int i = 0; i < tableRowElements.size(); i++) { //Loop to get suitable results
                    Element row = tableRowElements.get(i); //table element
                    Elements rowItems = row.select("td");	//td element
                    for (int j = 0; j < rowItems.size(); j++) {
                        trans.setWord(rowItems.get(0).text().toUpperCase());
                        if(trans.meaningChecker(rowItems.get(1).text())) //Take only search param and specific dictionary
                            trans.setMeaningArr(rowItems.get(1).text());//store in a list
                        else if(trans.suggestionChecker(rowItems.get(1).text())) {
                            trans.setSuggestionArr(rowItems.get(0).text()+" "+rowItems.get(1).text());
                        }
                    }
                }
                List<String> newList = trans.getMeaningArr().stream()
                        .distinct()
                        .collect(Collectors.toList()); //Remove Duplicate results
                Iterator<String> iter = newList.iterator();
                Iterator<String> iter2 = trans.getSuggestionArr().iterator();
                if (iter.hasNext()) {
                    while (iter.hasNext()) {
                        translation.append("- ").append(iter.next()).append("\n"); //Print result
                    }
                    if (iter2.hasNext()) {
                        while (iter2.hasNext()) {
                            suggestion.append("- ").append(iter2.next()).append("\n"); //Print result
                        }
                    } else {
                        suggestion.append("\n").append("Oops there is no suggestions for this word!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {

                if(!trans.getMeaningArr().isEmpty()) { //set Result condition
                    binding.svResult.setVisibility(View.VISIBLE);
                    binding.tvResultTranslation.setText(translation.toString()); //set output
                    binding.tvResultSuggestion.setText(suggestion.toString());
                    Toast.makeText(getApplicationContext(), "Result Found", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.svResult.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Result Not Found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
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