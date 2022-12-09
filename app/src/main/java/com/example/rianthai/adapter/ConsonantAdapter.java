package com.example.rianthai.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rianthai.R;
import com.example.rianthai.util.PreferenceManager;

public class ConsonantAdapter extends BaseAdapter {
    Context context;
    PreferenceManager preferenceManager;
    String thai_word, word_pron, eng_word, image;
    LayoutInflater layoutInflater;

    public ConsonantAdapter(Context context,  String image, String thai_word, String word_pron, String eng_word) {
        this.context = context;
        this.image = image;
        this.thai_word = thai_word;
        this.word_pron = word_pron;
        this.eng_word = eng_word;
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

            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.grid_item_consonant, null);
                consonantView(convertView);
            }
            return convertView;
        }
    public void consonantView(View convertView){
        TextView consonant_name = convertView.findViewById(R.id.tv_consonant_title);
        TextView consonant_pron = convertView.findViewById(R.id.tv_consonant_pron);
        TextView consonant_eng = convertView.findViewById(R.id.tv_consonant_eng);
        consonant_name.setText(thai_word);
        consonant_pron.setText(word_pron);
        consonant_eng.setText(eng_word);
        ImageView consonant_image = convertView.findViewById(R.id.iv_content_photo);

        if (!image.equals("")) {
            Glide
                    .with(convertView.getContext())
                    .load(image)
                    .placeholder(R.color.white)
                    .into(consonant_image);
        }
    }
}
