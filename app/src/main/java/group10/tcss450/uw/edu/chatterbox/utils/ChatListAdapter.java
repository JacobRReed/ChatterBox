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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group10.tcss450.uw.edu.chatterbox.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Chat> mContacts;
    private Context mChat;
    private View mView;
    private View myContactView;
    private String mUsername;
    private SharedPreferences mPrefs;
    private Runnable mSwap;
    private Runnable mSwap2;


    //didnt change mContact to mChat
    public ChatListAdapter(List<Chat> contacts, Context context,
                               Runnable swap, Runnable swap2, View theView,
                               String theUsername, SharedPreferences thePrefs) {
        mContacts = contacts;
        mChat = context;
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
            askForChatId(contact.getName());
            String chatid = chatButton.getText().toString().split(" ")[0];
            mPrefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID",chatid).commit();
            mSwap.run();
        });
        removeChatButton.setOnClickListener(v -> {
            String chatid = chatButton.getText().toString().split(" ")[0];

            mPrefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID",chatid).commit();
            mSwap2.run();
        });
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

    private void askForChatId(String theName) {
        Log.e("blah blah blah: ", "chicken farts " + theName);
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mChat.getString(R.string.ep_base_url))
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
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public interface OnFragmentInteractionListener {
        void onMessageFriendAction();
    }

}


