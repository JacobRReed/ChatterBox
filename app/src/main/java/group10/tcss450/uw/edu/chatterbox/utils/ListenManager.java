package group10.tcss450.uw.edu.chatterbox.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Thread manager that periodically checks the web server for updates. Building the manager
 * requires the fully formed URL and Consumer that processes the results.
 *
 * When the Consumer processing the results needs to manipulate any UI elements, this must be
 * performed on the UI Thread. See the following:
 *
 * <pre>
 *      runOnUiThread(() -> {
 *          // statements that manipulate UI Views
 *          // do not include non-UI related statements.
 *      }
 * </pre>
 *
 *  Do not include statements that do not need to manipulate UI Views inside of the runOnUiThread
 *  argument.
 *
 * @author Charles Bryan
 * @version 4/15/2018
 */
public class ListenManager {

    private final String mURL;
    private final Consumer<JSONObject> mActionToTake;
    private final Consumer<Exception> mActionToTakeOnError;
    private final int mDelay;
    private String mDate;

    private ScheduledThreadPoolExecutor mPool;
    private ScheduledFuture mThread;

    /**
     * Helper class for building ListenManagers.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        //Required Parameters
        private final String mURL;
        private final Consumer<JSONObject> mActionToTake;

        //Optional Parameters
        private int mSleepTime = 500;
        private Consumer<Exception> mActionToTakeOnError = e -> {};
        private String mDate = "1970-01-01 00:00:01.00000";

        /**
         * Constructs a new Builder with a delay of 500 ms.
         *
         * When the Consumer processing the results needs to manipulate any UI elements, this must be
         * performed on the UI Thread. See ListenManager class documentation for more information.
         *
         * @param url the fully-formed url of the web service this task will connect to
         * @param actionToTake the Consumer processing the results
         */
        public Builder(String url, Consumer<JSONObject> actionToTake) {
            mURL = url;
            mActionToTake = actionToTake;
        }

        /**
         * Set the delay amount between calls to the web service. The default delay is 500 ms.
         * @param val the delay amount between calls to the web service
         * @return
         */
        public Builder setDelay(final int val) {
            mSleepTime = val;
            return this;
        }

        /**
         * Set the action to perform during exceptional handling. Note, not ALL possible
         * exceptions are handled by this consumer.
         *
         * @param val the action to perform during exceptional handling
         * @return
         */
        public Builder setExceptionHandler(final Consumer<Exception> val) {
            mActionToTakeOnError = val;
            return this;
        }

        public Builder setTimeStamp(final String val) {
            mDate = val;
            return this;
        }

        /**
         * Constructs a ListenManager with the current attributes.
         *
         * @return a ListenManager with the current attributes.
         */
        public ListenManager build() {
            return new ListenManager(this);
        }

    }

    /**
     * Construct a ListenManager internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private ListenManager(final Builder builder) {
        mURL = builder.mURL;
        mActionToTake = builder.mActionToTake;
        mDelay = builder.mSleepTime;
        mActionToTakeOnError = builder.mActionToTakeOnError;
        mDate = builder.mDate;
        mPool = new ScheduledThreadPoolExecutor(5);
    }

    /**
     * Starts the worker thread to ask for updates every delay milliseconds.
     */
    public void startListening() {
        mThread = mPool.scheduleAtFixedRate(new ListenForMessages(),
                0,
                mDelay,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Stops listening for new messages.
     * @return the most recent timestamp
     */
    public String stopListening() {
        mThread.cancel(true);
        return mDate;
    }

    /**
     * Does the work!
     */
    private class ListenForMessages implements Runnable {

        @Override
        public void run() {
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;

            //go out and ask for new messages
            response = new StringBuilder();
            try {
                String getURL = mURL;
                //add the timestamp to the URL
                getURL += "&after=" + mDate;

                URL urlObject = new URL(getURL);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response.append(s);
                }

                JSONObject messages = new JSONObject(response.toString());

                //here is where we "publish" the message that we received.
                mActionToTake.accept(messages);

                //get and store the last date.
                JSONArray msgs = messages.getJSONArray("messages");
                if (msgs.length() > 0) {
                    JSONObject mostRecent = msgs.getJSONObject(msgs.length() - 1);
                    String timestamp = mostRecent.get("timestamp").toString();
                    mDate = timestamp;
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                mActionToTakeOnError.accept(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}


