package group10.tcss450.uw.edu.chatterbox;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment {


    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Connections");
        } catch (NullPointerException e) {
            Log.e("Error", "title isn't working");
        }

        return v;
    }

}
