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

public class ChapterAdapter extends BaseAdapter {

    Context context;
    String [] chapterName,images;

    LayoutInflater layoutInflater;

    public ChapterAdapter(Context context, String[] chapterName, String[] images) {
        this.context = context;
        this.chapterName = chapterName;
        this.images = images;
    }


    @Override
    public int getCount() {
        return chapterName.length;
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

        if(layoutInflater == null){
            layoutInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.grid_item_chapter,null);
        }

        ImageView imageView = convertView.findViewById(R.id.chapter_image);
        TextView textView = convertView.findViewById(R.id.chapter_name);

        Glide
                    .with(convertView)
                    .load(images[position])
                    .placeholder(R.color.white)
                    .into(imageView);

        textView.setText(chapterName[position]);

        return convertView;
    }



}