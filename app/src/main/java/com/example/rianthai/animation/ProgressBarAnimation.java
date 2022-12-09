package com.example.rianthai.animation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rianthai.activity.QuizActivity;
import com.example.rianthai.model.Quiz;

import java.io.Serializable;

public class ProgressBarAnimation extends Animation {

    private final Context context;
    private final ProgressBar progressBar;
    private final TextView textView;
    private final float from;
    private final float to;
    private final Quiz[] quiz;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to, Quiz[] quiz) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
        this.quiz = quiz;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        String loadingNum = (int) value+" %";

        progressBar.setProgress((int)value);
        textView.setText(loadingNum);
        if(value == to){
            Intent intent = new Intent(context, QuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("quiz", (Serializable) quiz);
            context.startActivity(intent);
            ((Activity)(context)).finish();
        }
    }
}
