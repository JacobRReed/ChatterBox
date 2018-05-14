package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.Context;
import android.support.annotation.NonNull;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.app.Fragment;
//import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import group10.tcss450.uw.edu.chatterbox.HomeActivity;
import group10.tcss450.uw.edu.chatterbox.MainActivity;
import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatContactsFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatListFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatMessageFragment;
import group10.tcss450.uw.edu.chatterbox.model.Credentials;

public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.ViewHolder> {

    private List<Contact> mContacts;
    private Context mContext;
    private int mPosition;
    private FragmentManager mFrag;



    public ChatContactsAdapter(List<Contact> contacts, Context context, FragmentManager fragmentManager) {
        mContacts = contacts;
//        Log.e("contactSize", "" + mContacts.size());
//        mRemovalPerson = null;
        mContext = context;
        mFrag = fragmentManager;

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
        Contact contact = mContacts.get(position);
        Button friendButton = holder.friendButton;
        friendButton.setText(contact.getName());
        friendButton.setOnClickListener(v -> {
            mPosition = position;
//            mListener.onAddFriendToChatAction();

            FragmentTransaction fragTrans = mFrag.beginTransaction();
            fragTrans.remove(new ChatContactsFragment());
            fragTrans.replace(R.id.ChatContactsRecyclerLayout, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();


        });

    }




    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button friendButton;
        public ViewHolder(View itemView) {
            super(itemView);
            friendButton = itemView.findViewById(R.id.ChatContactsContactButton);
            String friendName = friendButton.getText().toString();
            Log.e("friendName", friendName);
        }
    }


    public interface OnFragmentInteractionListener {
        void onMessageFriendAction();
    }

}


