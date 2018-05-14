package group10.tcss450.uw.edu.chatterbox.chatFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.support.v7.app.AppCompatActivity;

import group10.tcss450.uw.edu.chatterbox.R;
import group10.tcss450.uw.edu.chatterbox.WeatherFragment;
import group10.tcss450.uw.edu.chatterbox.connectionsFragments.ConnectionsExisting;
import group10.tcss450.uw.edu.chatterbox.model.Credentials;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ChatListFragment extends android.support.v4.app.Fragment {


    private ChatListFragment.OnFragmentInteractionListener mListener;
    private ConstraintLayout mContainer;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        mContainer = v.findViewById(R.id.connectionsFragmentContainer);


        Button makeNewChatButton = v.findViewById(R.id.ChatListMakeNewChatActionButton);
        makeNewChatButton.setOnClickListener(view -> mListener.onMakeNewChatAction());

        return v;
    }


    public interface OnFragmentInteractionListener {
        void onMakeNewChatAction();
        void onLogout();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatListFragment.OnFragmentInteractionListener) {
            mListener = (ChatListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
