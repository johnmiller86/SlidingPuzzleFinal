package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.johnmillercoding.slidingpuzzle.R;

public class LevelCompleteDialogFragment extends DialogFragment{

    // Instance
    private LevelCompleteDialogListener levelCompleteDialogListener;
    private Button replayButton;
    private  View view;
    private int score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_level_complete_dialog, container, false);
        setCancelable(false);
//        textView = (TextView) view.findViewById(R.id.scoreTextView);
        score = getArguments().getInt("score");
        replayButton = (Button) view.findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                levelCompleteDialogListener.replay();
            }
        });
        Button nextLevelButton = (Button) view.findViewById(R.id.nextLevelButton);
        nextLevelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                levelCompleteDialogListener.nextLevel();
            }
        });
        Button mainMenuButton = (Button) view.findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                getActivity().finish();
            }
        });
        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            // Configure dialog
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xCC505050));
            TextView textView = (TextView) view.findViewById(R.id.scoreTextView);
//            textView.setText(score);
        }
    }

    /**
     * Interface listener for the level complete dialog.
     * @param levelCompleteDialogListener the calling Activity's PauseDialogListener.
     */
    public void setLevelCompleteDialogListener(LevelCompleteDialogListener levelCompleteDialogListener) {
        this.levelCompleteDialogListener = levelCompleteDialogListener;
    }

    /**
     * Interface to be implemented in calling Activity.
     */
    public interface LevelCompleteDialogListener{
        void replay();
        void nextLevel();
    }
}
