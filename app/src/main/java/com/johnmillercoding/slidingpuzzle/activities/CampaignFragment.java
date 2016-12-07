package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.Level;
import com.johnmillercoding.slidingpuzzle.utilities.Config;
import com.johnmillercoding.slidingpuzzle.utilities.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_COL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_LEVEL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_MOVES_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_RESOURCE_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_ROW_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_TIMER_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;

@SuppressWarnings({"EmptyMethod", "MismatchedQueryAndUpdateOfCollection"})
public class CampaignFragment extends Fragment {

    // UI Components
    private View view;
    private InterstitialAd interstitialAd;

    public CampaignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_campaign, container, false);
        initialize();
        return view;
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
     * Configures the level selection ImageButtons.
     */
    private void initialize(){

        // Foreground image resource ids
        int checkmark = getResources().getIdentifier("checkmark_thumb", "drawable", getActivity().getPackageName());
        int lock = getResources().getIdentifier("lock_thumb", "drawable", getActivity().getPackageName());

        // Referencing
        final ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.level_1);
        ImageButton imageButton2 = (ImageButton) view.findViewById(R.id.level_2);
        ImageButton imageButton3 = (ImageButton) view.findViewById(R.id.level_3);
        ImageButton imageButton4 = (ImageButton) view.findViewById(R.id.level_4);
        ImageButton imageButton5 = (ImageButton) view.findViewById(R.id.level_5);
        ImageButton imageButton6 = (ImageButton) view.findViewById(R.id.level_6);
        ImageButton imageButton7 = (ImageButton) view.findViewById(R.id.level_7);
        ImageButton imageButton8 = (ImageButton) view.findViewById(R.id.level_8);
        ImageButton imageButton9 = (ImageButton) view.findViewById(R.id.level_9);
        ImageButton imageButton10 = (ImageButton) view.findViewById(R.id.level_10);
        ImageButton imageButton11 = (ImageButton) view.findViewById(R.id.level_11);
        ImageButton imageButton12 = (ImageButton) view.findViewById(R.id.level_12);
        ImageButton imageButton13 = (ImageButton) view.findViewById(R.id.level_13);
        ImageButton imageButton14 = (ImageButton) view.findViewById(R.id.level_14);
        ImageButton imageButton15 = (ImageButton) view.findViewById(R.id.level_15);
        ImageButton imageButton16 = (ImageButton) view.findViewById(R.id.level_16);
        ImageButton imageButton17 = (ImageButton) view.findViewById(R.id.level_17);
        ImageButton imageButton18 = (ImageButton) view.findViewById(R.id.level_18);
        ImageButton imageButton19 = (ImageButton) view.findViewById(R.id.level_19);
        ImageButton imageButton20 = (ImageButton) view.findViewById(R.id.level_20);

        // Adding to list
        final List<ImageButton> imageButtons = new ArrayList<>();
        imageButtons.add(imageButton1);
        imageButtons.add(imageButton2);
        imageButtons.add(imageButton3);
        imageButtons.add(imageButton4);
        imageButtons.add(imageButton5);
        imageButtons.add(imageButton6);
        imageButtons.add(imageButton7);
        imageButtons.add(imageButton8);
        imageButtons.add(imageButton9);
        imageButtons.add(imageButton10);
        imageButtons.add(imageButton11);
        imageButtons.add(imageButton12);
        imageButtons.add(imageButton13);
        imageButtons.add(imageButton14);
        imageButtons.add(imageButton15);
        imageButtons.add(imageButton16);
        imageButtons.add(imageButton17);
        imageButtons.add(imageButton18);
        imageButtons.add(imageButton19);
        imageButtons.add(imageButton20);

        // Configure foreground images async
        for (int i = 0; i < 20; i++) {

            // Add listener
            imageButtons.get(i).setOnClickListener(imagesListener);

            // Set completed or locked
            if (i < sessionManager.getUnlocked()) {
                Glide.with(this)
                        .load(checkmark)
                        .into(imageButtons.get(i));
            } else if (i > sessionManager.getUnlocked()) {
                Glide.with(this)
                        .load(lock)
                        .into(imageButtons.get(i));
                imageButtons.get(i).setEnabled(false);
            }
        }

        // Ad stuff
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
        requestNewInterstitial();
    }

    /**
     * Click listener for ImageButtons.
     */
    private final View.OnClickListener imagesListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            String resName = getResources().getResourceEntryName(view.getId());
            int levelNum = Integer.valueOf(String.valueOf(resName).replaceAll("\\D+", ""));
            getLevel(levelNum, resName);
        }
    };

    private void startGame(Level level){
        Intent campaign = new Intent(getActivity(), PuzzleActivity.class);
        campaign.putExtra(PUZZLE_RESOURCE_TAG, level.getUrl());
        campaign.putExtra(PUZZLE_TIMER_TAG, level.getTimeLimit());
        campaign.putExtra(PUZZLE_LEVEL_TAG, level.getLevelNum());
        campaign.putExtra(PUZZLE_COL_TAG, level.getColumns());
        campaign.putExtra(PUZZLE_ROW_TAG, level.getRows());
        campaign.putExtra(PUZZLE_MOVES_TAG, level.getMoveLimit());
        startActivityForResult(campaign, 0);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0) && (resultCode == Activity.RESULT_OK)) {

            // Refresh fragment after level beaten
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .detach(MainActivity.fragment)
                    .attach(MainActivity.fragment)
                    .commitAllowingStateLoss();
        }

        // Display an ad // TODO make sure this is only every 5 minutes or so
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }
    }

    /**
     * Gets the requested level.
     */
    private void getLevel(final int levelNum, final String resName) {
        String requestString = "get_level";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_LEVEL, new Response.Listener<String>() {
        final Level level = new Level(levelNum);
            @Override
            public void onResponse(String response) {

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        level.setLevelNum(levelNum);
                        level.setColumns(jsonObject.getInt("columns"));
                        level.setRows(jsonObject.getInt("rows"));
                        level.setTimeLimit(jsonObject.getInt("time_limit"));
                        level.setMoveLimit(jsonObject.getInt("move_limit"));
                        level.setUrl(resName);
//                        level.setUrl(jsonObject.getString("path"));  // TODO Mess with this
                        startGame(level);
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("level_num", String.valueOf(levelNum));
                return params;
            }
        };
        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }

    /**
     * Requests an ad for displaying.
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // Emulators
                .addTestDevice("91D6373C67AB407D90746EAF75E82B1A") // S7 Edge
                .build();
        interstitialAd.loadAd(adRequest);
    }
}
