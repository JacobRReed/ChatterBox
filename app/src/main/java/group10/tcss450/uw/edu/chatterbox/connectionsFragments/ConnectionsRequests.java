package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterRequests;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsRequests extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Contact> mContacts;


    public ConnectionsRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connections_requests, container, false);

        //Recycler view for result
        mRecyclerView = v.findViewById(R.id.requestsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        //Generate contacts
        mContacts = new ArrayList<>();
        createContacts();
        mAdapter = new ContactsAdapterRequests(mContacts);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }


    private void createContacts() {
        mContacts.add(new Contact("test1"));
        mContacts.add(new Contact("test2"));
        mContacts.add(new Contact("test3"));
        mContacts.add(new Contact("test4"));
        mContacts.add(new Contact("test5"));
        mContacts.add(new Contact("test6"));
        mContacts.add(new Contact("test7"));
        mContacts.add(new Contact("test8"));
        mContacts.add(new Contact("test9"));
        mContacts.add(new Contact("test10"));
        mContacts.add(new Contact("test11"));
        mContacts.add(new Contact("test12"));
        mContacts.add(new Contact("test13"));
        mContacts.add(new Contact("test14"));
        mContacts.add(new Contact("test15"));

    }
}
