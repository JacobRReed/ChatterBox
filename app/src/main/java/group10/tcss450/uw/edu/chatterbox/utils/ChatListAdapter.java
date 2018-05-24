package group10.tcss450.uw.edu.chatterbox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Chat> mContacts;
    private Context mContext;
    private int mPosition;
    private FragmentManager mFrag;
    private View mView;
    private String mSendUrlMakeChat;
    private String mSendUrlGetChat;
    private String mSendUrlRemoveMember;
    private String mSendUrlAddFriendToChat;

    private String mSendUrlMakeAndAddToChat;

    private String mUsername;
    private SharedPreferences mPrefs;

    private String myCurrentChatId;
    private String myCurrentMemId;
    private String doNothing; //please delete this later

    private View myContactView;

    private Runnable mSwap;
    private Runnable mSwap2;


    //didnt change mContact to mChat
    public ChatListAdapter(List<Chat> contacts, Context context,
                               Runnable swap, Runnable swap2, View theView,
                               String theUsername, SharedPreferences thePrefs) {
        mContacts = contacts;
        mContext = context;
        mView = theView;
        mUsername = theUsername;
        mPrefs = thePrefs;
        mSwap = swap;
        mSwap2 = swap2;
    }






    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        myContactView = inflater.inflate(R.layout.chat_list_recycler_item, parent, false);


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

        mSendUrlRemoveMember = new Uri.Builder() .scheme("https")
                .appendPath(mContext.getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath("removeMember")
                .build()
                .toString();
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(myContactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat contact = mContacts.get(position); //Did change the name of this either

        Button chatButton = holder.chatButton;
        Button removeChatButton = holder.removeChatButton;
        chatButton.setText(contact.getName());
        chatButton.setOnClickListener(v -> {
            mPosition = position;
            askForChatId(contact.getName());
            String chatid = chatButton.getText().toString().split(" ")[0];
            mPrefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID",chatid).commit();
            mSwap.run();
        });
        removeChatButton.setOnClickListener(v -> {
            String chatid = chatButton.getText().toString().split(" ")[0];
            Log.d("Fuck you forever", chatButton.getText().toString().split(" ")[0]);
//            String username = mUsername;
//            JSONObject messageJson = new JSONObject();

            SharedPreferences prefs =
                    mContext.getSharedPreferences( mContext.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            mPrefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID",chatid).commit();
            Log.d("Fuck you forever 22222222", mPrefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0"));
            mSwap2.run();

//            SharedPreferences prefs =
//                    getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//
//            String dogChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");
//            try {
//                messageJson.put(mContext.getString(R.string.keys_json_chat_id), Integer.parseInt(chatid));
//                messageJson.put(mContext.getString(R.string.keys_json_username), username);
//                //Log.d("Fuck Try Put: ", "Success on putting new friend into chat. chatID: " + currChatID + " CurrentFriendAdded: " + theCheckedFriends.get(i).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//                //Log.d("Fuck Try Put: ", "Failure on putting a temp chat. chatID: " + mCurrentChatId + " CurrentFriendAdded: " + theCheckedFriends.get(i));
//            }
//            new SendPostAsyncTask.Builder(mSendUrlRemoveMember, messageJson)
//                    .onPostExecute(this::endOfAddFriendsToChat)
//                    .onCancelled(this::handleError)
//                    .build().execute();

        });

    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfAddFriendsToChat(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(mContext.getString(R.string.keys_json_success)).toString()
                    .equals(mContext.getString(R.string.keys_json_success_value_true))) {

                Log.d("Can I say Fuck Ya?", "I guess so...");
                // not sure what this needs to be
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Can I say Fuck No?", "I guess not...");
        }
    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button chatButton;
        public Button removeChatButton;
        public ViewHolder(View itemView) {
            super(itemView);
            removeChatButton = itemView.findViewById(R.id.chatListRemoveButton);
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


