package com.johnmillercoding.slidingpuzzle.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.User;
import com.johnmillercoding.slidingpuzzle.utilities.UserFunctions;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@SuppressWarnings("UnusedParameters")
public class RegisterActivity extends AppCompatActivity {

    // UI Components
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
//    private ProgressDialog progressDialog;

    private UserFunctions userFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Linking UI Components
        emailEditText = (EditText) findViewById(R.id.editTextRegisterEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextRegisterPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.editTextConfirmRegisterPassword);

        // Progress dialog
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
        userFunctions = new UserFunctions();
    }

    /**
     * Listener for the register button.
     * @param view the register button.
     */
    public void registerAccount(View view) {

        // Configure user
        User user = new User();
        user.setEmail(emailEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());
        String confirmedPass = confirmPasswordEditText.getText().toString();

        // Validating input
        if (user.getEmail().equals("")){
            Toast.makeText(RegisterActivity.this, "You must provide a valid email address!!", Toast.LENGTH_SHORT).show();
        }
        else if (user.getPassword().equals("")){
            Toast.makeText(RegisterActivity.this, "You must choose a password!!", Toast.LENGTH_SHORT).show();
        }
        else if(!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")){
            Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters, contain no spaces and contain one uppercase, one lowercase, one number and one " +
                    "special character.", Toast.LENGTH_LONG).show();
        }
        else if (confirmedPass.equals("")){
            Toast.makeText(RegisterActivity.this, "You must confirm your password!!", Toast.LENGTH_SHORT).show();
        }
        else if (!user.getPassword().equals(confirmedPass)){
            Toast.makeText(RegisterActivity.this, "Password and confirmation do not match!!", Toast.LENGTH_SHORT).show();
        }else{
            user.setPassword(getSha512SecurePassword(user.getPassword()));
            userFunctions.registerUser(this, user, false);
        }
    }

//    /**
//     * Registers user into the mysql database.
//     * @param email the user's email.
//     * @param password the entered password.
//     */
//    private void registerUser(final String email, final String password) {
//        // Tag used to cancel the request
//        String requestString = "register";
//
//        progressDialog.setMessage("Registering...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_REGISTER, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Register Response: " + response);
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    // Register success
//                    if (!error) {
//                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
//                        finish();
//                    }
//                    // Register error
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
//                Log.e(TAG, "Registration Error: " + error.getMessage());
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
//        VolleyController.getInstance().addToRequestQueue(strReq, requestString);
//    }

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

    /**
     * Generates a sha512 encoded password.
     * @param passwordToHash the password to be hashed.
     * @return the hashed password.
     */
    private String getSha512SecurePassword(String passwordToHash){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
