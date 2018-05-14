package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;

import group10.tcss450.uw.edu.chatterbox.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatContactsFragment extends android.support.v4.app.Fragment {


    public ChatContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_contacts, container, false);



        return v;
    }

}
