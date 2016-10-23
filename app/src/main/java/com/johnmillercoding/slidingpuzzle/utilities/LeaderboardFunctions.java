package com.johnmillercoding.slidingpuzzle.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.models.LeaderboardEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Class to handle all puzzle DB functions.
 * @author John D. Miller.
 * @version 1.0.1
 * @since 09/10/2016
 */
public class LeaderboardFunctions {

//    /**
//     * Gets the user's settings from MySQL.
//     */
//    public ArrayList<LeaderboardEntry> getLeaderboards(final Context context, ListViewAdapter listViewAdapter) {
//        String requestString = "get_leaderboards";
//
//        final ArrayList<LeaderboardEntry> leaderboards = new ArrayList<>();
//        final ProgressDialog progressDialog = new ProgressDialog(context);
////        progressDialog.setMessage("Retrieving settings...");
////        progressDialog.show();
//        Toast.makeText(context, "Retrieving leaderboards...", Toast.LENGTH_SHORT).show();
//        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_LEADERBOARDS, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
////                progressDialog.dismiss();
//
//                try {
//
//                    // Retrieve JSON response object
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean error = jsonObject.getBoolean("error");
//
//                    // Retrieve JSON response array
//                    JSONArray jsonArray = jsonObject.getJSONArray("response");
//
//                    // Check for error node in json
//                    if (!error) {
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            // Retrieve inner JSON objects
//                            JSONObject row = jsonArray.getJSONObject(i);
//
//                            // Configure entry and add to list
//                            LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
//                            leaderboardEntry.setEmail(row.getString("email"));
//                            leaderboardEntry.setLevel_num(row.getInt("level_num"));
//                            leaderboardEntry.setMoves(row.getInt("moves"));
//                            leaderboardEntry.setScore(row.getInt("score"));
//                            leaderboardEntry.setTime(row.getString("time"));
//                            leaderboards.add(leaderboardEntry);
//                        }
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
//                return params;
//            }
//        };
//
//        // Adding request to request queue
//        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
//        return leaderboards;
//    }

    /**
     * Saves the user's settings in MySQL.
     */
    public void updateLeaderboards(final Activity activity, final LeaderboardEntry leaderboardEntry) {
        String requestString = "update_leaderboards";

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating leaderboards...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_UPDATE_LEADERBOARDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Pow right in the kisser!!",Toast.LENGTH_LONG);
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", leaderboardEntry.getEmail());
                params.put("level_num", String.valueOf(leaderboardEntry.getLevel_num()));
                params.put("score", String.valueOf(leaderboardEntry.getScore()));
                params.put("moves", String.valueOf(leaderboardEntry.getMoves()));
                params.put("time", leaderboardEntry.getTime());
                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }
}