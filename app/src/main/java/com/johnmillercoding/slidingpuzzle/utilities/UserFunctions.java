package com.johnmillercoding.slidingpuzzle.utilities;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.slidingpuzzle.activities.MainActivity;
import com.johnmillercoding.slidingpuzzle.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserFunctions {

    /**
     * Authenticates user against MySQL.
     */
    public void loginUser(final Activity activity, final SessionManager sessionManager, final User user) {
        String requestString = "login";
        Toast.makeText(getApplicationContext(), "Logging in ...", Toast.LENGTH_SHORT).show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Successful login
                    if (!error) {

                        // Create login session
                        sessionManager.setLoggedIn();

                        // Set info
                        sessionManager.setEmail(user.getEmail());

                        // Launch main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    // Login error
                    else {
                        String errorMsg = jObj.getString("error_msg");
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
                params.put("email", user.getEmail());
                params.put("password", hashPassword(user.getPassword()));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }

    /**
     * Registers user into MySQL.
     */
    public void registerUser(final Activity activity, final User user, final boolean facebook) {
        String requestString = "register";

        if (facebook){
            Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "Registering...", Toast.LENGTH_SHORT).show();
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Register success
                    if (!error && !facebook) {
                        Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();
                        activity.finish();
                    }
                    // Register error
                    else {
                        if (!facebook) {
                            Toast.makeText(getApplicationContext(), jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                // JSON error
                catch (JSONException e) {
                    e.printStackTrace();
                    if (!facebook) {
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (!facebook) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                if (facebook){
                    params.put("facebook", "true");
                }
                params.put("email", user.getEmail());
                params.put("password", hashPassword(user.getPassword()));

                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }

    /**
     * Hashes the user's password.
     * @param password the password to be hashed.
     * @return the hashed password.
     */
    private String hashPassword(String password) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }
}
