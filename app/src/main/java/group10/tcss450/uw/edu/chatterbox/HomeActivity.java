package group10.tcss450.uw.edu.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatContactsFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatListFragment;
import group10.tcss450.uw.edu.chatterbox.utils.SendPostAsyncTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            WeatherFragment.OnFragmentInteractionListener,
            ChangeLocationFragment.OnFragmentInteractionListener,
            ChatListFragment.OnFragmentInteractionListener,
            ChatContactsFragment.OnFragmentInteractionListener{

    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_FONT = "font_pref";
    private static final String PREFS_LOC = "location_pref";
    private String mCreateChatUrl;
    private String mAddFriendsToChatUrl;
    private String mUsername;
    private String mNewChatNameUrl;
    private String mCurrentChatId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Handle shared prefs for theme and fonts
         */
        SharedPreferences themePreferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePreferences.getInt(PREFS_THEME, 0);

        SharedPreferences prefs =
                getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);

        mUsername = prefs.getString(getString(R.string.keys_prefs_username_local), "");
        mAddFriendsToChatUrl = new Uri.Builder() .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath(getString(R.string.ep_add_friend_to_chat)) .build()
                .toString();
        mCreateChatUrl = new Uri.Builder() .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath(getString(R.string.ep_create_chat)) .build()
                .toString();
        mNewChatNameUrl = new Uri.Builder() .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("chats")
                .appendPath(getString(R.string.ep_new_chat_name)) .build()
                .toString();
        //Apply themes from shared prefs
        switch(themeChoice) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppThemeTwo);
                break;
            case 3:
                setTheme(R.style.AppThemeThree);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        //Apply nav side bar theme
        switch(themeChoice) {
            case 1:
                header.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                break;
            case 2:
                header.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDarkTwo));
                break;
            case 3:
                header.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDarkThree));
                break;
            default:
                header.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                break;
        }

        //Get location from map if available
        Fragment weatherFrag = new WeatherFragment();
        loadFragment(weatherFrag);
    }


    /**
     * Handles back presses
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creates options menu
     * @param menu Menu to create
     * @return boolean true if success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /*
    Handles menu item clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            onLogout();
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            return true;
        } else if(id == R.id.action_settings) {
            loadFragment(new SettingsFragment());
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles logouts.
     */
    @Override
    public void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_username));
        prefs.edit().remove(getString(R.string.keys_prefs_username_local));
        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_logged_in),
                false)
                .apply();
        //Closes app on logout
        finishAndRemoveTask();
    }

    /**
     * Helper method to load fragments
     * @param frag Fragment to load
     */
    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_connections) {
            // Handle the connections action
            loadFragment(new ConnectionsFragment());
        } else if (id == R.id.nav_chat) {
            //Handle chat action
//            loadFragment(new ChatFragment());
            loadFragment(new ChatListFragment());
        } else if (id == R.id.nav_weather) {
            //Handle weather action
            loadFragment(new WeatherFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Handles change location button, which loads new change location fragment
     */
    @Override
    public void onChangeLocationAction() {
        //Load change weather location fragment
        loadFragment(new ChangeLocationFragment());
    }

    /*
    When user enters zipcode and selects save changes, saves zipcode to prefs.
     */
    @Override
    public void onChangeLocationSubmitAction(String zipcode) {
        SharedPreferences.Editor locEditor = this.getSharedPreferences(PREFS_LOC, MODE_PRIVATE).edit();
        locEditor.putBoolean("searchZip", true);
        locEditor.putString("zipCode", zipcode);
        locEditor.commit();
        //Save location changes and send to weather fragment
        Bundle bundle = new Bundle();
        bundle.putString("zip", zipcode);
        Fragment frag = new WeatherFragment();
        frag.setArguments(bundle);
        loadFragment(frag);
    }


    /**
     * Handles errors for chat
     * @param msg
     */
    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfAddFriendsToChat(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                Log.d("Can I say Fuck Ya?", "I guess so...");
                // not sure what this needs to be
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Can I say Fuck No?", "I guess not...");
        }
    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfNewChatName(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                Log.d("Can I say Fuck Ya 2?", "I guess so... 2");
                // not sure what this needs to be
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Can I say Fuck No 2?", "I guess not... 2");
        }
    }

    /**
     * Handles end of message task ASYNC
     * @param result JSON string
     */
    private void endOfOnMakeNewChat(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {


                //----
                String temp = res.get("chatid").toString();
                temp = temp.replace("{\"chatid\":","");
                temp = temp.replace("}", "");

                //---

//                String tempCurrentChatId = temp;

                mCurrentChatId = temp;
//                SharedPreferences prefs =
//                        getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//                prefs.edit().putString("THIS_IS_MY_CURRENT_CHAT_ID", tempCurrentChatId);


//                Log.e("Results", "Success on creating a temp chat. the res: " + tempCurrentChatId + " the sharePref: " + prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Results", "Failure on creating a temp chat.");
        }
    }

    @Override
    public void onMakeNewChatAction() {

        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put("name", mUsername + " temp"); // change this
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Try Put: ", "Failure on putting a temp chat.");

        }
        new SendPostAsyncTask.Builder(mCreateChatUrl, messageJson)
                .onPostExecute(this::endOfOnMakeNewChat)
                .onCancelled(this::handleError)
                .build().execute();


        loadFragment(new ChatContactsFragment());
    }

    @Override
    public void onCreateNewChatButtonPressed(ArrayList<String> theCheckedFriends) {

        SharedPreferences prefs =
                getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String currentChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");

//        add username to arraylist
        theCheckedFriends.add(mUsername);

        Log.d("Fuck ya", theCheckedFriends.toString() + " the chatid is: " + mCurrentChatId);
        int currChatID = Integer.parseInt(mCurrentChatId);
        for(int i = 0; i < theCheckedFriends.size(); i++) {
            JSONObject messageJson = new JSONObject();

//            SharedPreferences prefs =
//                    getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//
//            String dogChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");
            try {
                messageJson.put(getString(R.string.keys_json_chat_id), currChatID);
                messageJson.put(getString(R.string.keys_json_username), theCheckedFriends.get(i).toString());
                Log.d("Fuck Try Put: ", "Success on putting new friend into chat. chatID: " + currChatID + " CurrentFriendAdded: " + theCheckedFriends.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Fuck Try Put: ", "Failure on putting a temp chat. chatID: " + mCurrentChatId + " CurrentFriendAdded: " + theCheckedFriends.get(i));
            }
            new SendPostAsyncTask.Builder(mAddFriendsToChatUrl, messageJson)
                    .onPostExecute(this::endOfAddFriendsToChat)
                    .onCancelled(this::handleError)
                    .build().execute();
        }

        chatNameChange(mCurrentChatId, theCheckedFriends);
        loadFragment(new ChatListFragment());

        theCheckedFriends.clear();
    }

    public void chatNameChange(String theChatid, ArrayList<String> theCheckedFriends) {
        String newChatName = "";
        newChatName += theChatid + " ";
        for(int i = 0; i < theCheckedFriends.size(); i++) {
            newChatName += theCheckedFriends.get(i).toString();
        }
        JSONObject messageJson = new JSONObject();

//            SharedPreferences prefs =
//                    getSharedPreferences( getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//
//            String dogChatId = prefs.getString("THIS_IS_MY_CURRENT_CHAT_ID", "0");
        try {
            messageJson.put(getString(R.string.keys_json_chat_id), Integer.parseInt(theChatid));
            messageJson.put(getString(R.string.keys_json_new_chatname), newChatName);
            //Log.d("Fuck Try Put: ", "Success on putting new friend into chat. chatID: " + currChatID + " CurrentFriendAdded: " + theCheckedFriends.get(i).toString());
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("Fuck Try Put: ", "Failure on putting a temp chat. chatID: " + mCurrentChatId + " CurrentFriendAdded: " + theCheckedFriends.get(i));
        }
        new SendPostAsyncTask.Builder(mNewChatNameUrl, messageJson)
                .onPostExecute(this::endOfNewChatName)
                .onCancelled(this::handleError)
                .build().execute();
    }
}
