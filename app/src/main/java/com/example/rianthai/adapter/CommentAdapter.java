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
import com.example.rianthai.model.Comment;

public class CommentAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    Comment[] comment;

    public CommentAdapter(Context context, Comment[] comment) {
        this.context = context;
        this.comment = comment;
    }

    @Override
    public int getCount() {
        return comment.length;
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
            convertView = layoutInflater.inflate(R.layout.grid_item_comments,null);
        }

        TextView tv_comment_des = convertView.findViewById(R.id.iv_comment_description);
        TextView tv_comment_date = convertView.findViewById(R.id.tv_timestamp);
        TextView tv_username = convertView.findViewById(R.id.iv_comment_username);
        ImageView iv_user_image = convertView.findViewById(R.id.iv_comment_user_image);

        tv_username.setText(comment[position].getUsername());

        if(!comment[position].getUser_photo().equals(""))
            Glide
                    .with(convertView.getContext())
                    .load(comment[position].getUser_photo())
                    .placeholder(R.color.white)
                    .into(iv_user_image);
        else
            iv_user_image.setImageResource(R.drawable.no_image_available);

        tv_comment_des.setText(comment[position].getCommentDescription());
        tv_comment_date.setText(comment[position].getCommentDate());

        return convertView;
    }
}
