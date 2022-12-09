package com.example.rianthai.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.rianthai.R;
import com.example.rianthai.util.PreferenceManager;

import java.io.IOException;

public class VocabGrammarAdapter extends BaseAdapter {
    Context context;
    String[] thai_word, word_pron, eng_word, audio, note;
    boolean up;
    PreferenceManager preferenceManager;
    LayoutInflater layoutInflater;

    public VocabGrammarAdapter(Context context, String[] thai_word, String[] word_pron, String[] eng_word, String[] audio, String[] note) {
        this.context = context;
        this.thai_word = thai_word;
        this.word_pron = word_pron;
        this.eng_word = eng_word;
        this.note = note;
        this.audio = audio;
    }

    @Override
    public int getCount() { return thai_word.length; }

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

        preferenceManager = new PreferenceManager(context);

        if(layoutInflater == null){
            layoutInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_vocab_grammar,null);
            vocabGrammar(convertView,position);
        }
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    public void vocabGrammar(View convertView, int position){

        TextView tv_word = convertView.findViewById(R.id.tv_title);
        TextView tv_name = convertView.findViewById(R.id.tv_eng);
        TextView tv_pron = convertView.findViewById(R.id.tv_pron);
        TextView tv_note_details = convertView.findViewById(R.id.tv_note_details);
        ImageView iv_audio = convertView.findViewById(R.id.iv_vocab_grammar_audio);
        TextView tv_note = convertView.findViewById(R.id.tv_note);

        tv_word.setText(thai_word[position]);
        tv_name.setText(word_pron[position]);
        tv_pron.setText(eng_word[position]);

        if(!note[position].equals("-")) {
            StringBuilder textNote = new StringBuilder();
            String[] a = note[position].split("//");
            for(String word:a) {
                textNote.append("-> ").append(word).append("\n");
            }
            tv_note_details.setText(textNote.toString());
        }
        else{
            tv_note.setVisibility(View.GONE);
            tv_note_details.setVisibility(View.GONE);
        }

        ImageView btn_arrow = convertView.findViewById(R.id.btn_arrow);
        ConstraintLayout cl_intermediate = convertView.findViewById(R.id.cl_intermediate);
        ConstraintLayout constraintLayout = convertView.findViewById(R.id.CL_vocab_grammar);
        cl_intermediate.setOnClickListener(v -> {
            if(up) {
                constraintLayout.setVisibility(View.GONE);
                btn_arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                iv_audio.setVisibility(View.GONE);
                up = false;
            }
            else{
                iv_audio.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                btn_arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                up = true;
            }
        });

        TextView tv_example = convertView.findViewById(R.id.tv_example);

        tv_example.setText("Example "+(position+1));

        iv_audio.setOnClickListener(v -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try{
                mediaPlayer.setDataSource(audio[position]);
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepare();
            }catch (IOException e){
                Toast.makeText(context, "No audio found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
