package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.utilities.Config;
import com.johnmillercoding.slidingpuzzle.utilities.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_COL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_LEVEL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_MODE_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_ROW_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_TIMER_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;

@SuppressWarnings("EmptyMethod")
public class CampaignFragment extends Fragment {

    // UI Components
    private View view;
    private List<RelativeLayout> relativeLayouts;
    private List<ImageButton> imageButtons;

    private int unlocked;
    private ArrayList<int[]> rowCols;

    public CampaignFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_campaign, container, false);
        initialize();
//        createLayout();
//        configureLayouts();
        return getOpenLevels(view);
    }

    private void initialize() {

        // Configuring session
//        SessionManager sessionManager = new SessionManager(view.getContext());
//        UserFunctions userFunctions = new UserFunctions();
//        LeaderboardFunctions leaderboardFunctions = new LeaderboardFunctions();
//        User user = userFunctions.getUser(sessionManager.getEmail());
//        SettingFunctions settingFunctions = new SettingFunctions();
//        Settings settings = settingFunctions.getSettings(getContext(), sessionManager.getEmail());
////        PuzzleFunctions puzzleFunctions = new PuzzleFunctions();
////        Puzzle puzzle = puzzleFunctions.getPuzzle(user);
        relativeLayouts = new ArrayList<>();
        imageButtons = new ArrayList<>();
        unlocked = 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void createLayout() {

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.campaign_tablelayout);
        TableRow tableRow = new TableRow(getContext());
        RelativeLayout outerLayout, innerLayout;
        TableRow.LayoutParams layoutParams;
        RelativeLayout.LayoutParams textViewParams;
        TableRow.LayoutParams imageButtonParams;
        ImageButton imageButton;
        TextView textView;

        for (int i = 0; i < 20; i++) {

            // Outer Layout
            outerLayout = new RelativeLayout(getContext());
            layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
            layoutParams.setMargins(5, 5, 5, 5);
            outerLayout.setLayoutParams(layoutParams);

            // ImageButton
            imageButton = new ImageButton(getActivity());
            imageButtonParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            imageButton.setLayoutParams(imageButtonParams);
            imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            imageButton.setOnClickListener(imagesListener);
            String levelNum = "level_" + (i + 1);
            imageButton.setTag(levelNum);
            imageButton.setId(i + 1);
            loadImages(imageButton, i, levelNum);
            imageButtons.add(imageButton);

            // Inner Layout
            innerLayout = new RelativeLayout(getContext());
            layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            innerLayout.setLayoutParams(layoutParams);

            // TextView
            textView = new TextView(getContext());
            textViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            textView.setLayoutParams(textViewParams);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText(getString(R.string.level_num, i + 1));
            textView.setTextSize(18);
            textView.setBackgroundResource(R.drawable.gradient);
            textView.setTypeface(Typeface.create("serif", Typeface.BOLD));
            textView.setAlpha(.5f);

            // Adding
            innerLayout.addView(textView);
            outerLayout.addView(imageButton);
            outerLayout.addView(innerLayout);
            tableRow.addView(outerLayout);
            relativeLayouts.add(outerLayout);

            // Close table row
            if (i > 0 && (i + 1) % 3 == 0) {
                tableLayout.addView(tableRow);
                tableRow = new TableRow(getContext());
            } else if (i == 19) {
                tableLayout.addView(tableRow);
            }
        }
    }

    private void loadImages(ImageButton imageButton, int i, String levelNum) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        if (i < unlocked) {
            loadAsync(getResources().getIdentifier("checkmark", "drawable", getActivity().getPackageName()), imageButton, getActivity().getDrawable(getResources().getIdentifier(levelNum, "drawable", getActivity().getPackageName())));
        } else if (i == unlocked) {
            loadAsync(getResources().getIdentifier("glass", "drawable", getActivity().getPackageName()), imageButton, getActivity().getDrawable(getResources().getIdentifier(levelNum, "drawable", getActivity().getPackageName())));
        } else {
            loadAsync(getResources().getIdentifier("lock", "drawable", getActivity().getPackageName()), imageButton, getActivity().getDrawable(getResources().getIdentifier(levelNum, "drawable", getActivity().getPackageName())));
            imageButton.setEnabled(false);
        }
    }

    /**
     * Configures the sizing of the nested layouts.
     */
    private void configureLayouts() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        for (RelativeLayout relativeLayout : relativeLayouts) {
            int ROWS = 7;
            int COLS = 3;
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                relativeLayout.getLayoutParams().width = displayMetrics.widthPixels / COLS;
                relativeLayout.getLayoutParams().height = displayMetrics.heightPixels / ROWS * 2;
            } else {
                relativeLayout.getLayoutParams().width = displayMetrics.widthPixels / COLS;
                relativeLayout.getLayoutParams().height = displayMetrics.heightPixels / ROWS;
            }
            relativeLayout.requestLayout();
        }
    }

    /**
     * Click listener for ImageButtons.
     */
    private final View.OnClickListener imagesListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent campaign = new Intent(getActivity(), PuzzleActivity.class);
            campaign.putExtra(PUZZLE_MODE_TAG, (String) view.getTag());
            // TODO different level times
            campaign.putExtra(PUZZLE_TIMER_TAG, 10 * view.getId());
            campaign.putExtra(PUZZLE_LEVEL_TAG, view.getId());
            fillRowCols();
            campaign.putExtra(PUZZLE_COL_TAG, rowCols.get(view.getId() - 1)[0]);
            campaign.putExtra(PUZZLE_ROW_TAG, rowCols.get(view.getId() - 1)[1]);
            startActivityForResult(campaign, 0);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

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
    }

    // Temp level configure
    private void fillRowCols() {
        rowCols = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            rowCols.add(new int[]{i, i});
            rowCols.add(new int[]{i + 1, i});
            rowCols.add(new int[]{i, i + 1});
        }
    }

    /**
     * Loads an image asynchronously.
     *
     * @param resId     the resource id.
     * @param imageView the receiving ImageView.
     * @param drawable  the applying Drawable.
     */
    private void loadAsync(int resId, ImageView imageView, Drawable drawable) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, drawable);
        task.execute(resId);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    // TODO Clean up this async task

    /**
     * Inner class for asynchronous image loading.
     */
    public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private final Drawable drawable;

        BitmapWorkerTask(ImageView imageView, Drawable drawable) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            this.drawable = drawable;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setBackground(drawable);
                }
            }
        }

        int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }
    }

    /**
     * Saves the user's settings in MySQL.
     */
    public View getOpenLevels(View view) {
        String requestString = "get_unlocked";

//        final ProgressDialog progressDialog = new ProgressDialog(activity);
//        progressDialog.setMessage("Updating leaderboards...");
//        progressDialog.show();
        int unlck;

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_UNLOCKED, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Count: " + jsonObject.getInt("count"), Toast.LENGTH_LONG);
                        unlocked = jsonObject.getInt("count");
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        createLayout();
                        configureLayouts();

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
//                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", sessionManager.getEmail());
                return params;
            }
        };
        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
        return view;
    }
}
