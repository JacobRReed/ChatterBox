package group10.tcss450.uw.edu.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group10.tcss450.uw.edu.chatterbox.model.Credentials;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.RegisterAction, RegisterVerification.OnFragmentInteractionListener {

    private static final String PREFS_THEME = "theme_pref";
    private static Credentials mCredentials = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = preferences.getInt(PREFS_THEME, 0);
        switch(themeChoice) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppThemeTwo);
                break;
            case 3:
                setTheme(R.style.AppThemeThree);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }

        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                SharedPreferences prefs =
                        getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                        false)) {
                    onLoginAction();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer, new LoginFragment(), getString(R.string.keys_fragment_login))
                            .commit();
                }
            }
        }



    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                checkStayLoggedIn();
                onLoginAction();
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the usertest
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.checkBoxLoginStayLogIn)).isChecked()) {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_prefs_username),
                    mCredentials.getEmail())
                    .apply();
            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    true)
                    .apply();
        }
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");


            if (success) {
                // registration was successful.
                // Switch to the registration verfication frag
                loadFragment(new RegisterVerification());
            } else {
                Toast.makeText(this, "Registration Unsuccessful. Either due to server error or duplicate username.", Toast.LENGTH_SHORT).show();

            }

        } catch (JSONException e) {

            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    @Override
    public void onLoginAction() {
        //Load home activity
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }

    @Override
    public void onLoginRegisterAction() {
        // retrieve text from email and password edit text
        // to pass into registration form
        EditText emailTxtbox = (EditText) findViewById(R.id.editTextLoginEmail);
        String email = (String)emailTxtbox.getText().toString();

        EditText pwTxtbox = (EditText) findViewById(R.id.editTextLoginPassword);
        String pw = (String)pwTxtbox.getText().toString();

        RegisterFragment regFrag = new RegisterFragment();

        // passing text from the username and password text box
        // in Login Fragment to Registration Fragment
        Bundle regArgs = new Bundle();
        regArgs.putSerializable("email", email);
        regArgs.putSerializable("password", pw);
        regFrag.setArguments(regArgs);

        //Load registration fragment
        loadFragment(regFrag);

    }

    @Override
    public void onLoginAttempt(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    public void onRegistrationInteraction(Credentials userInfo) {

        //Use this code when end points is ready for Asyntask
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();

        //build the JSONObject
        JSONObject msg = userInfo.asJSONObject();
        mCredentials = userInfo;

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

        FragmentManager fragManager = getSupportFragmentManager();
        fragManager.popBackStack();
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onVerificationLogin() {
        loadFragment(new LoginFragment());
    }
}
