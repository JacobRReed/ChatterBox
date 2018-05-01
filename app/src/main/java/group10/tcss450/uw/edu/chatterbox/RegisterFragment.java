package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
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
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private RegisterAction mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Register");
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        Button registerButton = (Button) v.findViewById(R.id.buttonRegisterRegister);
        registerButton.setOnClickListener(view -> {
            //retrieve text from each text fields
            EditText firstNameTxtbox = (EditText) v.findViewById(R.id.editTextRegisterFirstName);
            String firstName = (String) firstNameTxtbox.getText().toString();

            EditText lastNameTxtbox = (EditText) v.findViewById(R.id.editTextRegisterLastName);
            String lastName = (String) firstNameTxtbox.getText().toString();

            EditText emailTxtbox = (EditText) v.findViewById(R.id.editTextRegisterEmail);
            String email = (String) firstNameTxtbox.getText().toString();

            EditText nicknameTxtbox = (EditText) v.findViewById(R.id.editTextRegisterNickname);
            String nickname = (String) nicknameTxtbox.getText().toString();

            EditText pwTxtbox = (EditText) v.findViewById(R.id.editTextRegisterPassword);
            String pw = (String) pwTxtbox.getText().toString();

            EditText pwConTxtbox = (EditText) v.findViewById(R.id.editTextRegisterPasswordConfirm);
            String pwConfir = (String) pwConTxtbox.getText().toString();

            boolean check = true;
            if(nickname.length() < 1){
                nicknameTxtbox.setError("Username cannot not be blank");
                check = false;
            }
            if(firstName.length() < 1){
                firstNameTxtbox.setError("First name cannot not be blank");
                check = false;
            }
            if(lastName.length() < 1){
                lastNameTxtbox.setError("Last name cannot not be blank");
                check = false;
            }
            if(email.length() < 1){
                emailTxtbox.setError("Email cannot not be blank");
                check = false;
            }
            if(pw.length() < 1){
                pwTxtbox.setError("Password cannot not be blank");
                check = false;
            }
            if(pwConfir.length() < 1){
                pwConTxtbox.setError("Password confirmation cannot not be blank");
                check = false;
            }
            if(pw.length() < 6){
                pwTxtbox.setError("Password length must be more than 5");
                pwConTxtbox.setError("Password length must be more than 5");
                check = false;
            }
            if(!pw.equals(pwConfir)){
                pwTxtbox.setError("Passwords do not match ");
                pwConTxtbox.setError("Passwords do not match");
                check = false;
            }
            if(check) {
                Editable password = new SpannableStringBuilder(pwTxtbox.getText().toString());
                Credentials.Builder userInfoBuilder = new Credentials.Builder(nickname, password);
                userInfoBuilder.addFirstName(firstName);
                userInfoBuilder.addLastName(lastName);
                userInfoBuilder.addEmail(email);
                Credentials userInfo = userInfoBuilder.build();
                mListener.onRegistrationInteraction(userInfo);
            }

        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String nickname  = getArguments().getString("nickname");
            String pw  = getArguments().getString("password");
            String firstName  = getArguments().getString("firstName");
            String lastName  = getArguments().getString("lastName");
            String email  = getArguments().getString("email");
            regUpdateContent(nickname,pw, firstName, lastName, email);
        }
    }

    /**
     * Set the editText to text from Login Fragment
     */
    public void regUpdateContent(String nickname, String pw, String firstname, String lastname, String email) {

        TextView tvNickname = getActivity().findViewById(R.id.editTextRegisterNickname);
        tvNickname.setText(nickname);

        TextView tvPW = getActivity().findViewById(R.id.editTextRegisterPassword);
        tvPW.setText(pw);

        TextView tvfirstName = getActivity().findViewById(R.id.editTextRegisterFirstName);
        tvfirstName.setText(firstname);

        TextView tvlastName = getActivity().findViewById(R.id.editTextRegisterLastName);
        tvlastName.setText(lastname);

        TextView tvEmail = getActivity().findViewById(R.id.editTextRegisterEmail);
        tvEmail.setText(email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterAction) {
            mListener = (RegisterAction) context;
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

    @Override
    public void onClick(View v) {

    }

    public interface RegisterAction {
        void onRegistrationInteraction(Credentials userInfo);
    }
}
