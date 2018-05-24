package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.ListenManager;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatMessageFragment extends android.support.v4.app.Fragment {

    private String mUsername;
    private String mSendUrl;
    private TextView mOutputTextView; private ListenManager mListenManager;
    private String mCurrentChatId;

    public ChatMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);
        mOutputTextView = v.findViewById(R.id.chatOutputTextView);

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
        Log.d("Fuck test for messages get: the current chat id is: ", currentChatId);
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", currentChatId) // this need to be change to a unique chat
                .build();
        Log.d("Fuck test for messages get: the current chat id is: bbbbbbbb ", retrieve.toString());
        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            Log.d("Fuck test inside if prefs.contains():", "Seems to be checking right");
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp),"0"))
                    .setExceptionHandler(this::handleError) .setDelay(1000)
                    .build();
        } else {
            Log.d("Fuck test inside else statement:", "should still work right???");
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

        String dogChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), dogChatId); // change this
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
        Log.d("Fuck test publish progress:", "before if statement: " + messages.toString());
        if(messages.has(getString(R.string.keys_json_messages))) {
            Log.d("Fuck test publish progress:", "made it inside if statement");
            try {
                Log.d("Fuck test publish progress inside try:", "inside try!");
                JSONArray jMessages =
                        messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for(int i =0; i < jMessages.length(); i++) {
                    msgs[i] = jMessages.getJSONObject(i).getString("message");
                }
                Log.e("Messages Array:" , Arrays.deepToString((msgs)));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Fuck test for messages get:", "something went wrong!");
                return;
            }
            getActivity().runOnUiThread(() -> {
                mOutputTextView.setText("");
                for (String msg : msgs) {
                    Log.d("Fuck test Loop append messages:", "Message is: " + msgs);
                    mOutputTextView.append(msg);
                    mOutputTextView.append(System.lineSeparator());
                }
            });
        }
    }
}

