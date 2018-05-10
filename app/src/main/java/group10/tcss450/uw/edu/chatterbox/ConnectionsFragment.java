package group10.tcss450.uw.edu.chatterbox;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsAdd;
import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsExisting;
import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsInvite;
import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsRequests;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment  implements TabLayout.OnTabSelectedListener {
    private ConstraintLayout mContainer;

    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);
        mContainer = v.findViewById(R.id.connectionsFragmentContainer);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Connections");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        loadFragment(new ConnectionsExisting());
        TabLayout tabs = v.findViewById(R.id.connectionsTabLayout);
        tabs.addOnTabSelectedListener(this);
        return v;
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction().replace(R.id.connectionsFragmentContainer, frag);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        switch(position) {
            case 0:
                loadFragment(new ConnectionsExisting());
                break;
            case 1:
                loadFragment(new ConnectionsAdd());
                break;
            case 2:
                loadFragment(new ConnectionsRequests());
                break;
            case 3:
                loadFragment(new ConnectionsInvite());
                break;
            default:
                loadFragment(new ConnectionsExisting());
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
