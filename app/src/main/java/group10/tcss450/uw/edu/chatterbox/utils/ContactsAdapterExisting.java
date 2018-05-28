package group10.tcss450.uw.edu.chatterbox.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;

import group10.tcss450.uw.edu.chatterbox.R;

public class ContactsAdapterExisting extends
        RecyclerView.Adapter<ContactsAdapterExisting.ViewHolder> {

    private List<Contact> mContacts;
    private Context mContext;
    private int mPosition;
    private String mRemovalPerson;
    private ViewHolder mViewHolder;

    public ContactsAdapterExisting(List<Contact> contacts, Context context) {
        mContacts = contacts;
        mRemovalPerson = null;
        mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.connections_existing_recycler_item, parent, false);

        // Return a new holder instance
        mViewHolder = new ViewHolder(contactView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(contact.getName());
        //Remove friend button
        Button remove = holder.itemView.findViewById(R.id.connectionsExistingRemove);
        remove.setOnClickListener(v -> {
            mPosition = position;
            mRemovalPerson = contact.getName();
            onRemoveFriend(contact.getName());
        });
    }

    private void onRemoveFriend(String friend) {
        SharedPreferences prefs =
                mContext.getSharedPreferences(
                        mContext.getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String username = prefs.getString(mContext.getString(R.string.keys_prefs_username_local), "");

        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath(mContext.getString(R.string.ep_existing))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("friend", friend);
            msg.put("username", username);
            msg.put("remove", true);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRemoveOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleRemoveOnPost(String result) {
        if(result.contains("true")) {
            //Remove the contact from list
            List<Contact> temp = new ArrayList<>();
            temp.addAll(mContacts);
            for(Contact c : mContacts) {
                if(c.getName().equals(mRemovalPerson)) {
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
        public Button removeButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.connectionsExistingContactName);
            String friendName = nameTextView.getText().toString();
            removeButton = itemView.findViewById(R.id.connectionsExistingRemove);
        }
    }
}