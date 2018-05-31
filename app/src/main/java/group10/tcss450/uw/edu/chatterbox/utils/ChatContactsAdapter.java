package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import group10.tcss450.uw.edu.chatterbox.R;


public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.ViewHolder> {

    private List<Contact> mContacts;
    private ArrayList<String> mCheckedFriends;



    public ChatContactsAdapter(List<Contact> contacts, ArrayList<String> theCheckedFriends) {
        mContacts = contacts;
        mCheckedFriends = theCheckedFriends;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.chat_contacts_recycler_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // variables needed
        Contact contact = mContacts.get(position);
        TextView friendtext = holder.friendText;
        CheckBox checkBox = holder.checkBox;
        // Checkbox setup
        checkBox.setOnClickListener(v -> {
            if(checkBox.isChecked()) {
                mCheckedFriends.add(contact.getName());
            } else {
                if(!checkBox.isChecked()) {
                    for(int i = 0; i < mCheckedFriends.size(); i++) {
                        if(contact.getName() == mCheckedFriends.get(i)) {
                            mCheckedFriends.remove(i);
                        }
                    }
                }
            }
        });

        // friend text setup
        friendtext.setText(contact.getName());

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendText; // change to friendText
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            friendText = itemView.findViewById(R.id.ChatContactsContactText);
            checkBox = itemView.findViewById(R.id.chatContactsCheckBox);
//            String friendName = friendButton.getText().toString();
//            Log.e("friendName", friendName);
        }
    }


    public interface OnFragmentInteractionListener {
        void onMessageFriendAction();
    }



}


