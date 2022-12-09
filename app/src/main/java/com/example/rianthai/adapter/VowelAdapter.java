package com.example.rianthai.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rianthai.R;
import com.example.rianthai.util.PreferenceManager;

public class VowelAdapter extends BaseAdapter {
    Context context;
    String description;
    PreferenceManager preferenceManager;
    LayoutInflater layoutInflater;

    public VowelAdapter(Context context, String description) {
        this.context = context;
        this.description = description;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        preferenceManager = new PreferenceManager(context);

        if(layoutInflater == null){
            layoutInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_vowel,null);
            TextView vocal_name = convertView.findViewById(R.id.tv_vowel_note);
            vocal_name.setText(description);
        }
        return convertView;
    }
}
