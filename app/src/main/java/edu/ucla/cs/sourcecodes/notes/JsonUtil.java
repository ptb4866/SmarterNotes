package edu.ucla.cs.sourcecodes.notes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.ucla.cs.sourcecodes.notes.SessionData;
import edu.ucla.cs.sourcecodes.notes.SessionDataMap;


public class JsonUtil {
    public static String toJson(SessionDataMap sessionData)
    {
        try {
            JSONObject sessionPackage = new JSONObject();

            //store cur session name
            sessionPackage.put("currentSession",sessionData.getCurSessionName());

            //package json array for session data
            JSONArray sDataArray = new JSONArray();
            ArrayList<SessionData> sessionArray = sessionData.getSessionList();
            for (SessionData sd : sessionArray) {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("sessionName", sd.getSessionName());
                JSONArray jsonArr = new JSONArray();

                for (String wd : sd.getWordList()) {
                    JSONObject wdObj = new JSONObject();
                    wdObj.put("word", wd);
                    jsonArr.put(wdObj);
                }

                jsonObj.put("wordList", jsonArr);
                sDataArray.put(jsonObj);
            }

            //put in session data
            sessionPackage.put("sessionData",sDataArray);

            return sessionPackage.toString();
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionDataMap toSdata(String string)
    {
        SessionDataMap sData = new SessionDataMap();
        try {
            JSONObject sessionPackage = new JSONObject(string);
            sData.setCurSessionName(sessionPackage.getString("currentSession"));
            JSONArray jSessionArray = sessionPackage.getJSONArray("sessionData");
            for (int iArray = 0; iArray < jSessionArray.length(); iArray++)
            {
                SessionData sDat = new SessionData();
                JSONObject jObj = jSessionArray.getJSONObject(iArray);
                sDat.setSessionName(jObj.getString("sessionName"));

                JSONArray jArr = jObj.getJSONArray("wordList");
                ArrayList<String> wordList = new ArrayList<>();

                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    wordList.add(obj.getString("word"));
                }
                sDat.setWordList(wordList);
                sData.setSession(sDat.getSessionName(),sDat);
            }
            return sData;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return sData;
    }


}
