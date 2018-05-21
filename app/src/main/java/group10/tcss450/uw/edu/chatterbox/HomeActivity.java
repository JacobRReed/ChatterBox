package group10.tcss450.uw.edu.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatContactsFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatListFragment;
import group10.tcss450.uw.edu.chatterbox.chatFragments.ChatMessageFragment;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            WeatherFragment.OnFragmentInteractionListener,
            ChangeLocationFragment.OnFragmentInteractionListener,
            ChatListFragment.OnFragmentInteractionListener {

    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_FONT = "font_pref";
    private LatLng mLocation;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePreferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePreferences.getInt(PREFS_THEME, 0);
        SharedPreferences fontPreferences = getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPreferences.getInt(PREFS_FONT, 0);
        /*SharedPreferences userPrefs = getSharedPreferences(getString(R.string.keys_shared_prefs), MODE_PRIVATE);
        String name = userPrefs.getString(getString(R.string.keys_prefs_username_local),"ChatterBox");
        TextView navBarTitle = findViewById(R.id.navBarTitleText);
        navBarTitle.setText(name);*/

        //Apply themes
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

        switch(fontChoice) {
            case 1:
                //Set font to small
                break;
            case 2:
                //Set font to medium
                break;
            case 3:
                //Set font to large
                break;
            default:
                //Set font to medium
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

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
        //the way to close an app programmaticaly
        finishAndRemoveTask();
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFragmentContainer, frag);

        // Commit the transaction
        transaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChangeLocationAction() {
        //Load change weather location fragment
        loadFragment(new ChangeLocationFragment());
    }

    @Override
    public void onChangeLocationSubmitAction(String zipcode) {
        //Save location changes and send to weather fragment
        Bundle bundle = new Bundle();
        bundle.putString("zip", zipcode);
        Fragment frag = new WeatherFragment();
        frag.setArguments(bundle);
        loadFragment(frag);
    }


    @Override
    public void onMakeNewChatAction() {
        loadFragment(new ChatContactsFragment());
    }

//    @Override
//    public void onAddFriendToChatAction() {
//        loadFragment(new ChatMessageFragment());
//    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }


}
