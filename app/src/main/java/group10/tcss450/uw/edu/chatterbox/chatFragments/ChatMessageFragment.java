package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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
        mOutputTextView = v.findViewById(R.id.chatOutputTextView);
        mTextScroller = v.findViewById(R.id.chatScroller);

        SharedPreferences fontPrefs = getActivity().getApplicationContext().getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPrefs.getInt(PREFS_FONT, 0);
        switch(fontChoice) {
            case 1:
                Log.e("Chat Font Size:", "14");
                mOutputTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                break;
            case 2:
                Log.e("Chat Font Size:", "16");
                mOutputTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                break;
            case 3:
                Log.e("Chat Font Size:", "18");
                mOutputTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                break;
            default:
                Log.e("Chat Font Size:", "16");
                mOutputTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
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
                .appendPath(getString(R.string.ep_base_url)) .appendPath(getString(R.string.ep_send_message)) .build()
                .toString();

        /**
         * ASYNC Call
         */
        String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");
        mCurrentChatId = currentChatId;
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
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
        final String[] msgs;
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages =
                        messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for(int i =0; i < jMessages.length(); i++) {
                    msgs[i] = jMessages.getJSONObject(i).getString("message");
                }
                Log.e("Messages Array:" , Arrays.deepToString((msgs)));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            getActivity().runOnUiThread(() -> {
                mOutputTextView.setText("");
                for (String msg : msgs) {
                    mOutputTextView.append(msg);
                    mOutputTextView.append(System.lineSeparator());
                    mTextScroller.fullScroll(ScrollView.FOCUS_DOWN);
                }

            });
        }
    }
}

