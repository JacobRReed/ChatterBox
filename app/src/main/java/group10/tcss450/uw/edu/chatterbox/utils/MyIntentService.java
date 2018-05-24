package group10.tcss450.uw.edu.chatterbox.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group10.tcss450.uw.edu.chatterbox.MainActivity;
import group10.tcss450.uw.edu.chatterbox.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    public static final String RECEIVED_UPDATE = "new show from ChatterBox!";
    //60 seconds - 1 minute is the minimum...
    private static final int POLL_INTERVAL = 60_000;
    private static final String TAG = "MyIntentService";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Performing the service");
        }
    }

    private boolean checkWebservice(boolean isInForeground) {
        //check a webservice in the background...
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath("ep_base_url")
                .appendPath("check_web_service")
                .build();

        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {

            URL urlObject = new URL(retrieve.toString());
            urlConnection = (HttpURLConnection) urlObject.openConnection();
//            Log.d("");
            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s;
            while ((s = buffer.readLine()) != null) {
                response.append(s);
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        if (isInForeground) {
            Intent i = new Intent(RECEIVED_UPDATE);
            //add bundle to send the response to any receivers
            //i.putExtra(getString(R.string.keys_extra_results), response.toString());
            sendBroadcast(i);
        } else {
            buildNotification(response.toString());
        }
        return true;
    }

    private void buildNotification(String s) {

        //IMPORT V4 not V7
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_app_icon)
                        .setContentTitle("Sender Name")
                        .setContentText("Message will goes here");

        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(this, MainActivity.class);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Bundle bundle = new Bundle();
        //notifyIntent.putExtra(getString(R.string.keys_extra_results), s);

        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Creates the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this,
                0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Puts the PendingIntent into the notification builder
        mBuilder.setContentIntent(notifyPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }


}
