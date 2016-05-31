package edu.ucla.cs.sourcecodes.dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ptb4866 on 4/28/16.
 */
public class AsyncTaskParseXML extends AsyncTask<String, Integer, String> {


    final String TAG = "AsyncTaskParseXML.java";

    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser parser;

    public ArrayList<String> myDefs = new ArrayList();
    public ArrayList<String> mySounds = new ArrayList();

    private Context context;

    // contacts JSONArray
    JSONArray dataJsonArr = null;


    private String word;

    public AsyncTaskParseXML(String word, Context context) {
         this.word = word;

        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    protected void onProgressUpdate(Integer... progress){


    }


    @Override
    protected String doInBackground(String... arg0) {

            Log.d(TAG, "DoInbackground Begins");
            // instantiate our json parser
            DictionaryQuery query = new DictionaryQuery();


            String XMLData = query.getXMLData(word);
            Log.d(TAG, XMLData);

            InputStream stream =  query.getXMLStream(word);


            return (parser(stream ));



    }



    public void printProducts(ArrayList<String> products){

            for (String product: products) {

                 Log.d(TAG, product);
            }

    }

    public String parser(InputStream stream ) {

        ArrayList<String> defs = null;
        ArrayList<String> sounds = null;
        JSONArray soundsJSON = new JSONArray();
        JSONArray defsJSON = new JSONArray();
        JSONObject jObject = new JSONObject();

        try {
           xmlFactoryObject = XmlPullParserFactory.newInstance();
           parser = xmlFactoryObject.newPullParser();
           parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
           parser.setInput(stream, null);



            int eventType = parser.getEventType();

            String currentDefinition = null;
            String sound = null;

            while (eventType != XmlPullParser.END_DOCUMENT ){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:

                        defs = new ArrayList();
                        sounds = new ArrayList();
                        break;
                    case XmlPullParser.START_TAG:

                        name = parser.getName();
                        if ("dt".equalsIgnoreCase(name) ) {
                            if (parser.next() == XmlPullParser.TEXT) {

                                currentDefinition = parser.getText();
                                Log.d(TAG, "current def value " + currentDefinition);
                            }

                        }

                        if ("wav".equalsIgnoreCase(name)) {

                            if (parser.next() == XmlPullParser.TEXT) {

                                sound = parser.getText();
                                Log.d(TAG, "current sound value" + sound);

                            }

                        }
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG,"END_TAG");
                        name = parser.getName();
                        if (name.equalsIgnoreCase("def") && currentDefinition != null){
                            defs.add(currentDefinition);
                            Log.d(TAG, "Adding Definition to JSON");
                            defsJSON.put(currentDefinition);

                        }
                        if (name.equalsIgnoreCase("sound") & sound != null){
                            sounds.add(sound);
                            Log.d(TAG, "Adding Sounds to JSON");
                            soundsJSON.put(sound);
                        }
                }
                eventType = parser.next();
            }

           //copy values
           myDefs = (ArrayList<String>) defs.clone();
           mySounds = (ArrayList<String>)sounds.clone();

           try {

               Log.d(TAG, "String JSON in JSON JOBJECT");
               //add to json object
               jObject.put("sounds", soundsJSON);
               jObject.put("defs", defsJSON);

           } catch (JSONException e) {
               e.printStackTrace();
           }
          // printProducts(myDefs);
          // printProducts(mySounds);



       } catch (XmlPullParserException e) {

           e.printStackTrace();
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }

        Log.d(TAG, "Returning JSONObject String ");
        return jObject.toString();
    }


    @Override
    protected void onPostExecute(String strFromDoInBg) {

        Log.d(TAG, "Entering Post Execute");

        Intent i = new Intent(context, DictionaryActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra("Dictionary", strFromDoInBg);
        i.putExtra("WordName",word);
        Log.d(TAG, "starting Other Activity");
        context.startActivity(i);


    }


}