package group10.tcss450.uw.edu.chatterbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

public class LocationMapChange extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, WeatherFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_LOC = "location_pref";
    private LatLng mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePreferences.getInt(PREFS_THEME, 0);
        //Apply themes
        switch(themeChoice) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppThemeTwo);
                break;
            case 3:
                setTheme(R.style.AppThemeThree);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
        setContentView(R.layout.activity_location_map_change);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.setLocationOnMap);
        fab.setOnClickListener(view -> {
            SharedPreferences.Editor locEditor = this.getSharedPreferences(PREFS_LOC, MODE_PRIVATE).edit();
            locEditor.putFloat("lat", (float) mLocation.latitude);
            locEditor.putFloat("lon", (float) mLocation.longitude);
            locEditor.putBoolean("searchZip", false);
            locEditor.commit();
            Log.e("Sent coordinates to shared prefs:", mLocation.toString());
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });


    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng tac = new LatLng(47.25, -122.44);
        mMap.addMarker(new MarkerOptions().position(tac).title("Marker in Tacoma"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tac, 15f));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        mLocation = latLng;
        mMap.clear();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Desired Weather Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }



    @Override
    public void onChangeLocationAction() {
        //Do Nothing
        return;
    }

    @Override
    public void onLogout() {
        //Do nothing
        return;
    }
}
