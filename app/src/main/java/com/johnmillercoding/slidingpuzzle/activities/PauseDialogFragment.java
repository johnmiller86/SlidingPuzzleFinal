package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.johnmillercoding.slidingpuzzle.R;

public class PauseDialogFragment extends DialogFragment{

    // Instance
    private PauseDialogListener pauseDialogListener;
    private boolean isQuitting;
//    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pause_dialog, container, false);
        setCancelable(false);
        Button resumeButton = view.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                pauseDialogListener.unPause();
            }
        });
        Button quitButton = view.findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirm();
            }
        });
//        URL url = null;
//        try {
//            url = new URL(getArguments().getString(PUZZLE_URL_TAG));
//        }catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        imageView = (ImageView) view.findViewById(R.id.pauseImageView);
//        Glide.with(this)
//                .load(url)
//                .into(imageView);
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

            // Auto-prompt for back pressed
            if (isQuitting){
                isQuitting = false;
                confirm();
            }
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
     * Interface listener for the pause button.
     * @param pauseDialogListener the calling Activity's PauseDialogListener.
     */
    public void setPauseDialogListener(PauseDialogListener pauseDialogListener) {
        this.pauseDialogListener = pauseDialogListener;
    }

    /**
     * The user chose to exit rather than pause.
     */
    public void isQuitting(){
        this.isQuitting = true;
    }

    /**
     * Interface to be implemented in calling Activity.
     */
    public interface PauseDialogListener{
        void unPause();
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
