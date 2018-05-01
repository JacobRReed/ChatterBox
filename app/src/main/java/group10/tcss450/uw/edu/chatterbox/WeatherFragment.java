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
public class WeatherFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Weather");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        Button changeLocationButton = v.findViewById(R.id.buttonWeatherChangeLocation);
        changeLocationButton.setOnClickListener(view -> mListener.onChangeLocationAction());

        return v;
    }


    public interface OnFragmentInteractionListener {
        void onChangeLocationAction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherFragment.OnFragmentInteractionListener) {
            mListener = (WeatherFragment.OnFragmentInteractionListener) context;
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
