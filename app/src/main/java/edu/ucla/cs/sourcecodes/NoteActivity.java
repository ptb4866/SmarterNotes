package edu.ucla.cs.sourcecodes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

//import com.androidbelieve.sourcecodes.R;


import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private View mContentView = null;

    ListView listView;
    String[] values;

    ArrayList<String> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContentView = this.findViewById(android.R.id.content).getRootView();

        listView = (ListView) mContentView.findViewById(R.id.list);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)

                //        .setAction("Action", null).show();

                final EditText edittext = new EditText(getApplicationContext());
                edittext.setTextColor(Color.BLACK);
                alert.setMessage("Enter Your Message");
                alert.setTitle("Add Word To List");

                alert.setView(edittext);


                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //

                        String textValue = edittext.getText().toString();

                        array.add(textValue);
                        Log.d("MYTAG", textValue);
                        if (array != null) {
                            values = array.toArray(new String[array.size()]);
                            ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_layout, android.R.id.text1, values);
                            listView.setAdapter(adapter);


                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    // ListView Clicked item index
                                    int itemPosition = position;

                                    // ListView Clicked item value
                                    String word = (String) listView.getItemAtPosition(position);
                                    Log.d("MYTAGS", word);


                                    new AsyncTaskParseXML(word, getBaseContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




                                    // Show Alert
                                    //Toast.makeText(getActivity().getApplicationContext(),
                                    //        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                                    //        .show();



                                }

                            });


                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.

                        dialog.cancel();
                    }
                });

                alert.show();



            }
        });




    }

}
