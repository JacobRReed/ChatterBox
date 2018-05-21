package group10.tcss450.uw.edu.chatterbox;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLocationFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View mView;

    private static final String TAG = "MyLocationsFragment";


    public ChangeLocationFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_change_location, container, false);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Change Location");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        EditText zipCode = mView.findViewById(R.id.editTextChangeLocationZipcode);
        Button submitChangeButton = mView.findViewById(R.id.buttonChangeLocationSetChanges);
        submitChangeButton.setOnClickListener(view -> mListener.onChangeLocationSubmitAction(zipCode.getText().toString()));
        Button mapButton = mView.findViewById(R.id.chooseLocationMapButton);
        //Loads map activity on click
        mapButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LocationMapChange.class);
            startActivity(intent);
        });
        return mView;
    }

    /**
     * Interface to be implemented by host activity.
     */
    public interface OnFragmentInteractionListener {
        void onChangeLocationSubmitAction(String zipcode);
    }

    /*
    Attaches mListener from activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangeLocationFragment.OnFragmentInteractionListener) {
            mListener = (ChangeLocationFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /*
    Detaches mListener
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
