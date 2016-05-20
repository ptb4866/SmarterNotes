package edu.ucla.cs.sourcecodes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.androidbelieve.sourcecodes.R;

import android.widget.Toast;


public class MicrophoneFragment extends Fragment {


    View mContentView = null;

    private String TAG = "MicrophoneFragment.java";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.microphone_layout,container,false);

        Toast.makeText(getActivity(), "Coming Soon",Toast.LENGTH_SHORT).show();

      /*  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please start speaking");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        Log.d(TAG, "starting activity for result");
        getParentFragment().startActivityForResult(intent, 2);

*/
        return  mContentView;
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "In activity result");

        Log.d(TAG, Integer.toString(requestCode));

        if (requestCode == 2) {

            Log.d(TAG,"In result code");

            ArrayList<String> results;
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //If name has a  ' then the SQL is failing hence replacing them
            String text = results.get(0).replace("'", "");
            //Toast.makeText(getActivity(), text,Toast.LENGTH_LONG).show();

            Intent i = new Intent(getActivity().getApplicationContext(), NoteActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction(Intent.ACTION_VIEW);
            i.putExtra("MicroPhoneWords",text);
            Log.d(TAG, "starting NoteFragment class");
            getActivity().getApplicationContext().startActivity(i);


        }
    }

    */




}
