package com.example.rianthai.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rianthai.R;

public class CourseViewHolder {

    ImageView itemImage;
    TextView courseTitle;
    TextView courseDescription;

    public CourseViewHolder(View v){
        itemImage = v.findViewById(R.id.course_image);
        courseTitle = v.findViewById(R.id.course_title);
        courseDescription = v.findViewById(R.id.course_description);
    }
}
