package group10.tcss450.uw.edu.chatterbox;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterVerification extends Fragment {


    public RegisterVerification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Register Verification");
        View v = inflater.inflate(R.layout.fragment_register_verification, container, false);

        Button loginButton = (Button) v.findViewById(R.id.buttonRegisterVerifyLogin);
        loginButton.setOnClickListener((View view) ->  {



        });

        return v;
    }

}
