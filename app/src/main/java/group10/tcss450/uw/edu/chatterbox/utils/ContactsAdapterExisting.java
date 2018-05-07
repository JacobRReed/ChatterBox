package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.ClipData;
import android.content.Context;
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

import java.util.List;

import group10.tcss450.uw.edu.chatterbox.R;

public class ContactsAdapterExisting extends
        RecyclerView.Adapter<ContactsAdapterExisting.ViewHolder> implements View.OnClickListener {

    private List<Contact> mContacts;

    public ContactsAdapterExisting(List<Contact> contacts) {
        mContacts = contacts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.connections_existing_recycler_item, parent, false);

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
        Button remove = holder.itemView.findViewById(R.id.connectionsExistingRemove);
        remove.setOnClickListener(v -> {
            Log.wtf("Removing contact:", contact.getName());
        });
        Button message = holder.itemView.findViewById(R.id.connectionExistingMessage);
        message.setOnClickListener(v -> {
            Log.wtf("Starting message with:", contact.getName());
        });


    }



    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public void onClick(View v) {

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button removeButton;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.connectionsExistingContactName);
            String friendName = nameTextView.getText().toString();
            messageButton = itemView.findViewById(R.id.connectionExistingMessage);
            removeButton = itemView.findViewById(R.id.connectionsExistingRemove);

        }

    }

}