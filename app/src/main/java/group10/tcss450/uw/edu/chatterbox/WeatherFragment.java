package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;
import static android.content.Context.MODE_PRIVATE;


/**
 * Weather Fragment
 * This fragment loads all data from server for weather at a particular location.
 * Either Lat/Lon coordinates or zipcode.
 * This fragment shows the current conditions and a semi weekly forecast up to 5 days.
 * It also displays the sunrise and sunset times at the given location.
 */
public class WeatherFragment extends Fragment {
    private static final String PREFS_LOC = "location_pref";

    private OnFragmentInteractionListener mListener;
    private double mLat = 47.25; //hardcoded Tacoma for reference
    private double mLon= -122.44; //hard coded Tacoma for reference
    private LatLng mLocation;
    private boolean searchByZip;
    private boolean prefsSearch;
    private String mZip;
    /*
    Display Variables Below
     */
    private TextView mCCText;
    private TextView mCCTemp;
    private TextView mCCCity;
    private ImageView mCCIcon;
    private TextView mDayOneText;
    private TextView mDayTwoText;
    private TextView mDayThreeText;
    private TextView mDayFourText;
    private TextView mDayFiveText;
    private ImageView mDayOneImage;
    private ImageView mDayTwoImage;
    private ImageView mDayThreeImage;
    private ImageView mDayFourImage;
    private ImageView mDayFiveImage;
    private TextView mDayOneDesc;
    private TextView mDayTwoDesc;
    private TextView mDayThreeDesc;
    private TextView mDayFourDesc;
    private TextView mDayFiveDesc;
    private TextView mDayOneTemp;
    private TextView mDayTwoTemp;
    private TextView mDayThreeTemp;
    private TextView mDayFourTemp;
    private TextView mDayFiveTemp;
    private TextView mSunset;
    private TextView mSunrise;
    private ProgressBar mProgress;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);

        //Get location from shared prefs
        SharedPreferences mPrefs = getActivity().getSharedPreferences(PREFS_LOC, MODE_PRIVATE);
        float latTemp = mPrefs.getFloat("lat", 0);
        float lonTemp = mPrefs.getFloat("lon", 0);
        prefsSearch = mPrefs.getBoolean("searchZip", false);
        mLocation = new LatLng(latTemp, lonTemp);
        if((mLocation.latitude >= 0 && mLocation.latitude <= 1)
                || (mLocation.longitude >=0 && mLocation.longitude <= 1)) {
            mLocation = new LatLng(mLat, mLon);  //hard code to tacoma if lat lon are wrong
        } else {
            Log.e("Got Coordinates from shared prefs:", mLocation.toString()); //Else use location from shared prefs
        }

        // Checks SDK version and sets policy if higher SDK
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*
        Weather Fragment Variables for displaying data
         */
        mCCText = v.findViewById(R.id.ccText);
        mCCCity = v.findViewById(R.id.ccCity);
        mCCTemp = v.findViewById(R.id.ccTemp);
        mCCIcon = v.findViewById(R.id.ccImage);
        mDayOneText = v.findViewById(R.id.weatherDayOne);
        mDayTwoText = v.findViewById(R.id.weatherDayTwo);
        mDayThreeText = v.findViewById(R.id.weatherDayThree);
        mDayFourText = v.findViewById(R.id.weatherDayFour);
        mDayFiveText = v.findViewById(R.id.weatherDayFive);
        mDayOneImage = v.findViewById(R.id.dayOneImage);
        mDayTwoImage = v.findViewById(R.id.dayTwoImage);
        mDayThreeImage = v.findViewById(R.id.dayThreeImage);
        mDayFourImage = v.findViewById(R.id.dayFourImage);
        mDayFiveImage = v.findViewById(R.id.dayFiveImage);
        mDayOneDesc = v.findViewById(R.id.dayOneText);
        mDayTwoDesc = v.findViewById(R.id.dayTwoText);
        mDayThreeDesc = v.findViewById(R.id.dayThreeText);
        mDayFourDesc = v.findViewById(R.id.dayFourText);
        mDayFiveDesc = v.findViewById(R.id.dayFiveText);
        mDayOneTemp = v.findViewById(R.id.dayOneTemp);
        mDayTwoTemp = v.findViewById(R.id.dayTwoTemp);
        mDayThreeTemp = v.findViewById(R.id.dayThreeTemp);
        mDayFourTemp = v.findViewById(R.id.dayFourTemp);
        mDayFiveTemp = v.findViewById(R.id.dayFiveTemp);
        mSunrise = v.findViewById(R.id.weatherSunriseTime);
        mSunset = v.findViewById(R .id.weatherSunsetTime);
        mZip = new String();
        mProgress = v.findViewById(R.id.weatherProgress);

        //Get the zipcode from arguments if passed by selecting change location via zipcode
        Bundle bundle = getArguments();
        if(bundle == null){
            searchByZip = false;
            mZip = null;
        } else {
            mZip = bundle.getString("zip");
            searchByZip = true;
        }

        //If zipcode was the last thing saved to shared prefs instead of lat lon, use zipcode to load on app run instead.
        if(prefsSearch) {
            mZip = mPrefs.getString("zipCode", "");
            searchByZip = true;
        }

        //Calls ASYNC functions to get weather data
        onLoad();

        //Button handlers
        Button changeLocationButton = v.findViewById(R.id.buttonWeatherChangeLocation);
        changeLocationButton.setOnClickListener(view -> mListener.onChangeLocationAction());

        return v;
    }

    private void onLoad() {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(getContext().getString(R.string.ep_weather))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            //If zipcode search
            if(searchByZip || prefsSearch) {
                Log.e("Searching weather by zip code:", "True");
                msg.put("zip", mZip);
                msg.put("searchByZip", true);
            } else { //If lat lon search
                Log.e("Searching weather by lat/long", "True");
                msg.put("searchByZip", false);
                msg.put("lat", mLocation.latitude);
                msg.put("lon", mLocation.longitude);
            }

        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onCancelled(this::handleErrorsInTask)
                .onPreExecute(this::handleWeatherProgress)
                .onPostExecute(this::handleWeatherOnPost)
                .build().execute();
    }

    private void handleWeatherProgress() {
    }




    /**
     * Handles the onPost of ASYNC for weather data calls.
     * Sets all texts and images based on weather data.
     * @param result JSON response
     */
    private void handleWeatherOnPost(String result) {
        try {
            JSONObject weatherObj = new JSONObject(result);
            /*
            Result Format:
            currentConditions: weather, icon, temp, city
            sun: sunrise, sunset
            fiveDay (JSONArray) for each element: day, temp, icon, text
             */
            //Current conditions
            String ccText = weatherObj.getJSONObject("currentConditions").getString("weather");
            String ccIcon = weatherObj.getJSONObject("currentConditions").getString("icon");
            String ccTemp = weatherObj.getJSONObject("currentConditions").getString("temp");
            String ccCity = weatherObj.getJSONObject("currentConditions").getString("city");
            mCCText.setText(ccText);
            mCCTemp.setText(ccTemp + "° F");
            mCCCity.setText(ccCity);
            String urlIcon = new String("http://openweathermap.org/img/w/" + ccIcon + ".png");
            URL url = new URL(urlIcon);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mCCIcon.setImageBitmap(bmp);

            //Sunrise/Sunset Data
            String sunrise = weatherObj.getJSONObject("sun").getString("sunrise");
            String sunset = weatherObj.getJSONObject("sun").getString("sunset");
            mSunrise.setText(sunrise);
            mSunset.setText(sunset);

            //4-5 Day forecast
            JSONArray weeklyForecastJSON = weatherObj.getJSONArray("fiveDay");
            String[][] weeklyForecast = new String[weeklyForecastJSON.length()][4];
            for(int i=0; i < weeklyForecastJSON.length(); i++) { //populate array list with json objects
                String day = weeklyForecastJSON.getJSONObject(i).getString("day");
                String temp =weeklyForecastJSON.getJSONObject(i).getString("temp");
                String icon =weeklyForecastJSON.getJSONObject(i).getString("icon");
                String text =weeklyForecastJSON.getJSONObject(i).getString("text");
                weeklyForecast[i][0] = day;
                weeklyForecast[i][1] = temp;
                weeklyForecast[i][2] = icon;
                weeklyForecast[i][3] = text;
            }
            for(int j=0; j < weeklyForecast.length; j++) {
                String imgName = weeklyForecast[j][2];
                String tempIcon = new String("http://openweathermap.org/img/w/" + imgName + ".png");
                URL tempURL = new URL(tempIcon);
                Bitmap tempBMP = BitmapFactory.decodeStream(tempURL.openConnection().getInputStream());
                switch(j){
                    case 0:
                        mDayOneText.setText(convertDayString(weeklyForecast[j][0]));
                        mDayOneImage.setImageBitmap(tempBMP);
                        mDayOneDesc.setText(weeklyForecast[j][3]);
                        mDayOneTemp.setText(weeklyForecast[j][1] + "° F");
                        break;
                        case 1:
                            mDayTwoText.setText(convertDayString(weeklyForecast[j][0]));
                            mDayTwoImage.setImageBitmap(tempBMP);
                            mDayTwoDesc.setText(weeklyForecast[j][3]);
                            mDayTwoTemp.setText(weeklyForecast[j][1]+ "° F");
                            break;
                    case 2:
                        mDayThreeText.setText(convertDayString(weeklyForecast[j][0]));
                        mDayThreeImage.setImageBitmap(tempBMP);
                        mDayThreeDesc.setText(weeklyForecast[j][3]);
                        mDayThreeTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    case 3:
                        mDayFourText.setText(convertDayString(weeklyForecast[j][0]));
                        mDayFourImage.setImageBitmap(tempBMP);
                        mDayFourDesc.setText(weeklyForecast[j][3]);
                        mDayFourTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    case 4:
                        mDayFiveText.setText(convertDayString(weeklyForecast[j][0]));
                        mDayFiveImage.setImageBitmap(tempBMP);
                        mDayFiveDesc.setText(weeklyForecast[j][3]);
                        mDayFiveTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    default:
                        break;
                }
            }
            mProgress.setVisibility(View.GONE);
        } catch(JSONException e) {
            Log.wtf("JSON ERROR:", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Simple day converter. Since the output from API will be in 3 letter format, formats the day to a full day name and returns the string.
     * @param day 3 day letter code i.e Mon, Tue, Wed...
     * @return String of day i.e Monday, Tuesday, Wednesday...
     */
    private String convertDayString(String day) {
        String result = new String();
        switch(day) {
            case "Mon":
                result = "Monday";
                break;
            case "Tue":
                result = "Tuesday";
                break;
            case "Thu":
                result = "Thursday";
                break;
            case "Wed":
                result = "Wednesday";
                break;
            case "Fri":
                result = "Friday";
                break;
            case "Sat":
                result = "Saturday";
                break;
            case "Sun":
                result = "Sunday";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Interface to be implemeneted by host activity
     */
    public interface OnFragmentInteractionListener {
        void onChangeLocationAction();
        void onLogout();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /*
    Handles attach of fragment to activity. Makes sure activity implements interface
     */
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

    /*
    Detaches mListener onDetach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
