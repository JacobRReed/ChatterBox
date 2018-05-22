package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import group10.tcss450.uw.edu.chatterbox.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Login");

        } catch (NullPointerException e) {
            Log.e("Error", "login title isn't working");
        }

        Button loginButton = v.findViewById(R.id.buttonLoginLogin);
        Button registerButton = v.findViewById((R.id.buttonLoginRegister));
        Button forgotPasswordButton = v.findViewById(R.id.buttonLoginForgotPassword);
        EditText loginEmailTextBox = v.findViewById(R.id.editTextLoginEmail);
        EditText passwordTextBox = v.findViewById(R.id.editTextLoginPassword);

        /*
        Handle login button and register button on clicks. Does checks against lengths and requirements
         */
        loginButton.setOnClickListener(view -> {
            boolean flag = true;

            if(loginEmailTextBox.getText().toString().length() < 1) {
                loginEmailTextBox.setError("Must have a valid username!");
                flag = false;
            }
            if(passwordTextBox.getText().length() < 1) {
                passwordTextBox.setError("Must have a password!");
                flag = false;
            }
            if(flag) {
                Editable password = new SpannableStringBuilder(passwordTextBox.getText().toString());
                String loginUsername = loginEmailTextBox.getText().toString();
                Credentials.Builder mCredentials = new Credentials.Builder(loginUsername, password);
                Credentials mc = mCredentials.build();
                SharedPreferences prefs =
                        getActivity().getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                prefs.edit().putString(getString(R.string.keys_prefs_username_local), loginUsername).apply();
                prefs.edit().putString(getString(R.string.keys_prefs_current_chatid), "1").apply(); //Keldon added this
                Log.wtf("Local Username:", prefs.getString("usernameLocal", ""));
                mListener.onLoginAttempt(mc);
            }
        });
        registerButton.setOnClickListener(view -> mListener.onLoginRegisterAction());
        forgotPasswordButton.setOnClickListener(view -> mListener.onForgotPasswordAction());

        return v;
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
//        Log in unsuccessful for reason: err. Try again.
//        you may want to add error stuffs for the user here.
        ((TextView) getView().findViewById(R.id.logEmailEditText))
                .setError(err);
    }

    /**
     * Attaches context to fragment mListener
     * @param context Context to attach
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Detaches mListener onDetach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Handles interface to be implemented by host activity.
     */
    public interface OnFragmentInteractionListener {
        void onLoginAttempt(Credentials credentials);
        void onLoginAction();
        void onLoginRegisterAction();
        void onForgotPasswordAction();
    }
}
