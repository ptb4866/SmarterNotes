package edu.ucla.cs.sourcecodes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;



import java.util.ArrayList;


public class DictionaryActivity extends AppCompatActivity {



    public static final int PROGRESS_BAR = 0;
    public static ProgressDialog download_progress =  null;
    String word = null;
    String definitionInfo;
    private String TAG = "DictionaryActivity.java";

    private View mContentView = null;

    LayoutInflater inflater;

    private TextToSpeechHelper ttsh;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.definition_layout);

        Log.d(TAG, "Setting Content View ");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

    //    mContentView = this.findViewById(android.R.id.content).getRootView();

        ttsh = new TextToSpeechHelper(this);

        Log.d(TAG, "Content View Set");

        //inflater  = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mContentView = inflater.inflate(R.layout.activity_scrolling, null);


        // final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
        //       .findViewById(android.R.id.content)).getChildAt(0);



        Bundle extras = getIntent().getExtras();

        Log.d(TAG, "Getting Intent Extras");

        JSONObject jsonObject;
        JSONArray soundJArray, defJArray;

        final TextView wordName =  (TextView)findViewById(R.id.text_id) ;
        ListView listView = (ListView) findViewById(R.id.list2);

        final ImageView speakerImage = (ImageView) findViewById(R.id.speech);



        Log.d(TAG, "Setting Intent Extras");

        String[] values;


        if (extras != null) {
            word = extras.getString("WordName");
            Log.d(TAG,"Getting word "  + word);
            definitionInfo = extras.getString("Dictionary");
            Log.d(TAG,"Getting definition info" + definitionInfo);
            //textView.setText(word);
            wordName.setText(word);

            speakerImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ttsh.speakOut(word);
                }
            });

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

        final Button translateID =  (Button)findViewById(R.id.translateID) ;
        final Button browseID =  (Button)findViewById(R.id.browseID) ;

        translateID.setOnClickListener(onClickListener);
        browseID.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.translateID:
                    //DO something
                         try {


                            String translation = Translate.translate(word, Language.ENGLISH.toString(), Language.SPANISH.toString());
                             if (translation != null) {

                                 Log.d(TAG,translation);
                             } else {

                                 Log.d(TAG, "Translation is null");
                             }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    break;
                case R.id.browseID:
                    //DO something
                    if (!word.equals(null)) {

                        try {
                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);

                            intent.putExtra(SearchManager.QUERY, word);
                            startActivity(intent);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }
                    break;

            }
        }
    };


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