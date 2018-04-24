package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
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
public class ChangeLocationFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public ChangeLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_location, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        Button submitChangeButton = v.findViewById(R.id.buttonChangeLocationSetChanges);
        submitChangeButton.setOnClickListener(view -> mListener.onChangeLocationSubmitAction());

        return v;
    }


    public interface OnFragmentInteractionListener {
        void onChangeLocationSubmitAction();
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
