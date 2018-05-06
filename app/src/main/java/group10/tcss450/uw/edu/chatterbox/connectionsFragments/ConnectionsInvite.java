package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import group10.tcss450.uw.edu.chatterbox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsInvite extends Fragment {
    private Button mContactsButton;
    private static final int CONTACT_PICKER_RESULT = 1001;

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
        Button inviteButton = v.findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Send the invite via email using web call
            }
        });

        return v;
    }

    private void onContactClick(View v) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

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
