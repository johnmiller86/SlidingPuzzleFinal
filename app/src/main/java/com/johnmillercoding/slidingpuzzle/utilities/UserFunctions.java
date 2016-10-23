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

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class UserFunctions {

    /**
     * Authenticates user against MySQL.
     */
    public void loginUser(final Activity activity, final SessionManager sessionManager, final User user) {
        String requestString = "login";

//        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setMessage("Logging in ...");
//        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Successful login
                    if (!error) {

                        // Create login session
                        sessionManager.setLoggedIn(true);

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
//                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
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

//        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        if (facebook){
//            progressDialog.setMessage("Logging in...");
//        }else {
//            progressDialog.setMessage("Registering...");
//        }
//        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                progressDialog.hide();

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
                        String errorMsg = jObj.getString("error_msg");
                        if (!facebook) {
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
//                progressDialog.hide();
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
                params.put("password", user.getPassword());

                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
    }
}
