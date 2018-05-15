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
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
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
    private View mView;
    private String mSendUrlMakeChat;
    private String mSendUrlGetChat;
    private String mSendUrlAddFriendToChat;

    private String mSendUrlMakeAndAddToChat;

    private String mUsername;
    private SharedPreferences mPrefs;

    private String myCurrentChatId;
    private String myCurrentMemId;



    public ChatContactsAdapter(List<Contact> contacts, Context context,
                               FragmentManager fragmentManager, View theView,
                               String theUsername, SharedPreferences thePrefs) {
        mContacts = contacts;
//        Log.e("contactSize", "" + mContacts.size());
//        mRemovalPerson = null;
        mContext = context;
        mFrag = fragmentManager;
        mView = theView;
        mUsername = theUsername;
        mPrefs = thePrefs;
    }






    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.chat_contacts_recycler_item, parent, false);


        mSendUrlMakeChat = new Uri.Builder() .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("makeChat")
                .build()
                .toString();

        mSendUrlGetChat = new Uri.Builder() .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("getChat")
                .build()
                .toString();

        mSendUrlAddFriendToChat = new Uri.Builder() .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("addFriendToChat")
                .build()
                .toString();

        mSendUrlMakeAndAddToChat = new Uri.Builder() .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("MakeAndAddToChat")
                .build()
                .toString();

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
            String chatName = "Chat Room: " + mUsername.toUpperCase() + " and " + contact.getName().toUpperCase();
            MakeAndAddToChat(chatName, mUsername, contact.getName());
            askForChatId(chatName);


            FragmentTransaction fragTrans = mFrag.beginTransaction();
            fragTrans.remove(mFrag.findFragmentById(R.id.ChatContactsRecyclerLayout));
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


    //-------------

    private void MakeAndAddToChat(String theChatname, String theUsername, String theFriendU) {
        JSONObject messageJson = new JSONObject();

//        Log.d("the chat and mem id is: ", " " + theChatId + " + " + theMemId);
        try {
            messageJson.put("chatname", theChatname);
            messageJson.put("username", theUsername);
            messageJson.put("friendUsername", theFriendU);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrlMakeAndAddToChat, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }


    //---------------



//    private void makeChat(String theName) {
//        JSONObject messageJson = new JSONObject();
//        try {
//            messageJson.put(mContext.getString(R.string.keys_json_chat_name), theName);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new SendPostAsyncTask.Builder(mSendUrlMakeChat, messageJson)
//                .onPostExecute(this::endOfSendMsgTask)
//                .onCancelled(this::handleError)
//                .build().execute();
//    }

//    -------------

//    private void addFriendToChat(String theChatId, String theMemId) {
//        JSONObject messageJson = new JSONObject();
//
//        Log.d("the chat and mem id is: ", " " + theChatId + " + " + theMemId);
//        try {
//            messageJson.put("chatid", theChatId);
//            messageJson.put("memid", theMemId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new SendPostAsyncTask.Builder(mSendUrlAddFriendToChat, messageJson)
//                .onPostExecute(this::endOfSendMsgTask)
//                .onCancelled(this::handleError)
//                .build().execute();
//    }



//------------

    private void askForChatId(String theName) {
        Log.e("blah blah blah: ", "chicken farts");
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("getChat")
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try{
            msg.put("name", theName);
        } catch (JSONException e) {
            Log.wtf("Error creating JSON object for existing connections:", e);
        }

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::getChatId)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void getChatId(String result) {
//        Log.e("the results: ", result);
        String temp = result.replace("{\"name\":{\"chatid\":", "");
        temp = temp.replace("}}", "");
//        Log.e("the results: ", temp);

        mPrefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID", temp).apply();
        myCurrentChatId = temp;
    }

//    ------------

//    private void askForMemId(String theName) {
////        Log.e("blah blah blah: ", "chicken farts");
//        //build the web service URL
//        Uri uri = new Uri.Builder()
//                .scheme("https")
//                .appendPath(mContext.getString(R.string.ep_base_url))
//                .appendPath("chats")
//                .appendPath("getMemberID")
//                .build();
//        //build the JSONObject
//        JSONObject msg = new JSONObject();
//        try{
//            msg.put("name", theName);
//        } catch (JSONException e) {
//            Log.wtf("Error creating JSON object for existing connections:", e);
//        }
//
//        //instantiate and execute the AsyncTask.
//        //Feel free to add a handler for onPreExecution so that a progress bar
//        //is displayed or maybe disable buttons. You would need a method in
//        //LoginFragment to perform this.
//        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                .onPostExecute(this::getMemId)
//                .onCancelled(this::handleErrorsInTask)
//                .build().execute();
//    }
//
//    private void getMemId(String result) {
////        Log.e("the results: ", result);
//        String temp = result.replace("{\"name\":{\"memberid\":", "");
//        temp = temp.replace("}}", "");
//        Log.e("the mem id is: ", temp);
//        mPrefs.edit().putString(mContext.getString(R.string.keys_prefs_current_memid), temp).apply();
//        myCurrentMemId = String.valueOf(temp);
//        Log.e("the mem id is (will it show): ", myCurrentChatId);
//    }

//    -----------------

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }



    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(mContext.getString(R.string.keys_json_success)).toString()
                    .equals(mContext.getString(R.string.keys_json_success_value_true))) {
                ((EditText) mView.findViewById(R.id.chatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}


