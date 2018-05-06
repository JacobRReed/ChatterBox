package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group10.tcss450.uw.edu.chatterbox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsInvite extends Fragment {


    public ConnectionsInvite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connections_invite, container, false);
    }

}
