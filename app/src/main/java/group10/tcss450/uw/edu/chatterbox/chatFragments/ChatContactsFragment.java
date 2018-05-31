package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.ChatContactsAdapter;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatContactsFragment extends android.support.v4.app.Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Contact> mContacts;
    private String mUsername;
    private ChatContactsFragment.OnFragmentInteractionListener mListener;
    private ArrayList<String> mCheckedFriends = new ArrayList<>();

    private TextView mNoFriends;
    private Button mMakeNewChatButton;

    public ChatContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_contacts, container, false);

        mRecyclerView = v.findViewById(R.id.ChatContactsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mNoFriends = v.findViewById(R.id.chatContactNoFriendsText);
        mMakeNewChatButton = v.findViewById(R.id.chatCreateNewChatButton);
        mMakeNewChatButton.setOnClickListener(view -> {


            mListener.onCreateNewChatButtonPressed(mCheckedFriends);

        });

        //Get username from shared prefs
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mUsername = username;

        onChatContactLoad(username); //FIX THIS @TODO
        mContacts = new ArrayList<>();

        /*
        Handles fragment runnable
         */
        Runnable swap = () -> {
            FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.homeFragmentContainer, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();
        };

        int count = 0;

        if (mContacts.size() > 0) {
            for (int i = 0; i < mContacts.size(); i++) {
                if (mContacts.get(i).getName().length() != 0) {
                    count ++;
                }
            }
        }

        mAdapter = new ChatContactsAdapter(mContacts, this.getContext(), swap, getView(), mUsername, prefs, mCheckedFriends);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(null);


        if (count > 0) {
            mMakeNewChatButton.setVisibility(View.VISIBLE);
            mNoFriends.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoFriends.setVisibility(View.VISIBLE);
            mMakeNewChatButton.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    /**
     * This method loads the list of all contacts.
     * @param username passing in the username
     */
    private void onChatContactLoad(String username) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_existing))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("username", username);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleChatContactOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles chat connection on post
     * @param result
     */
    private void handleChatContactOnPost(String result) {
        String users = result.replace("friends","");
        users = users.replaceAll("[^a-zA-Z,]", "");
        String[] userList = users.split(",");
        String[] userListFinal = new String[userList.length];
        for(int i = 0; i < userList.length; i++) {
            userListFinal[i] = userList[i].replaceAll("[^a-zA-Z]", "");
        }

        for(String s : userList) {
            mContacts.add(new Contact(s));
            Log.e("contacts", mContacts.toString());
        }

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        /*
        Handles fragment runnable
         */
        Runnable swap = () -> {
            FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.homeFragmentContainer, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();
        };

        int count = 0;

        if (mContacts.size() > 0) {
            for (int i = 0; i < mContacts.size(); i++) {
                if (mContacts.get(i).getName().length() != 0) {
                    count ++;
                }
            }
        }

        mAdapter = new ChatContactsAdapter(mContacts, this.getContext(), swap, getView(), mUsername, prefs, mCheckedFriends); //I changed this
        mRecyclerView.setAdapter(mAdapter);

        if (count > 0) {
            mMakeNewChatButton.setVisibility(View.VISIBLE);
            mNoFriends.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoFriends.setVisibility(View.VISIBLE);
            mMakeNewChatButton.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Handles on attach of fragment and activity
     * @param context Context of attach
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatContactsFragment.OnFragmentInteractionListener) {
            mListener = (ChatContactsFragment.OnFragmentInteractionListener) context;
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
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Interface to be implemented by host activities
     */
    public interface OnFragmentInteractionListener {
        void onCreateNewChatButtonPressed(ArrayList<String> theCheckedFriends);

    }

}
