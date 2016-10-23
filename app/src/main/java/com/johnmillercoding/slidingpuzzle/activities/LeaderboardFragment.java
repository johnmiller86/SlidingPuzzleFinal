package com.johnmillercoding.slidingpuzzle.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.LeaderboardEntry;
import com.johnmillercoding.slidingpuzzle.models.ListViewAdapter;
import com.johnmillercoding.slidingpuzzle.utilities.Config;
import com.johnmillercoding.slidingpuzzle.utilities.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.johnmillercoding.slidingpuzzle.models.ListViewAdapter.EMAIL;
import static com.johnmillercoding.slidingpuzzle.models.ListViewAdapter.LEVEL;
import static com.johnmillercoding.slidingpuzzle.models.ListViewAdapter.MOVES;
import static com.johnmillercoding.slidingpuzzle.models.ListViewAdapter.SCORE;
import static com.johnmillercoding.slidingpuzzle.models.ListViewAdapter.TIME;


public class LeaderboardFragment extends Fragment {

    private View view;
    private ArrayList<HashMap<String, String>> list;
    private ListView listView;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        listView = (ListView) view.findViewById(R.id.listView1);
        list=new ArrayList<>();
//
////        LeaderboardFunctions leaderboardFunctions = new LeaderboardFunctions();
////        ArrayList<LeaderboardEntry> leaderboards = leaderboardFunctions.getLeaderboards(getContext());
//
//        for (LeaderboardEntry leaderboardEntry : leaderboards){
//            HashMap<String, String> hashMap = new HashMap<>();
//            hashMap.put(FIRST_COLUMN, leaderboardEntry.getEmail());
//            hashMap.put(SECOND_COLUMN, String.valueOf(leaderboardEntry.getScore()));
//            hashMap.put(THIRD_COLUMN, String.valueOf(leaderboardEntry.getMoves()));
//            hashMap.put(FOURTH_COLUMN, leaderboardEntry.getTime());
//            hashMap.put(FIFTH_COLUMN, String.valueOf(leaderboardEntry.getLevel_num()));
//            list.add(hashMap);
//        }
//
//        ListViewAdapter adapter = new ListViewAdapter(getLayoutInflater(savedInstanceState), list);
//        listView.setAdapter(adapter);
        return getLeaderboards(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Gets the user's settings from MySQL.
     */
    public View getLeaderboards(View view, final Bundle savedInstanceState) {
        String requestString = "get_leaderboards";

        final ArrayList<LeaderboardEntry> leaderboards = new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Retrieving settings...");
//        progressDialog.show();
        Toast.makeText(getContext(), "Retrieving leaderboards...", Toast.LENGTH_SHORT).show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_LEADERBOARDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();

                try {

                    // Retrieve JSON response object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Retrieve JSON response array
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            // Retrieve inner JSON objects
                            JSONObject row = jsonArray.getJSONObject(i);

//                            // Configure entry and add to list
//                            LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
//                            leaderboardEntry.setEmail(row.getString("email"));
//                            leaderboardEntry.setLevel_num(row.getInt("level_num"));
//                            leaderboardEntry.setMoves(row.getInt("moves"));
//                            leaderboardEntry.setScore(row.getInt("score"));
//                            leaderboardEntry.setTime(row.getString("time"));
//                            leaderboards.add(leaderboardEntry);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(LEVEL, String.valueOf(row.getInt("level_num")));
                            hashMap.put(EMAIL, row.getString("email"));
                            hashMap.put(SCORE, String.valueOf(row.getInt("score")));
                            hashMap.put(MOVES, String.valueOf(row.getInt("moves")));
                            hashMap.put(TIME, row.getString("time"));
                            list.add(hashMap);
                        }
                        ListViewAdapter adapter = new ListViewAdapter(getLayoutInflater(savedInstanceState), list);
                        listView.setAdapter(adapter);
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
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
        return view;
    }
}
