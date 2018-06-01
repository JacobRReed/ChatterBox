package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.Chat;
import group10.tcss450.uw.edu.chatterbox.utils.ChatListAdapter;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatListFragment extends android.support.v4.app.Fragment {
    private ChatListFragment.OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mSendUrlRemoveMember;
    public ArrayList<Contact> mContacts;
    public ArrayList<Chat> mChats;
    private String mUsername;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat List");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        mSendUrlRemoveMember = new Uri.Builder() .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("removeMember")
                .build()
                .toString();

        FloatingActionButton makeNewChatButton = v.findViewById(R.id.ChatListMakeNewChatActionButton);
        makeNewChatButton.setOnClickListener(view -> {
            mListener.onMakeNewChatAction();
        });

        mRecyclerView = v.findViewById(R.id.ChatListRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        //Get username from shared prefs
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mUsername = username;

        //Handles chat list ASYNC
        onChatListLoad(username); //FIX THIS @TODO

        mContacts = new ArrayList<>();
        mChats = new ArrayList<>();

        /**
         * Handles fragment swapping runnable
         */
        Runnable swap = () -> {
            FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.homeFragmentContainer, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();
        };

        Runnable swap2 = () -> {

            String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");
            JSONObject messageJson = new JSONObject();

            try {
                messageJson.put(getString(R.string.keys_json_chat_id), Integer.parseInt(currentChatId));
                messageJson.put(getString(R.string.keys_json_username), mUsername);
                } catch (JSONException e) {
                e.printStackTrace();
                }
            new SendPostAsyncTask.Builder(mSendUrlRemoveMember, messageJson)
                    .onPostExecute(this::endOfAddFriendsToChat)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        };

        mAdapter = new ChatListAdapter(mChats, this.getContext(), swap, swap2, getView(), mUsername, prefs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(null);

        return v;
    }

    /**
     * Interface to be implemented by host activities
     */
    public interface OnFragmentInteractionListener {
        void onMakeNewChatAction();
        void onLogout();

    }

    /**
     * Handles on attach of fragment and activity
     * @param context Context of attach
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatListFragment.OnFragmentInteractionListener) {
            mListener = (ChatListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Detaches mListener onDetach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * ASYNC method for Chat List Load
     * @param username username to load chats for
     */
    private void onChatListLoad(String username) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("getAllChats")
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("name", username);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleChatListOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles ASYNC call onPost
     * @param result JSON result
     */
    private void handleChatListOnPost(String result) {
        Log.e("#@#@#@# the results are: ", result.toString());
        String temp = result.toString();
        temp = temp.replace("{\"name\":[","");
        temp = temp.replace("]}","");
        temp = temp.replace("\"","");
        String[] chatNames = temp.split(",");

        for(String s : chatNames) {
            mChats.add(new Chat(s));
        }

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        android.support.v4.app.FragmentManager transaction = getChildFragmentManager();

        /*
        Handles fragment swap runnable
         */
        Runnable swap = () -> {
            FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.homeFragmentContainer, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();
        };

        /*
        Handles fragment swap runnable
         */
        Runnable swap2 = () -> {
            String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");
            JSONObject messageJson = new JSONObject();

            try {
                messageJson.put(getString(R.string.keys_json_chat_id), currentChatId);
                messageJson.put(getString(R.string.keys_json_username), mUsername);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(mSendUrlRemoveMember, messageJson)
                    .onPostExecute(this::endOfAddFriendsToChat)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        };

        int count = 0;

        if (mChats.size() > 0) {
            for (int i = 0; i < mChats.size(); i++) {
                if (mChats.get(i).getName().length() != 0) {
                    count ++;
                }
            }
        }

        mAdapter = new ChatListAdapter(mChats, this.getContext(), swap, swap2, getView(), mUsername, prefs); //I changed this
        mRecyclerView.setAdapter(mAdapter);

        if (count > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfAddFriendsToChat(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                //TO DO REFRESH METHOD @TODO

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
