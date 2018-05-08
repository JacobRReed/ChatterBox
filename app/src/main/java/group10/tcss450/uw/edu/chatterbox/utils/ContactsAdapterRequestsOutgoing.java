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

public class ContactsAdapterRequestsOutgoing extends RecyclerView.Adapter<ContactsAdapterRequestsOutgoing.ViewHolder>{
    private List<Contact> mContacts;
    private Context mContext;
    private int mPosition;
    private String mUsername;
    private String mUserRequested;

    public ContactsAdapterRequestsOutgoing(List<Contact> contacts, Context context) {
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
        View contactView = inflater.inflate(R.layout.connections_requests_out_recycler_item, parent, false);

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
        TextView textView = holder.nameTextView;
        textView.setText(contact.getName());

        Button cancel = holder.cancelButton;
        cancel.setOnClickListener(v -> {
            mPosition = position;
            mUserRequested = contact.getName();
            //Call async to remove request
            onCancelRequest(contact.getName());
        });
    }

    /**
     * Calls ASYNC for cancelling request
     * @param user username to cancel
     */
    private void onCancelRequest(String user) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .path(mContext.getString(R.string.ep_requests_out_cancel))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("friend", user);
            msg.put("username", mUsername);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleCancelRequestOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Handles onpost of ASYNC cancelling request outgoing
     * @param result
     */
    private void handleCancelRequestOnPost(String result) {
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
            Log.wtf("Cancel of friend request fail.", result);
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

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button cancelButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.requestsOutName);
            cancelButton = itemView.findViewById(R.id.requestsOutCancel);
        }
    }
}
