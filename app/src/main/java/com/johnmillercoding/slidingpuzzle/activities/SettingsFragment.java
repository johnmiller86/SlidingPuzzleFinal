package com.johnmillercoding.slidingpuzzle.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.johnmillercoding.slidingpuzzle.R;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.johnmillercoding.slidingpuzzle.R.string.settings;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.puzzleFunctions;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.settingFunctions;
//import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.settings;

@SuppressWarnings("EmptyMethod")
public class SettingsFragment extends Fragment {

    // Request tags
    private final int REQUEST_EXTERNAL_STORAGE_CAMERA = 1;
    private final int REQUEST_EXTERNAL_STORAGE_GALLERY = 2;
    private final int REQUEST_CAMERA = 3;
    private final int SELECT_IMAGE = 4;

    // UI components
    private View view;
    private ImageView imageView;
    private Button rowsButton, colsButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialize();
        return view;
    }
//
//    @Override
//    public void onAttach(Context context) {  // TODO Look for leaks and fix
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
////        if (changed){
////            settingFunctions.saveSettings(getActivity(), sessionManager.getEmail(), settings);
////        }
//    }

    private void initialize() {


        // UI components
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(puzzleFunctions.getPuzzle(Objects.requireNonNull(getActivity()).getBaseContext()));
        Button imagePicker = view.findViewById(R.id.button_pick_puzzle);
        imagePicker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                chooseImage();
            }
        });
        rowsButton = view.findViewById(R.id.button_pick_rows);
        colsButton = view.findViewById(R.id.button_pick_columns);
        rowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberPicker("rows");
            }
        });
        colsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberPicker("columns");
            }
        });
        rowsButton.setText(String.valueOf(sessionManager.getRows()));
        colsButton.setText(String.valueOf(sessionManager.getCols()));
    }

    /**
     * Opens an alert dialog to choose image source.
     */
    private void chooseImage() {
        final CharSequence[] charSequences = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Select a Photo");
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (charSequences[item].equals("Take Photo")) {

                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CAMERA);
                }
                else if (charSequences[item].equals("Choose from Library")) {

                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_GALLERY);
                }
                else if (charSequences[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Handles the image response.
     * @param requestCode the requesting intent.
     * @param resultCode the status of the result.
     * @param data the image data.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Camera Request
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA){

                // Retrieve captured photo
                Uri imageUri = data.getData();
                sessionManager.setPuzzlePath(getImagePath(Objects.requireNonNull(getContext()), imageUri));

                // Update ImageView
                Bitmap bitmap = puzzleFunctions.getPuzzle(getContext());
                imageView.setImageBitmap(bitmap);

                // Update MySQL
                settingFunctions.saveSettings(getActivity());

            }

            // Gallery Request
            else if (requestCode == SELECT_IMAGE) {

                // Retrieve selected image
                Uri imageUri = data.getData();
                sessionManager.setPuzzlePath(getImagePath(Objects.requireNonNull(getContext()), imageUri));

                // Update ImageView
                Bitmap bitmap = puzzleFunctions.getPuzzle(getContext());
                imageView.setImageBitmap(bitmap);

                // Update MySQL
                settingFunctions.saveSettings(getActivity());
            }
        }
    }

    /**
     * Gets the full filepath of the uri.
     * @param context the context.
     * @param contentUri the uri.
     * @return the path.
     */
    private String getImagePath(Context context, Uri contentUri) {

        String[] imageData = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, imageData, null, null, null);
        assert cursor != null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    /**
     * Handles operations based on permission results.
     * @param requestCode the request code.
     * @param permissions the result code.
     * @param grantResults the grant results array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        switch (requestCode){

            // Camera
            case REQUEST_EXTERNAL_STORAGE_CAMERA: {
                // Granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
                // Blocked
                else if (!ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                            .setTitle("Permission was blocked!")
                            .setMessage("You have previously blocked this app from accessing external storage. To set a free play puzzleFunctions, the app needs to " +
                                    "retrieve image paths and will not function without this access. Would you like to go to settings and allow this permission?")

                            // Open Settings button
                            .setPositiveButton(settings, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSettings();
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                // Denied
                else{
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                            .setTitle("Permission was denied!")
                            .setMessage("You are unable to set a free play puzzleFunctions without access to external storage. Would you like to allow access?")

                            // Open Settings button
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CAMERA);
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return;
            }
            case REQUEST_EXTERNAL_STORAGE_GALLERY:{
                // Granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_IMAGE);
                }
                // Blocked
                else if(!ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                            .setTitle("Permission was blocked!")
                            .setMessage("You have previously blocked this app from accessing external storage. To set a free play puzzleFunctions, the app needs to " +
                                    "retrieve image paths and will not function without this access. Would you like to go to settings and allow this permission?")

                            // Open Settings button
                            .setPositiveButton(settings, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSettings();
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                // Denied
                else {
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                            .setTitle("Permission was denied!")
                            .setMessage("You are unable to set a free play puzzleFunctions without access to external storage. Would you like to allow access?")

                            // Open Settings button
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_GALLERY);
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
    }

    /**
     * Opens the app's settings page in AppManager.
     */
    private void goToSettings(){
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        int REQUEST_PERMISSION = 0;
        startActivityForResult(intent, REQUEST_PERMISSION);
    }

    /**
     * Shows the number picker to change rows or columns.
     * @param which rows or columns.
     */
    private void showNumberPicker(final String which){

        // Lock the orientation to preserve dialog and the async task
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        // Create the layout
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        final NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMaxValue(8);
        numberPicker.setMinValue(2);
        if (which.equals("rows")) {
            numberPicker.setValue(sessionManager.getRows());
        }else{
            numberPicker.setValue(sessionManager.getCols());
        }
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        relativeLayout.setLayoutParams(params);
        relativeLayout.addView(numberPicker,layoutParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setTitle("Select number of " + which);
        alertDialogBuilder.setView(relativeLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (which.equals("rows")){
                                    sessionManager.setRows(numberPicker.getValue());
                                    rowsButton.setText(String.valueOf(sessionManager.getRows()));
                                }else{
                                    sessionManager.setCols(numberPicker.getValue());
                                    colsButton.setText(String.valueOf(sessionManager.getCols()));
                                }
                                settingFunctions.saveSettings(getActivity());

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
