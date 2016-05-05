package edu.ucla.cs.sourcecodes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidbelieve.drawerwithswipetabs.R;


public class NoteFragment extends Fragment {

    private  View mContentView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.note_layout,null);


       Intent i = new Intent(getActivity().getApplicationContext(), NoteActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        getActivity().getApplicationContext().startActivity(i);



        return mContentView;
    }
}
