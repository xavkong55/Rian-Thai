package com.example.rianthai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.rianthai.activity.ChapterActivity;
import com.example.rianthai.R;
import com.example.rianthai.util.Constants;
import com.example.rianthai.util.FirebaseUtil;
import com.example.rianthai.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CourseAdapter extends ArrayAdapter<String> {

    Context context;
    int[] images;
    String[] courseName;
    String[] courseDescription;
    PreferenceManager preferenceManager;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private long mLastClickTime = 0;

    public CourseAdapter(Context context, String[] courseName, int[] images, String[] courseDescription) {
        super(context, R.layout.grid_item_course,R.id.course_title,courseName);
        this.context = context;
        this.images = images;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        preferenceManager = new PreferenceManager(getContext());

        View singleItem = convertView;
        CourseViewHolder holder;
        firebaseAuth = FirebaseUtil.getAuth();
        db = FirebaseUtil.getFirestore();
        preferenceManager = new PreferenceManager(getContext());

        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.grid_item_course,parent,false);
            holder = new CourseViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else{
            holder = (CourseViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageResource(images[position]);
        holder.courseTitle.setText(courseName[position]);
        holder.courseDescription.setText(courseDescription[position]);
        singleItem.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
              db.collection(Constants.COURSE).document(getCourse(position).toLowerCase())
                      .collection(Constants.CHAPTER).orderBy(Constants.CHAPTER_NAME, Query.Direction.ASCENDING)
                      .get()
                      .addOnCompleteListener(task -> {
                      if (task.isSuccessful()) {
                          String[] chapterList = new String[task.getResult().size()];
                          String[] chapterImages = new String[task.getResult().size()];
                          int count =0;
                          for (QueryDocumentSnapshot document : task.getResult()) {
                              chapterList[count] = (String)document.get(Constants.CHAPTER_NAME);
                              chapterImages[count] = (String) document.get(Constants.CHAPTER_IMAGE);
                              count++;
                          }
                          preferenceManager.putString(Constants.COURSE_NAME, getCourse(position).toLowerCase());
                          Intent intent = new Intent(context,ChapterActivity.class);
                          intent.putExtra(Constants.CHAPTER_IMAGE,chapterImages);
                          intent.putExtra(Constants.CHAPTER_LIST, chapterList);
                          intent.putExtra(Constants.COURSE_NAME,getCourse(position));
                          context.startActivity(intent);
                      }
                  });
        });
        return singleItem;
    }

    public String getCourse(int position){
        if(position == 0)
            return "Basic";
        else if (position == 1)
            return  "Vocabulary";
        else
            return "Grammar";
    }
}
