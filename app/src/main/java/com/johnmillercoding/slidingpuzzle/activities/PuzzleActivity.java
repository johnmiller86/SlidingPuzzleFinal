package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.LeaderboardEntry;
import com.johnmillercoding.slidingpuzzle.models.Level;
import com.johnmillercoding.slidingpuzzle.utilities.Config;
import com.johnmillercoding.slidingpuzzle.utilities.NetworkReceiver;
import com.johnmillercoding.slidingpuzzle.utilities.PuzzleFunctions;
import com.johnmillercoding.slidingpuzzle.utilities.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_COL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_LEVEL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_MOVES_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_ROW_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_TIMER_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_URL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.leaderboardFunctions;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;
import static com.johnmillercoding.slidingpuzzle.models.Level.NUM_LEVELS;

public class PuzzleActivity extends AppCompatActivity implements NetworkReceiver.NetworkStateReceiverListener, PauseDialogFragment.PauseDialogListener, LevelCompleteDialogFragment.LevelCompleteDialogListener{

    // UI components
    private TableLayout tableLayout;
    private TextView movesTextView, timerTextView;
    private ImageButton previousButton;
    private Button pauseButton;
    private PauseDialogFragment pauseDialogFragment;
    private LevelCompleteDialogFragment levelCompleteDialogFragment;

    // Lists
    private List<ImageButton> imageButtons;
    private List<Drawable> answerKey;

    // Vars
    private Timer timer;
    private CountDownTimer countDownTimer;
    private Animation currentAnimation, previousAnimation;
    private int counter, movesCounter, rows, cols, startTime, currentTime, score, levelNum, movesLimit, movesRemaining;
    private boolean isPause, isCampaign;

