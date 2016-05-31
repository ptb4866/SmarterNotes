package edu.ucla.cs.sourcecodes.dictionary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;




public class DictionaryQuery {


    private String apiKeyLearners = "b2c6f3b2-6190-41fe-990c-11a38f4b53a4";
    private  String apikeyStudents = "2db0944a-e571-4dd7-969e-694466694873";

    private String TAG = "DictionaryQuery.java";
    static InputStream is = null;

    public DictionaryQuery() {

    }


    public InputStream getXMLStream (String word) {

        String s = word + "?" + "key=" + apiKeyLearners ;

        return  getXMLInputStream("http://www.dictionaryapi.com/api/v1/references/learners/xml/"+s);
    }


    private InputStream getXMLInputStream(String urlString) {

        // make HTTP request
        URL url = null;
        try {
            url = new java.net.URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Response Code: " + conn.getResponseCode());

            is = new BufferedInputStream(conn.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }

    public String getXMLData(String word) {
        String s = word + "?" + "key=" + apiKeyLearners ;

        InputStream inStream = getXMLInputStream("http://www.dictionaryapi.com/api/v1/references/learners/xml/"+s);


        StringBuilder sb = null;
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();


        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        return sb != null ? sb.toString() : null;


    }



    /**
     * Query a URL for their source code.
     * @param url The page's URL.
     * @return The source code.
     */
/*    private String getXML(String url) {


        URL dictionaryAPI;
        URLConnection dc;
        StringBuilder s = null;
        try {
            dictionaryAPI = new URL(url);
            dc = dictionaryAPI.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(dc.getInputStream(), "UTF-8"));
            String inputLine;
            s = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                s.append(inputLine);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s != null ? s.toString() : null;
    }

    */


}


