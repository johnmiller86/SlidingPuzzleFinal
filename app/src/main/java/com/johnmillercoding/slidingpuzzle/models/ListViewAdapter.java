package com.johnmillercoding.slidingpuzzle.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.johnmillercoding.slidingpuzzle.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter{

    // Constants
    public static final String LEVEL="level";
    public static final String EMAIL="email";
    public static final String SCORE="score";
    public static final String MOVES="moves";
    public static final String TIME="TIME";

    private final ArrayList<HashMap<String, String>> list;
    private final LayoutInflater layoutInflater;
    private TextView level, email, score, moves;
    private TextView time;

    public ListViewAdapter(LayoutInflater layoutInflater, ArrayList<HashMap<String, String>> list){
        super();
        this.list=list;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {

//        LayoutInflater inflater = MainActivity.fragment.getLayoutInflater(null);

        if(view == null){

            view = layoutInflater.inflate(R.layout.list_view_row, parent, false);

            level=(TextView) view.findViewById(R.id.levelTV);
            email=(TextView) view.findViewById(R.id.email);
            score=(TextView) view.findViewById(R.id.score);
            moves=(TextView) view.findViewById(R.id.moves);
            time=(TextView) view.findViewById(R.id.timeLV);

        }

        HashMap<String, String> map=list.get(position);
        level.setText(map.get(LEVEL));
        email.setText(map.get(EMAIL));
        score.setText(map.get(SCORE));
        moves.setText(map.get(MOVES));
        time.setText(map.get(TIME));

        return view;
    }

}