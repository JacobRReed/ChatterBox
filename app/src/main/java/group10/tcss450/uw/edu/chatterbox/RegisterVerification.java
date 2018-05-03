package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterVerification extends Fragment {
    private RegisterVerification.OnFragmentInteractionListener mListener;

    public RegisterVerification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Register Verification");
        View v = inflater.inflate(R.layout.fragment_register_verification, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Register Verification");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }


        Button loginButton = (Button) v.findViewById(R.id.buttonRegisterVerifyLogin);
        loginButton.setOnClickListener(view -> mListener.onVerificationLogin());

        return v;
    }

    public interface OnFragmentInteractionListener {
            void onVerificationLogin();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterVerification.OnFragmentInteractionListener) {
            mListener = (RegisterVerification.OnFragmentInteractionListener) context;
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

}
