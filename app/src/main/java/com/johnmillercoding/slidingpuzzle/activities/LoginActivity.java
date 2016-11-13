package com.johnmillercoding.slidingpuzzle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.User;
import com.johnmillercoding.slidingpuzzle.utilities.NetworkReceiver;
import com.johnmillercoding.slidingpuzzle.utilities.SessionManager;
import com.johnmillercoding.slidingpuzzle.utilities.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


@SuppressWarnings("UnusedParameters")
public class LoginActivity extends AppCompatActivity implements NetworkReceiver.NetworkStateReceiverListener {

    // UI Components
    private EditText emailEditText, passwordEditText;
    private AlertDialog alertDialog;

    // Session
    private SessionManager sessionManager;
    private CallbackManager callbackManager;
    private User user;
    private UserFunctions userFunctions;

    // Network
    private NetworkReceiver networkReceiver;
    private boolean isInFocus, connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Facebook first
        configureFacebook();

        // Linking UI Components
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);

        // Network stuff
        isInFocus = true;
        networkReceiver = new NetworkReceiver();
        networkReceiver.addListener(this);
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            // Session
            sessionManager = new SessionManager(getApplicationContext());
            userFunctions = new UserFunctions();

            // If user is logged in, continue to the main activity
            if (sessionManager.isLoggedIn() && connected) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
    }

    @Override
    protected void onResume(){
        super.onResume();
        isInFocus = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        isInFocus = false;
    }

    // Login button Click Event
    public void Login(View view) {

        user = new User();
        user.setEmail(emailEditText.getText().toString().trim());
        user.setPassword(passwordEditText.getText().toString().trim());

        // Login user
        if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {
            userFunctions.loginUser(this, sessionManager, user);
        }
        // Empty EditTexts
        else {
            Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
        }
    }

    // Register button Click Event
    public void Register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Configures the Facebook button.
     */
    private void configureFacebook() {

        // Initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        // Listener
        LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            // User was successfully logged in
            @Override
            public void onSuccess(final LoginResult loginResult) {

                // Get the user's profile
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                // Email received, proceed
                                try {
                                    // Configuring User
                                    user = new User();
                                    user.setPassword("Not today sir!");
                                    user.setEmail(object.getString("email"));

                                    // Configure session
                                    sessionManager.saveAccessToken(loginResult.getAccessToken().getToken());
                                    sessionManager.setLoggedIn();
                                    sessionManager.setEmail(user.getEmail());
                                    sessionManager.setFacebookImageUrl("https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large");
                                    userFunctions.registerUser(LoginActivity.this, user, true);

                                    // Proceed
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    Toast.makeText(LoginActivity.this, "There was an error with Facebook!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                // Necessary Facebook params
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            // The user cancelled the login, do nothing
            @Override
            public void onCancel() {
            }

            // There was an error logging in, print out the errors
            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });
    }

    // Handles results from the FacebookLogin Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void networkAvailable() {
        if (isInFocus) {

            connected = true;

            // Dismiss AlertDialog
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            // Return to main menu
            if (sessionManager.isLoggedIn()){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void networkUnavailable() {
        if (isInFocus) {
            showNoNetworkMenu();
        }
        connected = false;
    }

    /**
     * Shows a menu when no network available.
     */
    private void showNoNetworkMenu() {
        final CharSequence[] charSequences = { "Play Offline", "Exit Game"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (charSequences[item].equals("Play Offline")) {

                    // Start intent, but leave this activity open to return to
                    Intent intent = new Intent(LoginActivity.this, PuzzleActivity.class);
                    intent.putExtra("random", true);
                    startActivity(intent);
                }else if (charSequences[item].equals("Exit Game")) {
                    finish();
                }
            }
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }
}

