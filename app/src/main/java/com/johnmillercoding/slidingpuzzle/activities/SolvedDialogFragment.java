package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.Level;

import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_COL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_LEVEL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_MOVES_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_RESOURCE_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_ROW_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_TIMER_TAG;

public class SolvedDialogFragment extends DialogFragment{

    private SolvedDialogListener solvedDialogListener;
    private Intent nextLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solved_dialog, container, false);
        setCancelable(false);
        Button resumeButton = (Button) view.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
//                solvedDialogListener.unPause();
            }
        });
        Button quitButton = (Button) view.findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        return view;
    }

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
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                confirm();
            }
        };
    }

    /**
     * 
     * @param solvedDialogListener the calling Activity's PauseDialogListener.
     */
    public void setPauseDialogListener(SolvedDialogListener solvedDialogListener) {
        this.solvedDialogListener = solvedDialogListener;
    }

    /**
     * Interface to be implemented in calling Activity.
     */
    public interface SolvedDialogListener{
        void replay();
    }
    
    public void configNextLevel(Level level){
        
        if (level.getLevelNum() != 20) {
            nextLevel = new Intent(getActivity(), PuzzleActivity.class);
            nextLevel.putExtra(PUZZLE_RESOURCE_TAG, level.getUrl());
            nextLevel.putExtra(PUZZLE_TIMER_TAG, level.getTimeLimit());
            nextLevel.putExtra(PUZZLE_LEVEL_TAG, level.getLevelNum());
            nextLevel.putExtra(PUZZLE_COL_TAG, level.getColumns());
            nextLevel.putExtra(PUZZLE_ROW_TAG, level.getRows());
            nextLevel.putExtra(PUZZLE_MOVES_TAG, level.getMoveLimit());
        }
    }

    private void nextLevel(){
        startActivityForResult(nextLevel, 0);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        getActivity().finish();
    }

    /**
     * Opens an AlertDialog to confirm user intended to exit.
     */
    private void confirm() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Exit")
                .setMessage("Are you sure you want to quit?")

                // Finish Activity
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                })

                // Cancel
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }
}
