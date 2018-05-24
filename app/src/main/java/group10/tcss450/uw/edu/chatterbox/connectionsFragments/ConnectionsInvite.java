package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsInvite extends Fragment {
    private Button mContactsButton;
    private static final int CONTACT_PICKER_RESULT = 1001;
    private EditText mEmailAddr;
    private String mEmailStr;
    private View mView;

    public ConnectionsInvite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_connections_invite, container, false);
        mContactsButton = mView.findViewById(R.id.inviteContactButton);
        mContactsButton.setOnClickListener(this::onContactClick);
        mEmailAddr = mView.findViewById(R.id.inviteEmail);
        String mEmailStr = null;
        Button inviteButton = mView.findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(this::onInviteClick);

        return mView;
    }

    /**
     * Handles invite button on click. Send email if valid
     * @param view Current view
     */
    private void onInviteClick(View view) {
        mEmailStr = mEmailAddr.getText().toString();
        boolean sendEmail = false;
        //Check Email entry
        if(mEmailStr != null && mEmailStr.contains("@") && mEmailStr.length() >=2) {
            sendEmail = true;
        } else {
            Log.wtf("Invalid Email:", mEmailStr);
            mEmailAddr.setError("Invalid Email Address!");
        }

        if(sendEmail) {
            //Send email
            SharedPreferences prefs =
                    getContext().getSharedPreferences(
                            getContext().getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String username = prefs.getString(getContext().getString(R.string.keys_prefs_username_local), "");

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getContext().getString(R.string.ep_base_url))
                    .appendPath(getContext().getString(R.string.ep_invite))
                    .build();
            //build the JSONObject
            JSONObject msg = new JSONObject();
            try{
                msg.put("user", username);
                msg.put("emailAddress", mEmailStr);
            } catch (JSONException e) {
                Log.wtf("Error creating JSON object for existing connections:", e);
            }

            //instantiate and execute the AsyncTask.
            //Feel free to add a handler for onPreExecution so that a progress bar
            //is displayed or maybe disable buttons. You would need a method in
            //LoginFragment to perform this.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleInviteOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    /**
     * Handles the on post results of email invite
     * @param result
     */
    private void handleInviteOnPost(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String success = obj.getString("result");
            if(success.equals("true")) {
                Toast.makeText(mView.getContext(), "Invitation Sent!", Toast.LENGTH_SHORT).show();
                Log.e("Sent invite email!", "");
            } else {
                Toast.makeText(mView.getContext(), "Email Failed to Send!", Toast.LENGTH_SHORT).show();
                Log.e("Invite Email Error", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    /**
     * Opens the contacts activity from phone for user to select a contact from
     * @param v
     */
    private void onContactClick(View v) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI);

        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * Handles the contacts activity response
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            contactPicked(data);
        } else {
            Log.e("CONTACT PICKER", "Failed to pick contact");
        }
    }

    /**
     * Handles contact picked from contact picker activity
     * @param data
     */
    private void contactPicked(Intent data) {
       Uri contactUri = data.getData();
       String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
       Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
       if(cursor != null && cursor.moveToFirst()) {
           int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
           Cursor emailCur = getActivity().getContentResolver().query(
                   ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                   ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                   projection, null);
           String email = cursor.getString(numberIndex);
            //After retrieving email from contacts, set the textedit to that email so they can send invite
           mEmailAddr.setText(email);
       }
    }


}
