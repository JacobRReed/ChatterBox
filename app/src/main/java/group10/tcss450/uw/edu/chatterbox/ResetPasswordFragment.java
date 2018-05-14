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
        submitButton.setOnClickListener(view -> mListener.onNewPasswordSubmit());

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
        void onNewPasswordSubmit();
    }
}
