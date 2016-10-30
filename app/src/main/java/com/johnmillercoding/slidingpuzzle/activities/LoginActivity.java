package com.johnmillercoding.slidingpuzzle.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.johnmillercoding.slidingpuzzle.utilities.SessionManager;
import com.johnmillercoding.slidingpuzzle.utilities.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


@SuppressWarnings("UnusedParameters")
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText emailEditText, passwordEditText;

    // Session
    private SessionManager sessionManager;
    private CallbackManager callbackManager;
    private User user;
    private UserFunctions userFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureFacebook();
        // Linking UI Components
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);

        // Check network connectivity
        if (!networkAvailable()){
            showNoNetworkMenu();
        }else {
            // Session
            sessionManager = new SessionManager(getApplicationContext());
            userFunctions = new UserFunctions();

            // If user is logged in, continue to the main activity
            if (sessionManager.isLoggedIn()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!networkAvailable()){
            showNoNetworkMenu();
        }
    }

    // Login button Click Event
    public void Login(View view) {

//        if (!networkAvailable()){
//            Toast.makeText(this, "No network connection!!", Toast.LENGTH_LONG).show();
//        }else {
            user = new User();
            user.setEmail(emailEditText.getText().toString().trim());
            user.setPassword(passwordEditText.getText().toString().trim());

            // Login user
            if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {

                // Encode password before sending
                user.setPassword(getSha512SecurePassword(user.getPassword()));
                userFunctions.loginUser(this, sessionManager, user);
            }
            // Empty EditTexts
            else {
                Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
            }
//        }
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

                                    // Insert if new user  TODO handle in PHP
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

    /**
     * Generates a sha512 encoded password.
     * @param passwordToHash the password to be hashed.
     * @return the hashed password.
     */
    private String getSha512SecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Checks if the network is available.
     * @return true or false.
     */
    private boolean networkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                if (networkInfo.isConnected()) {
                    // Connected to wifi network
                    return true;
                }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected()) {
                    // Connected to mobile network
                    return true;
                }
            }
        }
        // No connection
        return false;
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
        builder.show();
    }
}

