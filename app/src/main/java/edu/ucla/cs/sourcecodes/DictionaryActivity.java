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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import java.util.ArrayList;


public class DictionaryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    public static final int PROGRESS_BAR = 0;
    public static ProgressDialog download_progress =  null;
    String word = null;
    String definitionInfo;
    private String TAG = "DictionaryActivity.java";

    private View mContentView = null;

    LayoutInflater inflater;
    String soundOfWord;
    private TextToSpeechHelper ttsh;
    ListView listView;

    ArrayList<String> definitions;
    TextView wordName = null;

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

        wordName =  (TextView)findViewById(R.id.text_id) ;
        listView = (ListView) findViewById(R.id.list2);

        final Button speakerImage = (Button) findViewById(R.id.speech);



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

                soundOfWord = soundJArray.getString(0);
                definitions = new ArrayList<>();

                for (int i = 0; i < defJArray.length(); i++) {

                    Log.d(TAG, "Adding to definition list");

                    if (!defJArray.getString(i).isEmpty()) {

                        definitions.add(defJArray.getString(i));
                        //definitions.add(Integer.toString(i + 1) + " " + defJArray.getString(i));
                        //  defCopy.add(defJArray.getString(i));

                    }
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

        // final Button translateID =  (Button)findViewById(R.id.translateID) ;
        final Button browseID =  (Button)findViewById(R.id.browseID) ;

        //translateID.setOnClickListener(onClickListener);
        browseID.setOnClickListener(onClickListener);



        //Spinner listiener
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


    }


    public void setTranslation(String to) {

        String data;
        String[] values;
        try {

            if (!to.equals(Language.ENGLISH.toString())) {

                data = word;
                data = Translate.translate(data, Language.ENGLISH.toString(), to);
                wordName.setText(data);

            } else {
                wordName.setText(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> defCopy = new ArrayList<>();

        if ( definitions != null) {


            Boolean check = false;
            if (!to.equals(Language.ENGLISH.toString())) {
                for (String def: definitions) {

                    try {
                        data = Translate.translate(def, Language.ENGLISH.toString(), to);
                        defCopy.add(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                values = defCopy.toArray(new String[defCopy.size()]);
                check = true;

            } else {
                values = definitions.toArray(new String[definitions.size()]);

                check = true;
            }


            if (check == true ) {
                Log.d(TAG, "Creating ListView adapter");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_layout2, android.R.id.text1, values);

                Log.d(TAG, "Setting Adapter");
                listView.setAdapter(adapter);

            }

        }





    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String item =  parent.getItemAtPosition(pos).toString();

        switch (item) {

            case "English":

                Toast.makeText(parent.getContext(), "Translating to: " + item, Toast.LENGTH_LONG).show();

                setTranslation(Language.ENGLISH.toString());

                break;
            case "French":

                Toast.makeText(parent.getContext(), "Translating to: " + item, Toast.LENGTH_LONG).show();


               /* try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                setTranslation(Language.FRENCH.toString());

                break;
            case "Spanish":

                Toast.makeText(parent.getContext(), "Translating to: " + item, Toast.LENGTH_LONG).show();
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                setTranslation(Language.SPANISH.toString());
                break;

            case "Russian":
                Toast.makeText(parent.getContext(), "Translating to: " + item, Toast.LENGTH_LONG).show();
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                setTranslation(Language.RUSSIAN.toString());
                break;
            default: Log.d(TAG, "No language Found:");
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
          /*      case R.id.translateID:
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


                    break;*/

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