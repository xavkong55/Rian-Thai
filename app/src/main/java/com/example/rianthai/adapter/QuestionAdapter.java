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
import com.example.rianthai.model.Question;

public class QuestionAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    Question[] question;

    public QuestionAdapter(Context context, Question[] question) {
        this.context = context;
        this.question = question;
    }

    @Override
    public int getCount() {
        return question.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater == null){
            layoutInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.grid_item_questions,null);
        }

        ImageView iv_Image = convertView.findViewById(R.id.question_image);
        TextView tv_question_title = convertView.findViewById(R.id.question_title);
        TextView tv_question_des = convertView.findViewById(R.id.question_description);
        TextView tv_see_more = convertView.findViewById(R.id.tv_see_more);
        TextView tv_timestamp = convertView.findViewById(R.id.tv_timestamp);
        TextView tv_username = convertView.findViewById(R.id.iv_question_username);
        ImageView iv_user_image = convertView.findViewById(R.id.iv_question_user_image);
        tv_username.setText(question[position].getUsername());

        if(!question[position].getUserPhoto().equals(""))
            Glide.with(convertView.getContext()).load(question[position].getUserPhoto()).placeholder(R.color.white).into(iv_user_image);
        else
            iv_user_image.setImageResource(R.drawable.no_image_available);

        if(!question[position].getQuestionImage().equals("")) {
            Glide.with(convertView.getContext()).load(question[position].getQuestionImage()).placeholder(R.color.white).into(iv_Image);
        }
        else{
            iv_Image.setImageResource(R.drawable.no_image_available);
        }
        tv_question_title.setText(question[position].getQuestionTitle());

        if(question[position].getQuestionDescription().length()<80) {
            tv_question_des.setText(question[position].getQuestionDescription());
            tv_see_more.setVisibility(View.GONE);
        }else {
            tv_question_des.setText(question[position].getQuestionDescription().substring(0,80)+"...");
            tv_see_more.setVisibility(View.VISIBLE);
        }
        tv_timestamp.setText(question[position].getQuestionDate());

        return convertView;
    }
}
