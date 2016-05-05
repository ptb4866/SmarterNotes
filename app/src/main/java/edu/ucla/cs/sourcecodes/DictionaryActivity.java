package edu.ucla.cs.sourcecodes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.androidbelieve.drawerwithswipetabs.R;

import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity {



    public static final int PROGRESS_BAR = 0;
    public static ProgressDialog download_progress =  null;
    String word;
    String definitionInfo;
    private String TAG = "DictionaryActivity.java";

    private View mContentView = null;

    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Log.d(TAG, "Setting Content View ");



       mContentView = this.findViewById(android.R.id.content).getRootView();


        Log.d(TAG, "Content View Set");

        //inflater  = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mContentView = inflater.inflate(R.layout.activity_scrolling, null);


       // final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
         //       .findViewById(android.R.id.content)).getChildAt(0);



        Bundle extras = getIntent().getExtras();

        Log.d(TAG, "Getting Intent Extras");

        JSONObject jsonObject;
        JSONArray soundJArray, defJArray;

        TextView wordName =  (TextView)mContentView.findViewById(R.id.text_id) ;
        ListView listView = (ListView) mContentView.findViewById(R.id.list2);

        Log.d(TAG, "Setting Intent Extras");

        String[] values;


        if (extras != null) {
            word = extras.getString("WordName");
            Log.d(TAG,"Getting word "  + word);
            definitionInfo = extras.getString("Dictionary");
            Log.d(TAG,"Getting definition info" + definitionInfo);
            //textView.setText(word);
            wordName.setText(word);
            try {
                jsonObject = new JSONObject(definitionInfo);
                defJArray = jsonObject.getJSONArray("defs");
                soundJArray = jsonObject.getJSONArray("sounds");

                String soundOfWord = soundJArray.getString(0);


                ArrayList<String> definitions = new ArrayList<>();

                for (int i = 0; i < defJArray.length(); i++) {

                    Log.d(TAG, "Adding to definition list");

                     //definitions = Integer.toString(i) + " " + defJArray.getString(i) + "\n";
                    definitions.add(Integer.toString(i + 1) + " " + defJArray.getString(i));



                }

                values = definitions.toArray(new String[definitions.size()]);

                if (definitions != null) {

                    Log.d(TAG,"Creating ListView adapter");
                     ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_layout2, android.R.id.text1, values);
                    Log.d(TAG,"Setting Adapter");
                     listView.setAdapter(adapter);

                    Log.d(TAG,"Adapter Set");

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }




    }



    @Override
    protected Dialog onCreateDialog(int id) {

        switch(id) {
            case PROGRESS_BAR:   //set to 0

                download_progress = new ProgressDialog(this);
                download_progress.setMessage("Finding Definiation ... ");
                download_progress.setIndeterminate(false);
                download_progress.setMax(100);
                download_progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                download_progress.setCancelable(true);
                download_progress.show();
                return download_progress;
        }

        return null;

    }


}
