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

import group10.tcss450.uw.edu.chatterbox.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button submitButton = v.findViewById(R.id.resetPasswordSubmitButton);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reset Password");
        } catch (NullPointerException e) {
            Log.e("Error", "registration title isn't working");
        }


        submitButton.setOnClickListener(view -> {

            // variables
            EditText pwTxtbox = (EditText) v.findViewById(R.id.resetPasswordEditText);
            String pw = (String) pwTxtbox.getText().toString();
            EditText pwConTxtbox = (EditText) v.findViewById(R.id.resetPasswordConfirmEditText);
            String pwConfir = (String) pwConTxtbox.getText().toString();
            EditText emailTxtbox = (EditText) v.findViewById(R.id.resetPEmailEditText);
            String email = (String) emailTxtbox.getText().toString();
            EditText usernameTxtbox = (EditText) v.findViewById(R.id.resetPUsernameEditText);
            String username = (String) usernameTxtbox.getText().toString();


            boolean check = true;

            if(pw.length() < 1){
                pwTxtbox.setError("New password cannot not be blank");
                check = false;
            }
            if(pwConfir.length() < 1){
                pwConTxtbox.setError("Password confirmation cannot not be blank");
                check = false;
            }
            if(pw.length() < 6){
                pwTxtbox.setError("New password length must be more than 5");
                pwConTxtbox.setError("New Password length must be more than 5");
                check = false;
            }
            if(!pw.equals(pwConfir)){
                pwTxtbox.setError("Passwords do not match ");
                pwConTxtbox.setError("Passwords do not match");
                check = false;
            }
            if(check) {
                Editable password = new SpannableStringBuilder(pwTxtbox.getText().toString());
                Credentials.Builder userInfoBuilder = new Credentials.Builder(username, password);
                userInfoBuilder.addEmail(email);
                Credentials userInfo = userInfoBuilder.build();
                mListener.onNewPasswordSubmit(userInfo);
            }
        });
        return v;
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
        void onNewPasswordSubmit(Credentials credentials);
    }
}
