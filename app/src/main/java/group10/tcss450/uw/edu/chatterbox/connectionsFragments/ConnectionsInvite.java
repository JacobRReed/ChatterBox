package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    public ConnectionsInvite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections_invite, container, false);
        mContactsButton = v.findViewById(R.id.inviteContactButton);
        mContactsButton.setOnClickListener(this::onContactClick);
        mEmailAddr = v.findViewById(R.id.inviteEmail);
        String mEmailStr = null;
        Button inviteButton = v.findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(this::onInviteClick);

        return v;
    }

    private void onInviteClick(View view) {
        mEmailStr = mEmailAddr.getText().toString();
        boolean sendEmail = false;
        //Check Email entry
        if(mEmailStr != null && mEmailStr.contains("@") && mEmailStr.length() >=2) {
            Log.wtf("Sending invite to email:", mEmailStr);
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
        if(result.contains("true")) {
            Toast.makeText(getContext(), "Invitation Sent!", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getContext(), "Invitation failed to send, server error", Toast.LENGTH_LONG);
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
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * Handles the contacts activity response
     * @param requestCode
     * @param resultCode
     * @param data
     * @TODO currently doesn't return selected user
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String email = "";
                    try {
                        Uri result = data.getData();
                        Log.v("", "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything email
                        cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

                        // let's just get the first email
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);
                            Log.v("", "Got email: " + email);
                        } else {
                            Log.w("", "No results");
                        }
                    } catch (Exception e) {
                        Log.e("", "Failed to get email data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        EditText emailEntry = getActivity().findViewById(R.id.inviteEmail);
                        emailEntry.setText(email);
                        if (email.length() == 0) {
                            Toast.makeText(getContext(), "No email found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
            }

        } else {
            // gracefully handle failure
            Log.w("", "Warning: activity result not ok");
        }


    }

}
