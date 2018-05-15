package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

//        LinearLayout layout = (LinearLayout) v.findViewById(R.id.frameLayout6);
//        layout.removeAllViewsInLayout();

        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);
        mOutputTextView = v.findViewById(R.id.chatOutputTextView);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

//        FragmentManager fragMan = getFragmentManager();
//        FragmentTransaction fragTrans = fragMan.beginTransaction();

//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.ChatListRecyclerLayout)).commit();


//        LinearLayout layout = (LinearLayout) .findViewById(R.id.layoutDeviceList);
//        layout.removeAllViewsInLayout();
//        fragTrans.
//        Fragment temp = fragMan.p;

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mSendUrl = new Uri.Builder() .scheme("https")
                .appendPath(getString(R.string.ep_base_url)) .appendPath(getString(R.string.ep_send_message)) .build()
                .toString();

        //--

        String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");
        Log.d("the current chat id is: ", currentChatId);
        //-------------
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", currentChatId) // this need to be change to a unique chat
                .build();
        //-------------

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

//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.ChatListRecyclerLayout)).commit();

//        SharedPreferences prefs =
//                getActivity().getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//
//
//
//        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
//            throw new IllegalStateException("No username in prefs!");
//        }
//        mUsername = prefs.getString(getString(R.string.keys_prefs_username_local), "");
//        mSendUrl = new Uri.Builder() .scheme("https")
//                .appendPath(getString(R.string.ep_base_url)) .appendPath(getString(R.string.ep_send_message)) .build()
//                .toString();
//
//        //--
//
//        String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "");
//        Log.d("the current chat id is: ", currentChatId);
//        //-------------
//        Uri retrieve = new Uri.Builder()
//                .scheme("https")
//                .appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_get_message))
//                .appendQueryParameter("chatId", currentChatId) // this need to be change to a unique chat
//                .build();
//        //-------------
//
//        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
//            //ignore all of the seen messages. You may want to store these messages locally
//            mListenManager = new ListenManager.Builder(retrieve.toString(),
//                    this::publishProgress)
//                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp),"0"))
//                    .setExceptionHandler(this::handleError) .setDelay(1000)
//                    .build();
//        } else {
////no record of a saved timestamp. must be a first time login
//            mListenManager = new ListenManager.Builder(retrieve.toString(),
//                    this::publishProgress)
//                    .setExceptionHandler(this::handleError)
//                    .setDelay(1000)
//                    .build();
//        }

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

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

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


    //-------

    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }

    private void publishProgress(JSONObject messages) {
        final String[] msgs;
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages =
                        messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = username + ":" + userMessage; }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            getActivity().runOnUiThread(() -> {
                for (String msg : msgs) {
                    mOutputTextView.append(msg);
                    mOutputTextView.append(System.lineSeparator());
                }
            });
        }
    }

}

