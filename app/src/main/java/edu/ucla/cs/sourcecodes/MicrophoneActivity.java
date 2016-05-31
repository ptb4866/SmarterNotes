package edu.ucla.cs.sourcecodes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import edu.ucla.cs.sourcecodes.main.CameraActivity;
import edu.ucla.cs.sourcecodes.notes.NoteActivity;

public class MicrophoneActivity extends AppCompatActivity {

    public static SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    private String TAG = "MicrophoneActivity.java";

    private TextView microphoneStatus;
    private ImageView mic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        mIslistening = false;

        microphoneStatus = (TextView)findViewById(R.id.mic_status);
        mic = (ImageView)findViewById(R.id.mic);

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

        // Add a listener to the Capture button
        mic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (!mIslistening) {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                }



            }
        });



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
            microphoneStatus.setText("Got it!!!");


        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            microphoneStatus.setText("Didn't hear that. Select Audio Button!!!");

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
            microphoneStatus.setText("Say Something!!!");
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
        AlertDialog dialog = new AlertDialog.Builder(MicrophoneActivity.this)
                .setTitle("Add a new session ")
                .setMessage("Enter A Name ")
                .setView(edittext)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mSpeechRecognizer != null) {
                            mSpeechRecognizer.cancel();
                            microphoneStatus.setText("Press Mic Button!!");
                        }
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

                        if (mSpeechRecognizer != null) {
                            mSpeechRecognizer.cancel();
                            microphoneStatus.setText("Press Mic Button!!");
                        }

                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();


    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {

            if (mSpeechRecognizer != null) {

                mSpeechRecognizer.cancel();
                microphoneStatus.setText("Press Mic Button!!");


            }
        }
        return true;
    }





}
