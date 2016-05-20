package edu.ucla.cs.sourcecodes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import edu.ucla.cs.sourcecodes.ApiKeys;

/**
 * Makes the Google Translate API available to Java applications.
 *
 * @author Richard Midwinter
 * @author Emeric Vernat
 * @author Juan B Cabral
 * @author Cedric Beust
 */
public class Translate {

    private static final String ENCODING = "utf-8";

    private static String apiKey = ApiKeys.PETERS_GOOGLE_TRANSLATOR_API_KEY;

    private static final String URL_STRING = "https://www.googleapis.com/language/translate/v2?key=" + apiKey;

    private static final String TEXT_VAR1 = "&source=";
    private static final String TEXT_VAR2 = "&target=";
    private static final String TEXT_VAR3 = "&q=";

    private static String TAG = "Translate";
    /**
     * Translates text from a given language to another given language using Google Translate
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to The language code to translate to.
     * @return The translated String.
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String translate(String text, String from, String to) throws Exception {
        return retrieveTranslation(text, from, to);
    }

    /**
     * Forms an HTTP request and parses the response for a translation.
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to The language code to translate to.
     * @return The translated String.
     * @throws Exception
     */
    private static String retrieveTranslation(String text, String from, String to) throws Exception {

        String translatedText = null;
        try {

            StringBuilder url = new StringBuilder();
            url.append(URL_STRING).append(TEXT_VAR1).append(from);
            url.append(TEXT_VAR2).append(to);
            url.append(TEXT_VAR3).append(URLEncoder.encode(text, ENCODING));

            Log.d(TAG, "Connecting to " + url.toString());
            HttpURLConnection uc = (HttpURLConnection) new URL(url.toString()).openConnection();

            try {
                uc.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            uc.connect();


            int response = uc.getResponseCode();

            if (response != 200) {

              Log.d(TAG,"Response code is " + Integer.toString(response) + "Error!!!");
            }


           // uc.setDoInput(true);
          //  uc.setDoOutput(true);
            try {

                InputStream is= uc.getInputStream();
                String result = toString(is);
                JsonParser parser = new JsonParser();

                JsonElement element = parser.parse(result.toString());

                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.get("error") == null) {
                        translatedText = obj.get("data").getAsJsonObject().
                                get("translations").getAsJsonArray().
                                get(0).getAsJsonObject().

                                get("translatedText").getAsString();

                        Log.d(TAG, "TranslatedText " + translatedText);
                       // return translatedText;

                    }
                }



            } finally { // http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html
                uc.getInputStream().close();
                if (uc.getErrorStream() != null) uc.getErrorStream().close();
            }
        } catch (Exception ex) {
            throw ex;
        }

        return translatedText;
    }

    /**
     * Reads an InputStream and returns its contents as a String. Also effects rate control.
     * @param inputStream The InputStream to read from.
     * @return The contents of the InputStream as a String.
     * @throws Exception
     */
    private static String toString(InputStream inputStream) throws Exception {
        StringBuilder outputBuilder = new StringBuilder();
        try {
            String string;
            if (inputStream != null) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(inputStream, ENCODING));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error reading translation stream.", ex);
        }
        return outputBuilder.toString();
    }
}