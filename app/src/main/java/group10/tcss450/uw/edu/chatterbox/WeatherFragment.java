package group10.tcss450.uw.edu.chatterbox;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private String mLat = "47.25"; //hardcoded fix later @TODO
    private String mLon= "-122.44"; //hard coded fix later @TODO

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

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Weather");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

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
        mSunset = v.findViewById(R.id.weatherSunsetTime);

        onLoad();

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
            msg.put("lat", mLat);
            msg.put("lon", mLon);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleWeatherOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //Handles weather on post
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
            //Sunrise/sunset stuff
            String sunrise = weatherObj.getJSONObject("sun").getString("sunrise");
            String sunset = weatherObj.getJSONObject("sun").getString("sunset");
            Log.wtf("Sunset:", sunset);
            Log.wtf("Sunrise:", sunrise);
            mSunrise.setText(sunrise);
            mSunset.setText(sunset);
            //Weekly forecast
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
                        mDayOneText.setText(weeklyForecast[j][0]);
                        mDayOneImage.setImageBitmap(tempBMP);
                        mDayOneDesc.setText(weeklyForecast[j][3]);
                        mDayOneTemp.setText(weeklyForecast[j][1] + "° F");
                        break;
                        case 1:
                            mDayTwoText.setText(weeklyForecast[j][0]);
                            mDayTwoImage.setImageBitmap(tempBMP);
                            mDayTwoDesc.setText(weeklyForecast[j][3]);
                            mDayTwoTemp.setText(weeklyForecast[j][1]+ "° F");
                            break;
                    case 2:
                        mDayThreeText.setText(weeklyForecast[j][0]);
                        mDayThreeImage.setImageBitmap(tempBMP);
                        mDayThreeDesc.setText(weeklyForecast[j][3]);
                        mDayThreeTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    case 3:
                        mDayFourText.setText(weeklyForecast[j][0]);
                        mDayFourImage.setImageBitmap(tempBMP);
                        mDayFourDesc.setText(weeklyForecast[j][3]);
                        mDayFourTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    case 4:
                        mDayFiveText.setText(weeklyForecast[j][0]);
                        mDayFiveImage.setImageBitmap(tempBMP);
                        mDayFiveDesc.setText(weeklyForecast[j][3]);
                        mDayFiveTemp.setText(weeklyForecast[j][1]+ "° F");
                        break;
                    default:
                        break;
                }
            }


        } catch(JSONException e) {
            Log.wtf("JSON ERROR:", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


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
