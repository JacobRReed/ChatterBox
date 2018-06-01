package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.ListenManager;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatMessageFragment extends android.support.v4.app.Fragment {

    private String mUsername;
    private String mSendUrl;
    private TextView mOutputTextView; private ListenManager mListenManager;
    private String mCurrentChatId;
    private ScrollView mTextScroller;
    private static final String PREFS_FONT = "font_pref";
    private LinearLayout mMessageLinear;
    private List<TextView> mChats;
    private int mFontSize;
    private String mUserID;

    public ChatMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_message, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);
        //mOutputTextView = v.findViewById(R.id.chatOutputTextView);
        mTextScroller = v.findViewById(R.id.chatScroller);
        mMessageLinear = v.findViewById(R.id.messageLinear);
        mChats = new ArrayList<>();

        SharedPreferences fontPrefs = getActivity().getApplicationContext().getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPrefs.getInt(PREFS_FONT, 0);
        switch(fontChoice) {
            case 1:
                mFontSize = 14;
                break;
            case 2:
                mFontSize = 16;
                break;
            case 3:
                mFontSize = 18;
                break;
            default:
                mFontSize = 16;
                break;

        }

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mSendUrl = new Uri.Builder() .scheme("https")
                .path(getString(R.string.ep_messages)) .appendPath(getString(R.string.ep_send_message)) .build()
                .toString();

        /**
         * Get user id from username for chat manipulation
         */
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(getContext().getString(R.string.ep_memberid))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("username", mUsername);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for userid retrieval:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleIDOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

        /**
         * ASYNC Call
         */
        String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");
        mCurrentChatId = currentChatId;
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .path(getString(R.string.ep_messages))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", currentChatId) // this need to be change to a unique chat
                .build();
        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp),"0"))
                    .setExceptionHandler(this::handleError) .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        }
    }

    /**
     * ON POST ASYNC handler for getting userid
     * @param s JSON String
     */
    private void handleIDOnPost(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            int id = obj.getJSONObject("id").getInt("memberid");
            mUserID = Integer.toString(id);
            Log.e("Got user id for username:" + mUsername, ", ID: " + mUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }

    /**
     * Handles send message butt
     * @param theButton Button for listener
     */
    private void sendMessage(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
                .getText().toString();

        SharedPreferences prefs =
                getActivity().getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        String prefsCurrentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), prefsCurrentChatId); // change this
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * Handles errors for chat
     * @param msg
     */
    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                ((EditText) getView().findViewById(R.id.chatInputEditText))
                        .setText("");
                mTextScroller.fullScroll(ScrollView.FOCUS_DOWN); //Auto scrolls text to bottom
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles errors for ASYNC
     * @param e
     */
    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }

    /**
     * Handles publish progress of messages
     * @param messages
     */
    private void publishProgress(JSONObject messages) {
        final String[][] msgs;
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages =
                        messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()][3];
                for(int i =0; i < jMessages.length(); i++) {
                    msgs[i][0] = jMessages.getJSONObject(i).getString("message");
                    msgs[i][1] = Integer.toString(jMessages.getJSONObject(i).getInt("memberid"));
                    msgs[i][2] = jMessages.getJSONObject(i).getString("timestamp");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            try {
                getActivity().runOnUiThread(() -> {
                    /*
                    Sort array by time
                     */
                    Arrays.sort(msgs, new StringArrayComparator());

                    /**
                     * Generate chat messages textviews with bubbles and add to list to hold them
                     */
                    mChats.clear();
                    mMessageLinear.removeAllViewsInLayout();
                    if (msgs.length > 0) {
                        for (int i = 0; i < msgs.length; i++) { //Loop through messages and create text views
                            /*
                            msg[i][0] = message
                            msg[i][1] = memberid
                            msg[i][2] = timestamp "2018-05-24T18:36:23.182Z" <-- format
                             */
                            if(msgs[i][1].equals(mUserID)) { //If the user id of this message is the current user
                                TextView txt1 = new TextView(getContext());
                                txt1.setBackground(getResources().getDrawable(R.drawable.chat_bubble_user));
                                txt1.setText(msgs[i][0]); //Message
                                txt1.setTextColor(getResources().getColor(R.color.chat_font_color));
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(120, 20, 0, 20);
                                params.gravity = Gravity.RIGHT;
                                txt1.setLayoutParams(params);
                                txt1.setGravity(Gravity.RIGHT);
                                txt1.setPadding(5, 20, 5, 20);
                                txt1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSize);
                                mChats.add(txt1);
                            } else { //Other users
                                TextView txt2 = new TextView(getContext());
                                txt2.setBackground(getResources().getDrawable(R.drawable.chat_bubble_from));
                                txt2.setTextColor(getResources().getColor(R.color.chat_font_color));
                                txt2.setText(msgs[i][0]);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 20, 20, 20);
                                params.gravity = Gravity.LEFT;
                                txt2.setGravity(Gravity.LEFT);
                                txt2.setLayoutParams(params);
                                txt2.setPadding(5, 20, 5, 20);
                                txt2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSize);
                                mChats.add(txt2);
                            }


                            //Add all generated chat textviews into the linear layout inside of scroll view
                            for (int j = 0; j < mChats.size(); j++) {
                                mMessageLinear.removeView(mChats.get(j));
                                mMessageLinear.addView(mChats.get(j));
                            }
                        }
                    }

                });
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Message out of bounds", e.toString());
            }
        }
    }

    /**
     * Get the actual timestamp from timestamp string SQL
     * @param s Timestamp string
     * @return Timestamp object
     */
    private Timestamp getTimestamp(String s) {
         /*
                        Format timestamp string into actual timestamp for comparison
                         */
        String tempTime = s;
        tempTime = tempTime.replace("T", " ");
        tempTime = tempTime.replace("Z", "");
        Timestamp timeStamp = Timestamp.valueOf(tempTime);
        return timeStamp;
    }

    public class StringArrayComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            Timestamp t1 = getTimestamp(o1[2]);
            Timestamp t2 = getTimestamp(o2[2]);
            if(t1.before(t2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}

