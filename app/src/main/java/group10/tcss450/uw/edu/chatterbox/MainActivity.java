package group10.tcss450.uw.edu.chatterbox;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new LoginFragment());
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onLoginAction(String username, String password) {
        //Load home activity
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }

    @Override
    public void onLoginRegisterAction() {
        //Load registration fragment
        loadFragment(new RegisterFragment());
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