    // Network
    private NetworkReceiver networkReceiver;
    private boolean isInFocus, connected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        initializeReferences();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cancelTimers();
        try{
            unregisterReceiver(networkReceiver);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (!isPause && !isSolved() && currentTime != 0 && isCampaign){
            pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isSolved() && isCampaign) {
            pauseDialogFragment.isQuitting();
            pause();
        }else if (isSolved() && isCampaign) {
            setResult(Activity.RESULT_OK);
            finish();
        }else{
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureButtons();
    }

    /**
     * Initializes all references.
     */
    private void initializeReferences() {

        // Network stuff
        isInFocus = true;
        networkReceiver = new NetworkReceiver();
        networkReceiver.addListener(this);
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Pause Dialog
        pauseDialogFragment = new PauseDialogFragment ();
        pauseDialogFragment.setPauseDialogListener(this);

        // Level Complete Dialog
        levelCompleteDialogFragment = new LevelCompleteDialogFragment();
        levelCompleteDialogFragment.setLevelCompleteDialogListener(this);

        // Initializing Layout
        tableLayout = findViewById(R.id.table_layout);

        // Initializing TextViews
        movesTextView = findViewById(R.id.currentMoves);
        timerTextView = findViewById(R.id.editTextTimer);

        // Initializing Lists
        imageButtons = new ArrayList<>();
        answerKey = new ArrayList<>();

        // Campaign, set size
        if (getIntent().getStringExtra(PUZZLE_URL_TAG) != null){
            rows = getIntent().getIntExtra(PUZZLE_ROW_TAG, 4);
            cols = getIntent().getIntExtra(PUZZLE_COL_TAG, 3);
        }

        // Free play use user settings if available
        else if (getIntent().getBooleanExtra("offline", false)) {

            rows = 4;
            cols = 3;
        }else{
            rows = sessionManager.getRows();
            cols = sessionManager.getCols();
        }

        // Initializing ImageButtons and adding to list
        createBoard();

        configureButtons();

        // Initializing pause and reset buttons
        pauseButton = findViewById(R.id.button_pause);
        Button resetButton = findViewById(R.id.button_reset);

        // Add listeners
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pause();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                restart();
            }
        });

        // Initializing Counters
        counter = 0;
        movesCounter = 0;
        isPause = false;

        // Create puzzleFunctions
        Intent intent = getIntent();

        // Campaign
        if (intent.getStringExtra(PUZZLE_URL_TAG) != null){
            isCampaign = true;
            startTime = intent.getIntExtra(PUZZLE_TIMER_TAG, 0);
            levelNum = intent.getIntExtra(PUZZLE_LEVEL_TAG, 1);
            movesLimit = intent.getIntExtra(PUZZLE_MOVES_TAG, 0);
            movesRemaining = movesLimit;
            movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesRemaining, movesRemaining));

            // Create bitmap
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                URL url = new URL(intent.getStringExtra(PUZZLE_URL_TAG));
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                createPuzzle(bitmap);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        // Free play
        else {
            isCampaign = false;
            startTime = 0;

            // No network use a random puzzle
            if (intent.getBooleanExtra("offline", false)) {
                createPuzzle(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(randomLevel(), "drawable", getPackageName())));
                Toast.makeText(this, "Connect to WiFi/Mobile to play full game with high res images!", Toast.LENGTH_LONG).show();
            }

            // Use free play settings
            else {

                // Free play puzzleFunctions set, but deleted or on another device
                if (pathValid() && !(new File(sessionManager.getPuzzlePath()).exists())) {
                    Toast.makeText(this, "Image deleted or you are on another device!!", Toast.LENGTH_SHORT).show();
                    createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
                }

                // Use the free play puzzleFunctions
                else if (pathValid() && new File(sessionManager.getPuzzlePath()).exists()) {
                    Bitmap bitmap = new PuzzleFunctions().getPuzzle(this);

                    // User has reinstalled and read permissions not yet enabled
                    if (bitmap == null) {
                        Toast.makeText(this, "You must enable permissions to use your previous free play image!!", Toast.LENGTH_SHORT).show();
                        createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
                    } else {
                        createPuzzle(new PuzzleFunctions().getPuzzle(this));
                    }
                }

                // Free play puzzleFunctions not chosen
                else {
                    createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
                }
            }
        }
    }

    private boolean pathValid(){
        return sessionManager.getPuzzlePath() != null && !sessionManager.getPuzzlePath().equals("");
    }

    /**
     * Creates the TableRows and adds them to the TableLayout.
     */
    private void createBoard(){
        for (int row = 0; row < rows; row++){

            // Creating TableRow
            TableRow tableRow = new TableRow(this);

            for (int col = 0; col < cols; col++){

                // Creating ImageButton
                ImageButton imageButton = new ImageButton(this);
                imageButtons.add(imageButton);
                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Sizes the ImageButtons after being added to the TableLayout.
     */
    private void configureButtons() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        for (ImageButton imageButton : imageButtons) {

            // Setting Attributes
            imageButton.getLayoutParams().width = displayMetrics.widthPixels / cols;
            imageButton.getLayoutParams().height = displayMetrics.heightPixels / rows;
            imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            imageButton.setPadding(0,0,0,0);
            imageButton.requestLayout();

            // Adding Listener
            imageButton.setOnClickListener(imagesListener);
        }
    }

    /**
     * Creates the bitmaps for the ImageButtons.
     * @param bitmap the source bitmap.
     */
    private void createPuzzle(Bitmap bitmap){

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight= bitmap.getHeight();

        for (int h = 0; h < rows; h++){
            for (int w = 0; w < cols; w++){
                bitmaps.add(Bitmap.createBitmap(bitmap, (w * bitmapWidth) / cols, (h * bitmapHeight) / rows, bitmapWidth / cols, bitmapHeight / rows));
            }
        }
        drawPuzzle(bitmaps);
    }

    /**
     * Fills the image imageButtons with bitmaps.
     * @param bitmaps the ArrayList of bitmaps.
     */
    private void drawPuzzle(ArrayList<Bitmap> bitmaps) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        for (int i = 0; i < bitmaps.size(); i++){
            imageButtons.get(i).setImageBitmap(Bitmap.createScaledBitmap(bitmaps.get(i), width / cols, height / rows, false));
        }
        randomize();
    }

    /**
     * Shuffles the tiles.
     */
    private void randomize() {
        List<Drawable> list = new ArrayList<>();
        for (int i = 0; i < rows * cols; i++){
            list.add(imageButtons.get(i).getDrawable());
            answerKey.add(imageButtons.get(i).getDrawable());
        }
        Collections.shuffle(list);
        for (int i = 0; i < rows * cols; i++){
            imageButtons.get(i).setImageDrawable(list.get(i));
        }
        startTimer(startTime);
    }

    /**
     * Click listener for ImageButtons.
     */
    private final View.OnClickListener imagesListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ImageButton imageButton = (ImageButton) view;

            if (counter % 2 == 0) {
                setPrevious(view);
                imageButton.setAlpha(0.6f);
                counter++;

                if (isCampaign){
                    movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesRemaining, movesRemaining));
                }else {
                    movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
                }
            }
            else if(counter % 2 == 1) {
                previousButton.setAlpha(1.0f);
                swapTiles(view);
            }
        }
    };

    private void resetButtons(){
        for (ImageButton imageButton : imageButtons){
            imageButton.setAlpha(1.0f);
        }
    }

    /**
     * Restarts the puzzleFunctions.
     */
    private void restart(){

        // Clearing
        cancelTimers();
        counter = 0;
        movesCounter = 0;
        resetButtons();

        if (isCampaign){
            movesRemaining = movesLimit;
            movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesRemaining, movesRemaining));
            if (startTime > 60) {
                timerTextView.setText(getString(R.string.minutes_seconds, startTime / 60, startTime % 60));
            } else {
                timerTextView.setText(getResources().getQuantityString(R.plurals.seconds, startTime, startTime));
            }
        }else {
            movesTextView.setText(R.string.default_moves);
            timerTextView.setText(R.string.default_time);
        }

        // Restarting
        if (getIntent().getBooleanExtra("offline", false)) {
            timer = null;
            createPuzzle(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(randomLevel(), "drawable", getPackageName())));
            Toast.makeText(this, "Connect to WiFi/Mobile to play full game with high res images!", Toast.LENGTH_LONG).show();
        }else {
            randomize();
        }
        enableButtons();
        pauseButton.setEnabled(true);
        isPause = false;
    }

    /**
     * Pauses the game.
     */
    private void pause(){
        isPause = !isPause;
        if (isPause) {
//            if (isCampaign) {
//                Bundle args = new Bundle();
//                args.putString(PUZZLE_URL_TAG, getIntent().getStringExtra(PUZZLE_URL_TAG));
//                pauseDialogFragment.setArguments(args);
//            }
            pauseDialogFragment.show(getFragmentManager(), null);
            cancelTimers();
        } else {
            startTimer(currentTime);
        }
    }

    /**
     * Disables the ImageButtons.
     */
    private void disableButtons(){
        for (ImageButton imageButton : imageButtons){
            imageButton.setEnabled(false);
        }
    }

    /**
     * Enables the ImageButtons.
     */
    private void enableButtons(){
        for (ImageButton imageButton : imageButtons){
            imageButton.setEnabled(true);
        }
    }

    /**
     * Swaps tiles between two ImageButtons.
     * @param view the last ImageButton clicked.
     */
    private void swapTiles(View view) {
        Drawable drawable;

        for (ImageButton imageButton : imageButtons){

            if (view == imageButton) {
                if (isAdjacent(imageButton)){
                    drawable = previousButton.getDrawable();
                    previousButton.startAnimation(previousAnimation);
                    imageButton.startAnimation(currentAnimation);
                    previousButton.setImageDrawable(imageButton.getDrawable());
                    imageButton.setImageDrawable(drawable);
                    counter++;
                    movesCounter++;

                    if (isCampaign){
                        movesRemaining--;
                        movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesRemaining, movesRemaining));
                    }else {
                        movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
                    }
                }
            }
        }
        if(isSolved()){
            cancelTimers();
            disableButtons();
            pauseButton.setEnabled(false);

            if (isCampaign){
                int bestPossible = levelNum * 10000 + 40000;
                double movesFactor = 1 - ((double) (movesCounter - 1) / movesLimit);
                double timeFactor = 1 - ((double) (startTime - currentTime - 1) /  startTime);
                score = (int)(((movesFactor + timeFactor) / 2) * bestPossible);
                Toast.makeText(this, "Congratulations, You Scored " + score + " points!!!", Toast.LENGTH_LONG).show();
                recordHighScores();
            }else{
                Toast.makeText(this, "Congratulations, You Win!!!", Toast.LENGTH_LONG).show();
            }
        }else if(isCampaign && movesRemaining == 0){
            cancelTimers();
            disableButtons();
            pauseButton.setEnabled(false);

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Objects.requireNonNull(vibrator).vibrate(500);
            Toast.makeText(PuzzleActivity.this, "You ran out of moves!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets the last ImageButton clicked.
     * @param view the ImageButton.
     */
    private void setPrevious(View view) {

        for (ImageButton imageButton : imageButtons){

            if (view.getId() == imageButton.getId()) {
                previousButton = (ImageButton) view;
            }
        }
    }

    /**
     * Checks ImageButtons are adjacent and sets animations.
     * @param button the last ImageButton clicked.
     * @return true or false.
     */
    private Boolean isAdjacent(ImageButton button){

        // Getting indicies
        int previousIndex = 0;
        int currentIndex = 0;
        for (int i = 0; i < imageButtons.size(); i++){
            if (previousButton == imageButtons.get(i)) {
                previousIndex = i;
            }
            if (button == imageButtons.get(i)){
                currentIndex = i;
            }
        }

        // Left
        if(currentIndex - 1 == previousIndex){
            currentAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
            previousAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
            return true;
        }
        // Right
        else if (currentIndex + 1 == previousIndex){
            currentAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
            previousAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
            return true;
        }
        // Up
        else if (currentIndex - cols == previousIndex){
            currentAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            previousAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            return true;
        }
        // Down
        else if (currentIndex + cols == previousIndex){
            currentAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            previousAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            return true;
        }
        // Tiles not adjacent
        counter--;

        if (isCampaign){
            movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesRemaining, movesRemaining));

        }else {
            movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
        }
        // Don't annoy user if they cancelled a selection
        if (currentIndex != previousIndex) {
            Toast.makeText(this, "You must select two adjacent tiles!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Starts a new timer.
     */
    private void startTimer(final int seconds) {

        if (isCampaign){
            countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTime = new BigDecimal(millisUntilFinished / 1000).intValueExact();
                    if (millisUntilFinished / 1000 > 60) {
                        timerTextView.setText(getString(R.string.minutes_seconds, millisUntilFinished / 60000, millisUntilFinished / 1000 % 60));
                    } else {
                        timerTextView.setText(getResources().getQuantityString(R.plurals.seconds, new BigDecimal(millisUntilFinished / 1000).intValueExact(), millisUntilFinished / 1000));
                    }
                }

                @Override
                public void onFinish() {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    Objects.requireNonNull(vibrator).vibrate(500);
                    Toast.makeText(PuzzleActivity.this, "Time's up!", Toast.LENGTH_LONG).show();
                    disableButtons();
                    timerTextView.setText(R.string.default_time);
                }
            };
            countDownTimer.start();
        }else {
            timer = new Timer();
            currentTime = seconds;

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    currentTime++;
                    runOnUiThread(new Runnable() {
                        public void run() {

                            if (currentTime > 60) {
                                timerTextView.setText(getString(R.string.minutes_seconds, currentTime / 60, currentTime % 60));
                            } else {
                                timerTextView.setText(getResources().getQuantityString(R.plurals.seconds, currentTime, currentTime));
                            }
                        }
                    });
                }
            }, 1000, 1000);
        }
    }

    /**
     * Cancels the appropriate game timer.
     */
    private void cancelTimers() {
        if (timer != null) {
            timer.cancel();
        }
        else if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    /**
     * Checks if the puzzleFunctions has been solved.
     */
    private boolean isSolved()
    {
        for (int i = 0; i < imageButtons.size(); i ++) {
            if (imageButtons.get(i).getDrawable() != answerKey.get(i)) {
                return false;
            }
        }
        return true;
    }

    private void recordHighScores(){
        if (connected) {
            LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
            leaderboardEntry.setEmail(sessionManager.getEmail());
            leaderboardEntry.setLevel_num(levelNum);
            leaderboardEntry.setScore(score);

            if (isCampaign){
                leaderboardEntry.setTime(String.valueOf(startTime - currentTime));
            }else {
                leaderboardEntry.setTime(timerTextView.getText().toString());
            }
            leaderboardEntry.setMoves(movesCounter);
            leaderboardFunctions.updateLeaderboards(this, leaderboardEntry);

            Bundle args = new Bundle();
            args.putInt("score", score);
            levelCompleteDialogFragment.setArguments(args);
            levelCompleteDialogFragment.show(getFragmentManager(), null);
        }else{
            showNoNetworkMenu();
        }
    }

    /**
    * Getting random level via resource id string
    * @return a random resource string.
    */
    private String randomLevel(){
        int level = (int) (Math.random() * NUM_LEVELS) + 1;
        return "level_" + level + "_thumb";
    }

    @Override
    public void networkAvailable() {
        if (isInFocus) {
            connected = true;
        }
    }

    @Override
    public void networkUnavailable() {
        if (isInFocus) {
            connected = false;
        }
    }
    /**
     * Shows a menu when no network available.
     */
    private void showNoNetworkMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Your connection was interrupted and your score will not be recorded.  Would you like to retry to submit your score?");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recordHighScores();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void unPause() {
        pause();
    }

    @Override
    public void replay() {
        restart();
    }

    @Override
    public void nextLevel() {
        if (levelNum < NUM_LEVELS){
            getLevel(levelNum + 1);
        }
        else{
            Toast.makeText(this, "This was the last level, you're awesome!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the requested level.
     */
    private void getLevel(final int levelNum) {
        String requestString = "get_level";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading level...");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_GET_LEVEL, new Response.Listener<String>() {
            final Level level = new Level(levelNum);
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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
                        level.setUrl(jsonObject.getString("url"));
                        startGame(level);
                    } else {
                        // Error fetching data. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
                // JSON error
                catch (JSONException e) {
                    progressDialog.dismiss();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "We're sorry! Our servers are down.", Toast.LENGTH_LONG).show();
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

    private void startGame(Level level){
        Intent campaign = new Intent(this, PuzzleActivity.class);
        campaign.putExtra(PUZZLE_URL_TAG, level.getUrl());
        campaign.putExtra(PUZZLE_TIMER_TAG, level.getTimeLimit());
        campaign.putExtra(PUZZLE_LEVEL_TAG, level.getLevelNum());
        campaign.putExtra(PUZZLE_COL_TAG, level.getColumns());
        campaign.putExtra(PUZZLE_ROW_TAG, level.getRows());
        campaign.putExtra(PUZZLE_MOVES_TAG, level.getMoveLimit());
        startActivityForResult(campaign, 0);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
