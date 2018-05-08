package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import java.util.List;

import group10.tcss450.uw.edu.chatterbox.R;

public class ContactsAdapterRequestsIncoming extends RecyclerView.Adapter<ContactsAdapterRequestsIncoming.ViewHolder>{

    private List<Contact> mContacts;
    private Context mContext;
    private int mPosition;
    private String mUsername;
    private String mUserRequested;

    public ContactsAdapterRequestsIncoming(List<Contact> contacts, Context context) {
        mContacts = contacts;
        mPosition = 0;
        mContext = context;
        mUserRequested = null;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.connections_requests_recycler_item, parent, false);

        SharedPreferences prefs =
                mContext.getSharedPreferences(
                        mContext.getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mUsername = prefs.getString(mContext.getString(R.string.keys_prefs_username_local), "");

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(contact.getName());
        Button decline = holder.itemView.findViewById(R.id.requestsRecyclerDecline);
        Button accept = holder.itemView.findViewById(R.id.requestsRecycleAccept);
        accept.setOnClickListener(v -> {
            mPosition = position;
            mUserRequested = contact.getName();
            //Call async method to accept request
            onFriendAcceptOrDecline(contact.getName(), true);
        });
        decline.setOnClickListener(v -> {
           mPosition = position;
           mUserRequested = contact.getName();
           //Call async method to decline request
            onFriendAcceptOrDecline(contact.getName(), false);
        });
    }

    /**
     * Accept the friend request ASYNC Task
     * @param friend
     * @param removeOrAdd True for add, false to decline
     */
    private void onFriendAcceptOrDecline(String friend, boolean removeOrAdd) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(mContext.getString(R.string.ep_requests_acceptdecline))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("friend", friend);
            msg.put("username", mUsername);
            if(removeOrAdd){
                //Add user
                msg.put("accept", true);
            }else {
                //Decline user
                msg.put("accept", false);
            }

        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAddDeclineOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles ASYNC on post
     * @param result
     */
    private void handleAddDeclineOnPost(String result) {
        //If result is true then the user needs to be deleted from
        //the list regardless if they accepted or declined
        //because they will now show up under existing connections tab
        if(result.contains("true")) {
            Log.wtf("Result is", "true");
            //Remove the contact from list
            List<Contact> temp = new ArrayList<>();
            temp.addAll(mContacts);
            for(Contact c : mContacts) {
                if(c.getName().equals(mUserRequested)) {
                    temp.remove(c);
                }
            }

            //Refresh ui
            refreshEvents(temp);
        } else {
            Log.wtf("Removal of friend fail.", result);
        }
    }

    /**
     * Refreshes the UI with new contacts list
     * @param contacts
     */
    private void refreshEvents(List<Contact> contacts) {
        mContacts.clear();
        mContacts.addAll(contacts);
        notifyDataSetChanged();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button acceptButton;
        public Button declineButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.requestsRecyclerName);
            acceptButton = itemView.findViewById(R.id.requestsRecycleAccept);
            declineButton = itemView.findViewById(R.id.requestsRecyclerDecline);
        }
    }
}