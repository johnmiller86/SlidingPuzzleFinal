package com.johnmillercoding.slidingpuzzle.activities;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.johnmillercoding.slidingpuzzle.R;
import com.johnmillercoding.slidingpuzzle.models.User;
import com.johnmillercoding.slidingpuzzle.utilities.NetworkReceiver;
import com.johnmillercoding.slidingpuzzle.utilities.UserFunctions;


@SuppressWarnings("UnusedParameters")
public class RegisterActivity extends AppCompatActivity implements NetworkReceiver.NetworkStateReceiverListener {

    // UI Components
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private AlertDialog alertDialog;

    // Functions
    private UserFunctions userFunctions;

    // Network
    private NetworkReceiver networkReceiver;
    private boolean isInFocus, connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Linking UI Components
        emailEditText = (EditText) findViewById(R.id.editTextRegisterEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextRegisterPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.editTextConfirmRegisterPassword);

        // Network stuff
        isInFocus = true;
        networkReceiver = new NetworkReceiver();
        networkReceiver.addListener(this);
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Functions
        userFunctions = new UserFunctions();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            unregisterReceiver(networkReceiver);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
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
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Password Policy");
            alertDialog.setMessage("Password must contain:\n\t-8 characters\n\t-1 uppercase\n\t-1 lowercase\n\t-1 number\n\t-1 special character\nSpaces are not permitted.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else if (confirmedPass.equals("")){
            Toast.makeText(RegisterActivity.this, "You must confirm your password!!", Toast.LENGTH_SHORT).show();
        }
        else if (!user.getPassword().equals(confirmedPass)){
            Toast.makeText(RegisterActivity.this, "Password and confirmation do not match!!", Toast.LENGTH_SHORT).show();
        }else{
            if (connected) {
                userFunctions.registerUser(this, user, false);
            }else{
                showNoNetworkMenu();
            }
        }
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
        final CharSequence[] charSequences = { "Retry", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (charSequences[item].equals("Retry")) {
                    registerAccount(new View(getApplicationContext()));
                }else if (charSequences[item].equals("Cancel")) {
                    alertDialog.cancel();
                }
            }
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }
}
