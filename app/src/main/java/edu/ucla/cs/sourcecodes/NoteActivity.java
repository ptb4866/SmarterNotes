package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


//import com.androidbelieve.sourcecodes.R;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import co.hkm.soltag.TagContainerLayout;
import co.hkm.soltag.ext.LayouMode;
import edu.ucla.cs.utils.Chip;
import edu.ucla.cs.utils.ChipView;
import edu.ucla.cs.utils.ChipViewAdapter;
import edu.ucla.cs.utils.MainChipViewAdapter;
import edu.ucla.cs.utils.OnChipClickListener;
import edu.ucla.cs.utils.Tag;


public class NoteActivity extends AppCompatActivity {



    private View mContentView = null;

    private static String TAG = "NoteActivity";
    private List<Chip>    mTagList1 = new ArrayList<>();

    private List<Chip> mTagList2;

    private ChipView mTextChipDefault;
    private ChipView mTextChipAttrs;
    private ChipView mTextChipLayout;
    private ChipView mTextChipOverride;



    ListView listView;
    String[] values;

    ArrayList<String> array = new ArrayList<>();;
    List<String> tags = new ArrayList<>();
    protected static int position = 0;
    TagContainerLayout mTagContainerLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContentView = this.findViewById(android.R.id.content).getRootView();

        //listView = (ListView) mContentView.findViewById(R.id.list);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String words = extras.getString("cameraActivityString");

            if (words != null) {

                words = words.trim();
                //split words with space and tab as the delimeter
                String[] data = words.split("\\s+");

                for (String word : data) {

                    mTagList1.add(new Tag(word)) ;

                }
            }


            // Attrs ChipTextView
            mTextChipAttrs = (ChipView) findViewById(R.id.text_chip_attrs);
            mTextChipAttrs.setChipList(mTagList1);
            mTextChipAttrs.setOnChipClickListener(new OnChipClickListener() {
                @Override
                public void onChipClick(final Chip chip) {
                    final String text = chip.getText();

                    AlertDialog dialog = new AlertDialog.Builder(NoteActivity.this)
                            .setTitle(" ")
                            .setMessage("Select Option!")
                            .setPositiveButton("Define", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (isNetworkConnected()) {
                                        new AsyncTaskParseXML(text, getBaseContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    } else {
                                        Toast.makeText(NoteActivity.this, "No Internet Connection ",  Toast.LENGTH_LONG).show();

                                    }

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTextChipAttrs.remove(chip);
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();

                }
            });







        }













        final AlertDialog.Builder alert = new AlertDialog.Builder(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
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
                            //tags.add(textValue);

                  /*            if (tags.isEmpty()) {

                                tags.add(textValue);

                            } else {

                                if (NoteActivityCustomTags.container != null) {

                                    NoteActivityCustomTags.container.removeAllTags();

                                    tags.add(textValue);


                                }


                            }


                            NoteActivityCustomTags.setContext(getBaseContext());
                            // After you set your own attributes for TagView, then set tag(s) or add tag(s)
                            NoteActivityCustomTags.ROUND_CORNER
                                    .render(NoteActivity.this)
                                    .setMode(LayouMode.SINGLE_CHOICE)
                                    .define(NoteActivity.this)
                                    .setTags(tags);

*/
                            mTagList1.add(new Tag(textValue)) ;

                             // Attrs ChipTextView
                            mTextChipAttrs = (ChipView) findViewById(R.id.text_chip_attrs);
                            mTextChipAttrs.setChipList(mTagList1);
                            mTextChipAttrs.setOnChipClickListener(new OnChipClickListener() {
                                @Override
                                public void onChipClick(final Chip chip) {
                                    final String text = chip.getText();

                                    AlertDialog dialog = new AlertDialog.Builder(NoteActivity.this)
                                            .setTitle(" ")
                                            .setMessage("Select Option!")
                                            .setPositiveButton("Define", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (isNetworkConnected()) {
                                                        new AsyncTaskParseXML(text, getBaseContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                    } else {
                                                        Toast.makeText(NoteActivity.this, "No Internet Connection ",  Toast.LENGTH_LONG).show();

                                                    }

                                                 }
                                            })
                                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mTextChipAttrs.remove(chip);
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    dialog.show();

                              }
                            });







                         /*   array.add(textValue);
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


                            } */
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



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

 }
