package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.app.FragmentManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.ChatContactsAdapter;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterExisting;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatContactsFragment extends android.support.v4.app.Fragment {

//    private ChatContactsFragment.OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Contact> mContacts;

    public ChatContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_contacts, container, false);

        //        mRecyclerView = v.findViewById(R.id.connectionsExistingRecycler);
        mRecyclerView = v.findViewById(R.id.ChatContactsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        //Get username
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        onExistingConnectionsLoad(username); //FIX THIS @TODO
        mContacts = new ArrayList<>();
//        FragmentManager fg;
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        mAdapter = new ChatContactsAdapter(mContacts, this.getContext(), fm);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(null);




//        Button makeNewChatButton = v.findViewById(R.id.ChatContactsFriendButton);
//        makeNewChatButton.setOnClickListener(view -> mListener.onAddFriendToChatAction());

        return v;
    }


    private void onExistingConnectionsLoad(String username) {
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
                .onPostExecute(this::handleExistingConnectionOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleExistingConnectionOnPost(String result) {
        String users = result.replace("friends","");
        users = users.replaceAll("[^a-zA-Z,]", "");
        String[] userList = users.split(",");
        String[] userListFinal = new String[userList.length];
        for(int i = 0; i < userList.length; i++) {
            userListFinal[i] = userList[i].replaceAll("[^a-zA-Z]", "");
        }

        for(String s : userListFinal) {
            mContacts.add(new Contact(s));
            Log.e("contacts", mContacts.toString());
        }

        android.support.v4.app.FragmentManager transaction = getChildFragmentManager();
        mAdapter = new ChatContactsAdapter(mContacts, this.getContext(), transaction); //I changed this
        mRecyclerView.setAdapter(mAdapter);

    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

//    public interface OnFragmentInteractionListener {
//        void onAddFriendToChatAction();
//        void onLogout();
//
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof ChatListFragment.OnFragmentInteractionListener) {
//            mListener = (ChatContactsFragment.OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


}