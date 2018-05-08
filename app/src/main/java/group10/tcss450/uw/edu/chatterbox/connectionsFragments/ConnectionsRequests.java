package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterRequestsIncoming;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterRequestsOutgoing;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsRequests extends Fragment {
    private RecyclerView mRecyclerViewIncoming;
    private RecyclerView.Adapter mAdapterIncoming;
    private RecyclerView.LayoutManager mLayoutManagerIncoming;
    public ArrayList<Contact> mContactsIncoming;

    private RecyclerView mRecyclerViewOutgoing;
    private RecyclerView.Adapter mAdapterOutgoing;
    public ArrayList<Contact> mContactsOutgoing;
    private RecyclerView.LayoutManager mLayoutManagerOutgoing;

    public ConnectionsRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connections_requests, container, false);

        //Get username
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(getString(R.string.keys_prefs_username_local), "");

        //Incoming requests inits
        mRecyclerViewIncoming = v.findViewById(R.id.requestsRecyclerIncoming);
        mRecyclerViewIncoming.setHasFixedSize(true);
        mLayoutManagerIncoming = new LinearLayoutManager(getContext());
        onRequestConnectionsIncLoad(username);
        mContactsIncoming = new ArrayList<>();
        mAdapterIncoming = new ContactsAdapterRequestsIncoming(mContactsIncoming, getContext());
        mRecyclerViewIncoming.setAdapter(mAdapterIncoming);
        mRecyclerViewIncoming.setLayoutManager(mLayoutManagerIncoming);

        //Outgoing requests inits
        mRecyclerViewOutgoing = v.findViewById(R.id.requestsRecyclerOutgoing);
        mRecyclerViewOutgoing.setHasFixedSize(true);
        mLayoutManagerOutgoing = new LinearLayoutManager(getContext());
        onRequestConnectionsOutLoad(username);
        mContactsOutgoing = new ArrayList<>();
        mAdapterOutgoing = new ContactsAdapterRequestsOutgoing(mContactsOutgoing, getContext());
        mRecyclerViewOutgoing.setAdapter(mAdapterOutgoing);
        mRecyclerViewOutgoing.setLayoutManager(mLayoutManagerOutgoing);

        return v;
    }

    /**
     * Call ASYNC for retrieving outgoing requests list
     * @param username
     */
    private void onRequestConnectionsOutLoad(String username) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(getString(R.string.ep_requests_out))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("username", username);
            msg.put("cancel", false);
            msg.put("friend", "null");
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleReqOutOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles ASYNC for outgoing request lists
     * @param result
     */
    private void handleReqOutOnPost(String result) {
        String users = result.replace("friends","");
        users = users.replaceAll("[^a-zA-Z,]", "");
        String[] userList = users.split(",");
        String[] userListFinal = new String[userList.length];
        for(int i = 0; i < userList.length; i++) {
            userListFinal[i] = userList[i].replaceAll("[^a-zA-Z]", "");
            userListFinal[i] = userList[i].replaceAll("outgoingFriends", "");
        }

        for(String s : userListFinal) {
            mContactsOutgoing.add(new Contact(s));
        }
        mAdapterOutgoing = new ContactsAdapterRequestsOutgoing(mContactsOutgoing, getContext());
        mRecyclerViewOutgoing.setAdapter(mAdapterOutgoing);
    }

    /**
     * Call ASYNC for retrieving incoming requests list
     * @param username
     */
    private void onRequestConnectionsIncLoad(String username) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(getString(R.string.ep_requests_inc))
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
                .onPostExecute(this::handleReqIncOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleReqIncOnPost(String result) {
        String users = result.replace("friends","");
        users = users.replaceAll("[^a-zA-Z,]", "");
        String[] userList = users.split(",");
        String[] userListFinal = new String[userList.length];
        for(int i = 0; i < userList.length; i++) {
            userListFinal[i] = userList[i].replaceAll("[^a-zA-Z]", "");
            userListFinal[i] = userList[i].replaceAll("incomingFriends", "");
        }

        for(String s : userListFinal) {
            mContactsIncoming.add(new Contact(s));
        }
        mAdapterIncoming = new ContactsAdapterRequestsIncoming(mContactsIncoming, getContext());
        mRecyclerViewIncoming.setAdapter(mAdapterIncoming);
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }
}
