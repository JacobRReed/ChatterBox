package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.WeatherFragment;
import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsExisting;
import group10.tcss450.uw.edu.chatterbox.model.Credentials;
import group10.tcss450.uw.edu.chatterbox.utils.Chat;
import group10.tcss450.uw.edu.chatterbox.utils.ChatListAdapter;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterExisting;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatListFragment extends android.support.v4.app.Fragment {


    private ChatListFragment.OnFragmentInteractionListener mListener;
//    private ConstraintLayout mContainer;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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
//        mContainer = v.findViewById(R.id.connectionsFragmentContainer);


        FloatingActionButton makeNewChatButton = v.findViewById(R.id.ChatListMakeNewChatActionButton);
        makeNewChatButton.setOnClickListener(view -> mListener.onMakeNewChatAction());

        //------------

        mRecyclerView = v.findViewById(R.id.ChatListRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        //Get username
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mUsername = username;
        //---
        onExistingConnectionsLoad(username); //FIX THIS @TODO
        //-----
        mContacts = new ArrayList<>();
        mChats = new ArrayList<>();
//        FragmentManager fg;
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();

        mAdapter = new ChatListAdapter(mChats, this.getContext(), fm, getView(), mUsername, prefs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(null);


        return v;
    }




    public interface OnFragmentInteractionListener {
        void onMakeNewChatAction();
        void onLogout();

    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //---------

    private void onExistingConnectionsLoad(String username) {
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
                .onPostExecute(this::handleExistingConnectionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleExistingConnectionOnPost(String result) {
        Log.e("#@#@#@# the results are: ", result.toString());
        String temp = result.toString();
        temp = temp.replace("{\"name\":[","");
        temp = temp.replace("]}","");
        temp = temp.replace("\"","");
        String[] chatNames = temp.split(",");
        Log.e("#@#@#@#@ the results are: ", chatNames[0]);

        for(String s : chatNames) {
            mChats.add(new Chat(s));
            Log.e("chats", mChats.toString());
        }

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);


        android.support.v4.app.FragmentManager transaction = getChildFragmentManager();
        mAdapter = new ChatListAdapter(mChats, this.getContext(), transaction, getView(), mUsername, prefs); //I changed this
        mRecyclerView.setAdapter(mAdapter);

    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }
}
