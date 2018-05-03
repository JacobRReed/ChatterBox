package group10.tcss450.uw.edu.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WeatherFragment.OnFragmentInteractionListener, ChangeLocationFragment.OnFragmentInteractionListener {

    private static final String PREFS_THEME = "theme_pref";
    private static final String PREFS_FONT = "font_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePreferences = getSharedPreferences(PREFS_THEME, MODE_PRIVATE);
        int themeChoice = themePreferences.getInt(PREFS_THEME, 0);
        SharedPreferences fontPreferences = getSharedPreferences(PREFS_FONT, MODE_PRIVATE);
        int fontChoice = fontPreferences.getInt(PREFS_FONT, 0);

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
                header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case 2:
                header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkTwo));
                break;
            case 3:
                header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkThree));
                break;
            default:
                header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
        }

        loadFragment(new WeatherFragment());
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
                .replace(R.id.homeFragmentContainer, frag)
                .addToBackStack(null);
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
            loadFragment(new ChatFragment());
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
    public void onChangeLocationSubmitAction() {
        //Save location changes and send to weather fragment
        loadFragment(new WeatherFragment());
    }
}
