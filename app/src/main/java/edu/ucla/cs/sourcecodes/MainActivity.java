package edu.ucla.cs.sourcecodes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends  Activity implements CameraFragment.onMyEventListener {
    DrawerLayout mDrawerLayout;
    NavigationView mBottomView;
    android.app.FragmentManager mFragmentManager;
    android.app.FragmentTransaction mFragmentTransaction;

    private String TAG = "MainActivity.java";


    final int[] ICONS = new int[]{
            R.drawable.microphone,
            R.drawable.gallery,
            R.drawable.composea,
    };

    public final int CAMERA_MESSAGE = 0;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    private MyAdapter adapter;
    public static ProgressDialog progressBar =  null;

    public static SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    TextView notifications;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            /**
         *Inflate tab_layout and setup Views.
         */

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

       notifications = (TextView)findViewById(R.id.status);


        mIslistening = false;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        /**
         *Set an Apater for the View Pager
         */
      //  viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        adapter = new MyAdapter(MainActivity.this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(3,true);

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(ICONS[0]);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);

                 tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        // super.onTabSelected(tab);
                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);

                        switch(tabPosition) {
                            case 0:
                                Log.d(TAG, "Position 0 selected");


                                if (CameraFragment.progressDialog == null) {

                                    if (!mIslistening) {
                                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                    }
                                }
                                if (CameraFragment.progressDialog != null) {
                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        if (!mIslistening) {
                                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                        }
                                    }
                                }
                                break;
                            case 1:
                                if (mSpeechRecognizer != null) {
                                    mSpeechRecognizer.cancel();
                                    findViewById(R.id.status).setVisibility(View.GONE);
                                }
                                if (CameraFragment.progressDialog == null) {

                                    final CameraFragment fragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_frag);
                                    fragment.onInitiateCapture();
                                }
                                if (CameraFragment.progressDialog != null) {

                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        final CameraFragment fragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_frag);
                                        fragment.onInitiateCapture();
                                    }

                                }
                                            break;
                            case 2:

                                if (mSpeechRecognizer != null) {

                                    mSpeechRecognizer.cancel();
                                    findViewById(R.id.status).setVisibility(View.GONE);

                                }
                                Log.d(TAG, "Position 2 selected");
                                if (CameraFragment.progressDialog == null) {

                                    Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.setAction(Intent.ACTION_VIEW);
                                    startActivity(i);

                                }
                                if (CameraFragment.progressDialog != null) {

                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.setAction(Intent.ACTION_VIEW);
                                        startActivity(i);

                                    }
                                }
                                        break;
                        }

                        Log.d(TAG,"Tab Selected!!");


                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                        int tabPosition  = tab.getPosition();
                        viewPager.setCurrentItem(tabPosition);
                        switch(tabPosition) {
                            case 0:
                                Log.d(TAG, "Position 0 selected");
                                if (CameraFragment.progressDialog == null) {

                                    if (!mIslistening) {
                                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                    }

                                }
                                if (CameraFragment.progressDialog != null) {

                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        if (!mIslistening) {
                                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                        }

                                    }
                                }


                                break;
                            case 1:

                                if (mSpeechRecognizer != null) {

                                    mSpeechRecognizer.cancel();
                                    findViewById(R.id.status).setVisibility(View.GONE);

                                }
                                if (CameraFragment.progressDialog == null) {

                                    final CameraFragment fragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_frag);
                                    fragment.onInitiateCapture();

                                }
                                if (CameraFragment.progressDialog != null) {

                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        final CameraFragment fragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_frag);
                                        fragment.onInitiateCapture();


                                    }

                                }

                                break;
                            case 2:
                                Log.d(TAG, "Position 2 selected");

                                if (mSpeechRecognizer != null) {

                                    mSpeechRecognizer.cancel();
                                    findViewById(R.id.status).setVisibility(View.GONE);

                                }
                                Log.d(TAG, "Position 2 selected");
                                if (CameraFragment.progressDialog == null) {

                                    Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.setAction(Intent.ACTION_VIEW);
                                    startActivity(i);

                                }
                                if (CameraFragment.progressDialog != null) {

                                    if (!CameraFragment.progressDialog.isShowing()) {
                                        Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.setAction(Intent.ACTION_VIEW);
                                        startActivity(i);

                                    }
                                }
                               // Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                               // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               // i.setAction(Intent.ACTION_VIEW);
                               // startActivity(i);
                                break;
                        }


                        Log.d(TAG,"Re Tab Selected!!");
                    }


                });

            }
        });
       // return mContentView;

    }



    @Override
    public void  onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");

            notifications.setVisibility(View.VISIBLE);
            //notifications.setText("Please Wait!!!");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
            notifications.setText("Got it!!!");

            notifications.setVisibility(View.GONE);
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            notifications.setText("Didn't hear that. Select Audio Button!!!");

            Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
            notifications.setText("Say Something!!!");
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do

            String str = "";

            for (String match: matches) {

                str = str  + match + " ";
            }

            notifications.setText("You said " + str);

            //Intent i = new Intent( getApplicationContext(), NoteActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // i.setAction(Intent.ACTION_VIEW);
           // i.putExtra("cameraActivityString", str);
           // Log.d(TAG, "starting Other Activity");
            //startActivity(i);
            displayWordsInNoteActivity(str);

        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }


    void displayWordsInNoteActivity(String words) {


        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.BLACK);

        final String finalStr = words;
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add a new session ")
                .setMessage("Enter A Name ")
                .setView(edittext)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String textValue = edittext.getText().toString();


                        Intent i = new Intent( getApplicationContext(), NoteActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setAction(Intent.ACTION_VIEW);
                        i.putExtra("addToNote", finalStr);
                        i.putExtra("sessionTitle",textValue);
                        Log.d(TAG, "starting Other Activity");
                        startActivity(i);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();



    }




    void addShowHideListener(final Fragment fragment) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out);
            if (fragment.isHidden()) {
                ft.show(fragment);

            } else {
                ft.hide(fragment);

            }
            ft.commit();


    }

    @Override
    public void someEvent(String s) {

        if ("displayCameraMessage".equals(s)) {

            CameraFragment.progressDialog = ProgressDialog.show(this,"Camera Message", "Please Wait. Extracting Text");

        }

        if ("dismissProgressBar".equals(s)) {

            if (CameraFragment.progressDialog != null && CameraFragment.progressDialog.isShowing()) {

                CameraFragment.progressDialog.dismiss();
            }
        }


        String str = s.substring(0,14) ;
        Log.d(TAG, "substring:" + str);

        if ("recognizedText".equals(str)) {

            if (s.length() > 15) {
                displayWordsInNoteActivity(s.substring(15, s.length()));
                ;
            } else {
                Toast.makeText(MainActivity.this, "Text Couldn't Be Retrieved. Retry!!! ",  Toast.LENGTH_LONG).show();


            }
        }


    }

     private class  MyAdapter extends  PagerAdapter {

        private LayoutInflater mInflater;
        private int[] mLayouts = {R.layout.microphone_layout, R.layout.camera_layout, R.layout.note_layout};

        public MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup pageView = (ViewGroup) mInflater.inflate(mLayouts[position],
                    container, false);
            container.addView(pageView);
            getItemPosition(pageView);
            return pageView;
        }



        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }



    /*
    class MyAdapter extends FragmenStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }


        //
        // Return fragment with respect to Position .
         ///

        @Override
        public  Fragment getItem(int position)
        {
            switch (position){
              //  case 0 : return new android.support.v4.app.Fragment();
              //  case 1 : return new android.support.v4.app.Fragment();
              //  case 2 : return new android.support.v4.app.Fragment();

                case 0: return new MicrophoneFragment();

                case 1: return new CameraFragment();


                case 2: return new  NoteFragment() ;


            }

            Log.d(TAG, "NULL");
            Log.d(TAG, "position in getItem: " + Integer.toString(position));

            return null;

        }

        @Override
        public int getCount() {

            return int_items;

        }

    }

*/

}