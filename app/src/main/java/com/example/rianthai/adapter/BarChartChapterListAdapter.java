package com.example.rianthai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rianthai.R;

public class BarChartChapterListAdapter extends BaseAdapter {
    Context context;
    String [] chapterName;

    LayoutInflater layoutInflater;

    public BarChartChapterListAdapter(Context context, String[] chapterName) {
        this.context = context;
        this.chapterName = chapterName;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater == null){
            layoutInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.grid_item_bar_chart,null);
        }

        ImageView imageView = convertView.findViewById(R.id.bar_chart_label_color);
        TextView textView = convertView.findViewById(R.id.bar_chart_label_name);

        int[] bar_color = context.getResources().getIntArray(R.array.bar_colors);

        imageView.setBackgroundColor(bar_color[position]);
        textView.setText(chapterName[position]);

        return convertView;
    }
}