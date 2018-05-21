package group10.tcss450.uw.edu.chatterbox;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMapChange extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, WeatherFragment.OnFragmentInteractionListener {

    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_LOC = "location_pref";
    private GoogleMap mMap;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LatLng mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Set Theme from SharedPrefs
         */
        SharedPreferences themePreferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePreferences.getInt(PREFS_THEME, 0);
        //Apply themes
        switch (themeChoice) {
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
        //Load map change location activity
        setContentView(R.layout.activity_location_map_change);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //FAB to handle location change. When user clicks fab it saves current marker as new location.
        FloatingActionButton fab = findViewById(R.id.setLocationOnMap);
        fab.setOnClickListener(view -> {
            //Save lat/lon of marker as new location
            SharedPreferences.Editor locEditor = this.getSharedPreferences(PREFS_LOC, MODE_PRIVATE).edit();
            locEditor.putFloat("lat", (float) mLocation.latitude);
            locEditor.putFloat("lon", (float) mLocation.longitude);
            locEditor.putBoolean("searchZip", false);
            locEditor.commit();
            Log.e("Sent coordinates to shared prefs:", mLocation.toString());
            //Load the homeactivity
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

        //Sets map to last known latlon from sharedprefs
        SharedPreferences locpref = this.getSharedPreferences(PREFS_LOC, MODE_PRIVATE);
        float lat = locpref.getFloat("lat", 0);
        float lon = locpref.getFloat("lon", 0);
        LatLng coord = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(coord).title("Marker in Tacoma"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 15f));
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
