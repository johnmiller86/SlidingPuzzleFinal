package com.johnmillercoding.slidingpuzzle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
//    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFacebook();

        // Session
        sessionManager = new SessionManager(getApplicationContext());
        userFunctions = new UserFunctions();

        // Database Manager
//        dbHelper = new DBHelper(this.getApplicationContext());
//        DatabaseManager.initializeInstance(dbHelper);

        // Check if user is already logged in or not
        if (sessionManager.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Database Manager
//        DBHelper dbHelper = new DBHelper(this.getApplicationContext());
//        DatabaseManager.initializeInstance(dbHelper);

        // Linking UI Components
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
    }

    // Login button Click Event
    public void Login(View view) {

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
    }

    // Register button Click Event
    public void Register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Configures the Facebook button.
     */
    private void initializeFacebook() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        // Listener for FacebookLoginButton
        LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            // User was successfully logged in
            @Override
            public void onSuccess(final LoginResult loginResult) {

                // Get their profile
//                Profile profile = Profile.getCurrentProfile();

//                // Configuring User
//                user = new User();
//                user.setPassword("Not today sir!");
//                user.setEmail(profile.getName());

                // Inserting
//                if (!userFunctions.userExists(user)){
//                    userFunctions.insertUser(user);
//                }
                // Requests the user's email address from Facebook
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

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

//    /**
//     * Authenticates against the mysql database.
//     * @param email the user's email.
//     * @param password the entered password.
//     */
//    private void loginUser(final String email, final String password) {
//        // Tag used to cancel the request
//        String tag_string_req = "req_login";
//
//        progressDialog.setMessage("Logging in ...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Login Response: " + response);
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    // Successful login
//                    if (!error) {
//
//                        // Create login session
//                        sessionManager.setLoggedIn(true);
//
//                        // Set info
//                        sessionManager.getEmail(user.getEmail());
//
//                        // Launch main activity
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                    // Login error
//                    else {
//                        String errorMsg = jObj.getString("error_msg");
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
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
//                params.put("password", password);
//
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        VolleyController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//
//    /**
//     * Shows the progress dialog.
//     */
//    private void showDialog() {
//        if (!progressDialog.isShowing())
//            progressDialog.show();
//    }
//
//    /**
//     * Closes the progress dialog.
//     */
//    private void hideDialog() {
//        if (progressDialog.isShowing())
//            progressDialog.dismiss();
//    }
//

    /**
     * Generates a sha512 encoded password.
     *
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
}

