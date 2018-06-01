package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterAdd;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsAdd extends Fragment implements RadioGroup.OnCheckedChangeListener{
    private EditText textOne;
    private EditText textTwo;
    private Button searchButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Contact> mContacts;
    private int mOption;
    private ProgressBar mProgress;

    public ConnectionsAdd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connections_add, container, false);
        RadioGroup searchMethod = v.findViewById(R.id.addRadios);
        mOption = 1;
        searchMethod.setOnCheckedChangeListener(this::onCheckedChanged);
        textOne = v.findViewById(R.id.addTextBoxOne);
        textTwo = v.findViewById(R.id.addTextBox2);

        //Progress bar
        mProgress = v.findViewById(R.id.connectionsAddProgress);

        //Recycler view for result
        mRecyclerView = v.findViewById(R.id.addRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        //Generate contacts
        mContacts = new ArrayList<>();
        mAdapter = new ContactsAdapterAdd(mContacts, this.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        searchButton = v.findViewById(R.id.addSearchButton);
        searchButton.setOnClickListener(this::searchMethod);
        return v;
    }

    /**
     * Handles search button clicks
     * @param view
     */
    private void searchMethod(View view) {
        Log.wtf("Searching using method ", Integer.toString(mOption));
        switch(mOption) {
            case 1:
                //Search email
                String email = textOne.getText().toString();
                email = email.toLowerCase();
                if(email.length() <3 || !email.contains("@")) {
                    textOne.setError("Invalid email address!");
                } else {
                    //build the web service URL
                    Uri uri = new Uri.Builder()
                            .scheme("https")
                            .path(getString(R.string.ep_search))
                            .build();
                    //build the JSONObject
                    JSONObject msg = new JSONObject();
                    try {
                        msg.put("option", 1);
                        msg.put("email", email);
                    } catch (JSONException e) {
                        Log.wtf("Error creating JSON object for existing connections:", e);
                    }

                    //instantiate and execute the AsyncTask.
                    //Feel free to add a handler for onPreExecution so that a progress bar
                    //is displayed or maybe disable buttons. You would need a method in
                    //LoginFragment to perform this.
                    new SendPostAsyncTask.Builder(uri.toString(), msg)
                            .onPostExecute(this::handleSearchOnPost)
                            .onCancelled(this::handleErrorsInTask)
                            .build().execute();
                }
                break;
            case 2:
                //Search username
                String username = textOne.getText().toString();
                username = username.toLowerCase();
                if(username.length() < 3) {
                    textOne.setError("Nickname to search must be at least 3 characters!");
                } else {
                    //build the web service URL
                    Uri uri2 = new Uri.Builder()
                            .scheme("https")
                            .path(getString(R.string.ep_search))
                            .build();
                    //build the JSONObject
                    JSONObject msg2 = new JSONObject();
                    try {
                        msg2.put("option", 2);
                        msg2.put("username", username);
                    } catch (JSONException e) {
                        Log.wtf("Error creating JSON object for existing connections:", e);
                    }

                    //instantiate and execute the AsyncTask.
                    //Feel free to add a handler for onPreExecution so that a progress bar
                    //is displayed or maybe disable buttons. You would need a method in
                    //LoginFragment to perform this.
                    new SendPostAsyncTask.Builder(uri2.toString(), msg2)
                            .onPostExecute(this::handleSearchOnPost)
                            .onProgressUpdate(this::handleProgress)
                            .onCancelled(this::handleErrorsInTask)
                            .build().execute();
                }
                break;
            case 3:
                //Search first and last
                String firstName = textOne.getText().toString();
                String lastName = textTwo.getText().toString();
                firstName = firstName.toLowerCase();
                lastName = lastName.toLowerCase();
                boolean go = true;
                if(firstName.length() < 3) {
                    textOne.setError("First name invalid or blank!");
                    go = false;
                }
                if(lastName.length() < 3) {
                    textTwo.setError("Last name invalid or blank!");
                    go = false;
                }
                if(go) {
                    //build the web service URL
                    Uri uri3 = new Uri.Builder()
                            .scheme("https")
                            .path(getString(R.string.ep_search))
                            .build();
                    //build the JSONObject
                    JSONObject msg3 = new JSONObject();
                    try {
                        msg3.put("option", 3);
                        msg3.put("firstName", firstName);
                        msg3.put("lastName", lastName);
                    } catch (JSONException e) {
                        Log.wtf("Error creating JSON object for existing connections:", e);
                    }

                    //instantiate and execute the AsyncTask.
                    //Feel free to add a handler for onPreExecution so that a progress bar
                    //is displayed or maybe disable buttons. You would need a method in
                    //LoginFragment to perform this.
                    new SendPostAsyncTask.Builder(uri3.toString(), msg3)
                            .onPostExecute(this::handleSearchOnPost)
                            .onCancelled(this::handleErrorsInTask)
                            .build().execute();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Handles progress bar for async
     * @param strings
     */
    private void handleProgress(String[] strings) {
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Handles search results and refreshes them in recycler view
     * @param result
     */
    private void handleSearchOnPost(String result) {
        mContacts.clear();
        String users = result.replace("username","");
        users = users.replaceAll("matches", "");
        users = users.replaceAll("[^a-zA-Z,]", "");
        String[] userList = users.split(",");
        String[] userListFinal = new String[userList.length];
        for(int i = 0; i < userList.length; i++) {
            userListFinal[i] = userList[i].replaceAll("[^a-zA-Z]", "");
        }

        for(String s : userListFinal) {
            mContacts.add(new Contact(s));
        }
        mAdapter = new ContactsAdapterAdd(mContacts, this.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mProgress.setVisibility(View.GONE);
    }

    /**
     * Checks the currently selected radio button for search method
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.addEmailRadio:
                textOne.setHint(R.string.add_email);
                textTwo.setVisibility(EditText.GONE);
                mOption = 1;
                break;
            case R.id.addNameRadio:
                textOne.setHint(R.string.add_first_name);
                textTwo.setVisibility(EditText.VISIBLE);
                textTwo.setHint(R.string.add_last_name);
                mOption = 3;
                break;
            case R.id.addNickRadio:
                textOne.setHint(R.string.add_nickname);
                textTwo.setVisibility(EditText.GONE);
                mOption = 2;
                break;
            default:
                textOne.setHint(R.string.add_email);
                textTwo.setVisibility(EditText.GONE);
                break;
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }
}
