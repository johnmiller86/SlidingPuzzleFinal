package com.johnmillercoding.slidingpuzzle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.LeaderboardEntry;
import com.johnmillercoding.slidingpuzzle.utilities.PuzzleFunctions;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_COL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_LEVEL_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_MODE_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_ROW_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.PUZZLE_TIMER_TAG;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.leaderboardFunctions;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;
import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.settings;

public class PuzzleActivity extends AppCompatActivity {

    // Session
////    SessionManager sessionManager = new SessionManager(getBaseContext());
//    private UserFunctions userFunctions; //= new UserFunctions();
//    private User user; //= userFunctions.getUser(sessionManager.getEmail());
//    private SettingFunctions settingFunctions;// = new SettingFunctions();
//    private Settings settings;// = settingFunctions.getSettings(this, sessionManager.getEmail());
////    private PuzzleFunctions puzzleFunctions;// = new PuzzleFunctions();
//    private PuzzleFunctions puzzleFunctions;// = puzzleFunctions.getPuzzle(user);
//    private LeaderboardFunctions leaderboardFunctions;// = new LeaderboardFunctions();
////    private final LeaderboardEntry leaderboardEntry = leaderboardFunctions.getLeaderboards(user);

    // UI components
    private TableLayout tableLayout;
    private TextView movesTextView, timerTextView;
    private ImageButton previousButton;
    private Button pauseButton, resetButton;

    // Lists
    private List<ImageButton> imageButtons;
    private List<Drawable> answerKey;

    // Vars
    private Timer timer;
    private CountDownTimer countDownTimer;
    private Animation currentAnimation, previousAnimation;
    private int counter, movesCounter, rows, cols, startTime, currentTime, score, levelNum;
    private boolean isPause, isCampaign;

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
    }


    @Override
    public void onBackPressed() {

        if (!isSolved()) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to quit?")

                    // Open Settings button
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    })

                    // Denied, close app
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
        }else{
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureButtons();
    }

    /**
     * Initializes all references.
     */
    private void initializeReferences() {

//        // Initializing Session
//        UserFunctions userFunctions = new UserFunctions();
//        User user = userFunctions.getUser(sessionManager.getEmail());
//        SettingFunctions settingFunctions = new SettingFunctions();
//        Settings settings = settingFunctions.getSettings(this.getApplicationContext(), sessionManager.getEmail());
////        PuzzleFunctions puzzleFunctions = new PuzzleFunctions();
////        PuzzleFunctions puzzleFunctions = puzzleFunctions.getPuzzle(user);
//        puzzleFunctions = new PuzzleFunctions();
////        puzzleFunctions.setPuzzlePath(this, settings);
////        leaderboardFunctions = new LeaderboardFunctions();
////    private final LeaderboardEntry leaderboardEntry = leaderboardFunctions.getLeaderboards(user);


        // Initializing Layout
        tableLayout = (TableLayout) findViewById(R.id.table_layout);

        // Initializing TextViews
        movesTextView = (TextView) findViewById(R.id.currentMoves);
        timerTextView = (TextView) findViewById(R.id.editTextTimer);

        // Initializing Lists
        imageButtons = new ArrayList<>();
        answerKey = new ArrayList<>();

        // Campaign, set size
        if (getIntent().getStringExtra(PUZZLE_MODE_TAG) != null){
            rows = getIntent().getIntExtra(PUZZLE_ROW_TAG, 4);
            cols = getIntent().getIntExtra(PUZZLE_COL_TAG, 3);
        }
        // Free play use user settings if available
        else{
            rows = settings.getRows();
            cols = settings.getColumns();
        }

        // Initializing ImageButtons and adding to list
        createBoard();

        configureButtons();

        // Initializing pause and reset buttons
        pauseButton = (Button) findViewById(R.id.button_pause);
        resetButton = (Button) findViewById(R.id.button_reset);

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
        if (intent.getStringExtra(PUZZLE_MODE_TAG) != null){
            isCampaign = true;
            startTime = intent.getIntExtra(PUZZLE_TIMER_TAG, 0);
            levelNum = intent.getIntExtra(PUZZLE_LEVEL_TAG, 1);
//            rows = intent.getIntExtra(PUZZLE_ROW_TAG, 4);
//            cols = intent.getIntExtra(PUZZLE_COL_TAG, 3);

            // Create bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(intent.getStringExtra(PUZZLE_MODE_TAG), "drawable", getPackageName()));
            createPuzzle(bitmap);
        }
        // Free play
        else{
            isCampaign = false;
            startTime = 0;

            // Free play puzzleFunctions set, but deleted or on another device
            if (sessionManager.getPuzzlePath() != null && !(new File(sessionManager.getPuzzlePath()).exists())){
                Toast.makeText(this, "Image deleted or you are on another device!!", Toast.LENGTH_SHORT).show();
                createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
            }

            // Use the free play puzzleFunctions
            else if (sessionManager.getPuzzlePath() != null && new File(sessionManager.getPuzzlePath()).exists()){
                Bitmap bitmap = new PuzzleFunctions().getPuzzle(this);

                // User has reinstalled and read permissions not yet enabled
                if (bitmap == null) {
                    Toast.makeText(this, "You must enable permissions to use your previous free play image!!", Toast.LENGTH_SHORT).show();
                    createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
                }else{
                    createPuzzle(new PuzzleFunctions().getPuzzle(this));
                }
            }

            // Free play puzzleFunctions not chosen
            else {
                createPuzzle(BitmapFactory.decodeResource(getResources(), R.drawable.level_1));
            }
        }
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
                movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
                counter++;
            }
            else if(counter % 2 == 1) {
                previousButton.setAlpha(1.0f);
                swapTiles(view);
            }
        }
    };

    /**
     * Restarts the puzzleFunctions.
     */
    private void restart(){

        // Clearing
        cancelTimers();
        counter = 0;
        movesCounter = 0;
        movesTextView.setText(R.string.default_moves);
        timerTextView.setText(R.string.default_time);

        // Restarting
        randomize();
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
            disableButtons();
            resetButton.setEnabled(false);
            cancelTimers();
        } else {
            enableButtons();
            resetButton.setEnabled(true);
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
                    movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
                }
            }
        }
        if(isSolved()){
            cancelTimers();
            disableButtons();
            pauseButton.setEnabled(false);

            if (isCampaign){
                score = ((startTime - currentTime) * 10000) / movesCounter;
                Toast.makeText(this, "Congratulations You Scored " + score + " points!!!", Toast.LENGTH_LONG).show();
                recordHighScores();
            }
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
        movesTextView.setText(getResources().getQuantityString(R.plurals.moves, movesCounter, movesCounter));
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
                    vibrator.vibrate(500);
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
            // TODO need to do level num and probably levels table
        LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
        leaderboardEntry.setEmail(sessionManager.getEmail());
        leaderboardEntry.setLevel_num(levelNum);
        leaderboardEntry.setScore(score);
        leaderboardEntry.setTime(timerTextView.getText().toString());
        leaderboardEntry.setMoves(movesCounter);
        leaderboardFunctions.updateLeaderboards(this, leaderboardEntry);
//        leaderboardFunctions.insert(user, leaderboardEntry);
    }
}
