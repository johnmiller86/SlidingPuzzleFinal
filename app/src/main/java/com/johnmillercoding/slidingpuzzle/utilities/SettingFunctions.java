package com.johnmillercoding.slidingpuzzle.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.models.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;

/**
 * Class to handle all puzzleFunctions DB functions.
 * @author John D. Miller.
 * @version 1.0.1
 * @since 09/10/2016
 */
public class SettingFunctions {

    /**
     * Gets the user's settings from MySQL.
     */
    public Settings getSettings(final Context context, final String email) {
        String requestString = "get_settings";

        final Settings settings = new Settings();
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Retrieving settings...");
//        progressDialog.show();
//        Toast.makeText(context, "Retrieving settings...", Toast.LENGTH_SHORT).show();

        //StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_SETTINGS, new Response.Listener<String>() {
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_SETTINGS, new Response.Listener<String>() {


                @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Configuring settings
                        settings.setRows(jsonObject.getInt("rows"));
                        settings.setColumns(jsonObject.getInt("columns"));
                        sessionManager.setPuzzlePath(jsonObject.getString("puzzle_path"));
                    } else {
                        // Error fetching data. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
                // JSON error
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
        return settings;
    }

//    /**
//     * Authenticates against the mysql database.
//     */
//    public Settings getSettings(final Context context, final String email, final NumberPicker numberPickerCols, final NumberPicker numberPickerRows, final ImageView imageView) {
//        String requestString = "get_settings";
//
//        final Settings settings = new Settings();
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Retrieving settings...");
//        progressDialog.show();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_SETTINGS, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                progressDialog.dismiss();
//
//                try {
//
//                    // Retrieve JSON error object
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean error = jsonObject.getBoolean("error");
//
//                    // Check for error node in json
//                    if (!error) {
//                        // Configuring settings
//                        settings.setRows(jsonObject.getInt("rows"));
//                        settings.setColumns(jsonObject.getInt("columns"));
//                        sessionManager.setPuzzlePath(jsonObject.getString("puzzle_path"));
//                        numberPickerCols.setValue(settings.getColumns());
//                        numberPickerRows.setValue(settings.getRows());
//                        if(sessionManager.getPuzzlePath() != null) {
//                            PuzzleFunctions puzzleFunctions = new PuzzleFunctions();
//                            imageView.setImageBitmap(puzzleFunctions.getPuzzle(context, settings));
//                        }
//
//                    } else {
//                        // Error fetching data. Get the error message
//                        String errorMsg = jsonObject.getString("error_msg");
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                }
//                // JSON error
//                catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
//        return settings;
//    }

    /**
     * Saves the user's settings in MySQL.
     */
    public void saveSettings(final Activity activity, final String email, final Settings settings) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        String requestString = "save_settings";

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_SAVE_SETTINGS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    if (!error) {


                        Toast.makeText(getApplicationContext(), "Settings saved!",Toast.LENGTH_LONG);
                    } else {
                        // Error fetching data. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
                // JSON error
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("rows", String.valueOf(settings.getRows()));
                params.put("columns", String.valueOf(settings.getColumns()));
                params.put("puzzle_path", sessionManager.getPuzzlePath());
                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }
}