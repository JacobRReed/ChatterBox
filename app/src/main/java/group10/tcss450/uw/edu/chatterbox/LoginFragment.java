package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
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

        // variables
        Button loginButton = v.findViewById(R.id.buttonLoginLogin);
        Button registerButton = v.findViewById((R.id.buttonLoginRegister));
        EditText usernameTextBox = (EditText) v.findViewById(R.id.editTextLoginEmail);
        EditText passwordTextBox = (EditText) v.findViewById(R.id.editTextLoginPassword);

        loginButton.setOnClickListener(view -> {
            boolean flag = true;

            if(usernameTextBox.getText().toString().length() < 1) {
                usernameTextBox.setError("Must have a username!");
                flag = false;
            }
            if(passwordTextBox.getText().length() < 1) {
                passwordTextBox.setError("Must have a password!");
                flag = false;
            }
            if(flag) {
                Editable password = new SpannableStringBuilder(passwordTextBox.getText().toString());
                String username = usernameTextBox.getText().toString();
                Credentials.Builder mCredentials = new Credentials.Builder(username, password);
                Credentials mc = mCredentials.build();
                mListener.onLoginAttempt(mc);
            }
        });

        registerButton.setOnClickListener(view -> mListener.onLoginRegisterAction());

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
        ((TextView) getView().findViewById(R.id.logUsernameEditText))
                .setError("Login Unsuccessful");
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLoginAttempt(Credentials credentials);
        void onLoginAction();
        void onLoginRegisterAction();
    }
}
