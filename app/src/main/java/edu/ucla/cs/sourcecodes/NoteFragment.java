package edu.ucla.cs.sourcecodes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


//This class is needed because of tab fragment
public class NoteFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent i = new Intent(getActivity().getApplicationContext(), NoteActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        getActivity().getApplicationContext().startActivity(i);

    }


}
