package group10.tcss450.uw.edu.chatterbox.connectionsFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.utils.Contact;
import group10.tcss450.uw.edu.chatterbox.utils.ContactsAdapterAdd;


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

    public ConnectionsAdd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connections_add, container, false);
        RadioGroup searchMethod = v.findViewById(R.id.addRadios);
        searchMethod.setOnCheckedChangeListener(this::onCheckedChanged);
        textOne = v.findViewById(R.id.addTextBoxOne);
        textTwo = v.findViewById(R.id.addTextBox2);
        searchButton = v.findViewById(R.id.addSearchButton);
        //@TODO
        //Set search button so that is searches database returns results in a list and feeds them through contacts
        //Generating results into recycler view

        //Recycler view for result
        mRecyclerView = v.findViewById(R.id.addRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        //Generate contacts
        mContacts = new ArrayList<>();
        createContacts();
        mAdapter = new ContactsAdapterAdd(mContacts);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return v;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.addEmailRadio:
                textOne.setHint(R.string.add_email);
                textTwo.setVisibility(EditText.GONE);
                break;
            case R.id.addNameRadio:
                textOne.setHint(R.string.add_first_name);
                textTwo.setVisibility(EditText.VISIBLE);
                textTwo.setHint(R.string.add_last_name);
                break;
            case R.id.addNickRadio:
                textOne.setHint(R.string.add_nickname);
                textTwo.setVisibility(EditText.GONE);
                break;
            default:
                textOne.setHint(R.string.add_email);
                textTwo.setVisibility(EditText.GONE);
                break;
        }
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
