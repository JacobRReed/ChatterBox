package group10.tcss450.uw.edu.chatterbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import group10.tcss450.uw.edu.chatterbox.model.Credentials;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.RegisterAction {

    private static final String PREFS_THEME = "theme_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_main);
        loadFragment(new LoginFragment());
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onLoginAction(String username, String password) {
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
    public void onRegistrationInteraction(Credentials userInfo) {

        RegisterVerification registerVer = new RegisterVerification();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, registerVer);
        FragmentManager fragManager = getSupportFragmentManager();
        fragManager.popBackStack();
        transaction.commit();

        //Use this code when end points is ready for Asyntask
        /*//build the web service URL
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
        fragManager.popBackStack();*/
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}
