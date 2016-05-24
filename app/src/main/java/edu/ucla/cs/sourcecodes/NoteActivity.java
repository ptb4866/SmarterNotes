package edu.ucla.cs.sourcecodes;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


//import com.androidbelieve.sourcecodes.R;


import java.io.File;
import java.io.FileOutputStream;
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
    private ArrayList<Chip> mTagList1 = new ArrayList<>();

    private ChipView mTextChipAttrs;

    private String file = "mydata2";

    private SessionDataMap sessionData;
    private ListView mDrawerList;
    private ArrayList<ListViewItem> items;

    //new array type??


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentView = this.findViewById(android.R.id.content).getRootView();

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

                            String textValue = edittext.getText().toString();

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


    @Override
    protected void onPause()
    {
        super.onPause();
        /*
         * Use OnPause because it's guaranteed to always happen
         * Basically, this save function always happen
         * Possibility of memory leak?? Need to check for validity.
         */

        saveSession();

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(file, Context.MODE_PRIVATE);
        File myInternalFile = new File(directory , file);

        String saveData = JsonUtil.toJson(sessionData);
        if (saveData != null)
            try{
                FileOutputStream fout = new FileOutputStream(myInternalFile);
                fout.write(saveData.getBytes());
                fout.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void addDrawerItems() {
        //fill array with session name stored using session
        items = new ArrayList<>();
        items.add(new ListViewItem("Add Session", DrawerArrayAdapter.TYPE_ADD));

        ArrayList<String> fileNames = sessionData.getsNames();

        for (int i = 0; i < sessionData.getLength(); i++)
        {
            items.add(new ListViewItem(fileNames.get(i),DrawerArrayAdapter.TYPE_OBJECT));
        }

        //Update drawer here with sessions found
        mDrawerList = (ListView)findViewById(R.id.navList);
        final DrawerArrayAdapter drawerArrayAdapter = new DrawerArrayAdapter(this, R.id.text, items);

        mDrawerList.setAdapter(drawerArrayAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //save current array and stuff
                    saveSession();

                    //change current session name to the one clicked
                    ListViewItem curListView = (ListViewItem) drawerArrayAdapter.getItem(position);

                    sessionData.setCurSessionName(curListView.getText());
                    getSupportActionBar().setTitle(curListView.getText());

                    SessionData curData = sessionData.getSession(curListView.getText());

                    //switch array to new one
                    clearArray();
                    for (String word : curData.getWordList()) {
                        mTagList1.add(new Tag(word));
                    }

                    //close the drawer
                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                    mDrawerLayout.closeDrawers();
                }
            }
        });
    }

    public void addNewSession(String sessionName)
    {
        sessionData.setSession(sessionName, new SessionData());
        DrawerArrayAdapter drawerArrayAdapter = (DrawerArrayAdapter)mDrawerList.getAdapter();
        drawerArrayAdapter.add(new ListViewItem(sessionName, DrawerArrayAdapter.TYPE_OBJECT));
        drawerArrayAdapter.notifyDataSetChanged();

    }

    public void saveSession()
    {
        SessionData temp = new SessionData();
        temp.setSessionName(sessionData.getCurSessionName());

        ArrayList<String> wordArray = new ArrayList<>();

        for (Chip c :mTagList1)
        {
            wordArray.add(c.getText());
        }

        temp.setWordList(wordArray);
        sessionData.setSession(temp.getSessionName(),temp);

    }

    public void clearArray()
    {
        //clear the current array and reflect it in the adapter
        mTagList1.clear();
    }

    public void changeCurSessionName(String t)
    {
        //change current session
        sessionData.setCurSessionName(t);
        //update to reflect title
        getSupportActionBar().setTitle(t);

    }

    public String getCurName()
    {
        return sessionData.getCurSessionName();
    }

    public void removeSession(String sName)
    {
        sessionData.removeSession(sName);
    }

    public boolean sessionExist(String name)
    {
        ArrayList<String> names = sessionData.getsNames();
        return names.contains(name);
    }


 }
