package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatContactsFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatListFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatMessageFragment;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Chat> mContacts;
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
    private String doNothing; //please delete this later


    //didnt change mContact to mChat
    public ChatListAdapter(List<Chat> contacts, Context context,
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

        View contactView = inflater.inflate(R.layout.chat_list_recycler_item, parent, false);


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

//        mSendUrlAddFriendToChat = new Uri.Builder() .scheme("https")
//                .appendPath(mContext.getString(R.string.ep_base_url))
//                .appendPath("chats")
//                .appendPath("addFriendToChat")
//                .build()
//                .toString();

//        mSendUrlMakeAndAddToChat = new Uri.Builder() .scheme("https")
//                .appendPath(mContext.getString(R.string.ep_base_url))
//                .appendPath("chats")
//                .appendPath("MakeAndAddToChat")
//                .build()
//                .toString();

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat contact = mContacts.get(position); //Did change the name of this either
        Button chatButton = holder.chatButton;
        chatButton.setText(contact.getName());
        chatButton.setOnClickListener(v -> {
            mPosition = position;
////            String chatName = "Chat Room: " + mUsername.toUpperCase() + " and " + contact.getName().toUpperCase();
////            MakeAndAddToChat(chatName, mUsername, contact.getName());
            Log.d("#@#@#@#@#@#@#@#@#@#@#@#@", contact.getName());
            askForChatId(contact.getName());
//
            FragmentTransaction fragTrans = mFrag.beginTransaction();
//            fragTrans.remove(new ChatListFragment());
            fragTrans.replace(R.id.ChatListRecyclerLayout, new ChatMessageFragment());
            fragTrans.addToBackStack(null);
            fragTrans.commit();

        });

    }




    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button chatButton;
        public ViewHolder(View itemView) {
            super(itemView);
            chatButton = itemView.findViewById(R.id.chatListChatButton);
            String chatName = chatButton.getText().toString();
            Log.e("chatName", chatName);
        }
    }


    public interface OnFragmentInteractionListener {
        void onMessageFriendAction();
    }


//    //-------------
//
//    private void MakeAndAddToChat(String theChatname, String theUsername, String theFriendU) {
//        JSONObject messageJson = new JSONObject();
//
////        Log.d("the chat and mem id is: ", " " + theChatId + " + " + theMemId);
//        try {
//            messageJson.put("chatname", theChatname);
//            messageJson.put("username", theUsername);
//            messageJson.put("friendUsername", theFriendU);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new SendPostAsyncTask.Builder(mSendUrlMakeAndAddToChat, messageJson)
//                .onPostExecute(this::endOfSendMsgTask)
//                .onCancelled(this::handleError)
//                .build().execute();
//    }
//
//
//
////------------
//
    private void askForChatId(String theName) {
        Log.e("blah blah blah: ", "chicken farts " + theName);
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
        Log.e("-=-=-=-=-=--=- the results: ", result);
        String temp = result.replace("{\"name\":{\"chatid\":", "");
        temp = temp.replace("}}", "");
        Log.e("#######the results: ", temp);

        mPrefs.edit()
                .putString("THIS_IS_MY_CURRENT_CHAT_ID", temp)
                .apply();
        myCurrentChatId = temp;
    }

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


