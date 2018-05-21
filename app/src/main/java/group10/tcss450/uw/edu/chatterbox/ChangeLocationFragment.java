package group10.tcss450.uw.edu.chatterbox;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnSuccessListener;


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
        mapButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LocationMapChange.class);
            startActivity(intent);
        });

        return mView;
    }

    public interface OnFragmentInteractionListener {
        void onChangeLocationSubmitAction(String zipcode);
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
