package com.johnmillercoding.slidingpuzzle.models;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.johnmillercoding.slidingpuzzle.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ListViewAdapter extends BaseAdapter{

    // Constants
    public static final String LEVEL="level";
    public static final String EMAIL="email";
    public static final String SCORE="score";
    public static final String MOVES="moves";
    public static final String TIME="time";

    // Instance vars
    private final ArrayList<HashMap<String, String>> list;
    private final Context context;

    // Constructor
    public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> list){
        super();
        this.list=list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = Objects.requireNonNull(layoutInflater).inflate(R.layout.list_view_row, parent, false);
        }
        TextView level = view.findViewById(R.id.levelTV);
        TextView email = view.findViewById(R.id.email);
        TextView score = view.findViewById(R.id.score);
        TextView moves = view.findViewById(R.id.moves);
        TextView time = view.findViewById(R.id.timeLV);

        HashMap<String, String> map=list.get(position);
        level.setText(map.get(LEVEL));
        email.setText(map.get(EMAIL));
        email.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        email.setSingleLine(true);
        email.setMarqueeRepeatLimit(-1);
        email.setSelected(true);
        score.setText(map.get(SCORE));
        moves.setText(map.get(MOVES));
        time.setText(map.get(TIME));
        return view;
    }
}