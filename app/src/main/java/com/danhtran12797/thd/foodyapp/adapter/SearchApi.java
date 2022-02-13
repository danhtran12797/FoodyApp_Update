package com.danhtran12797.thd.foodyapp.adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchApi {
    public ArrayList<String> autoComplete(String input) {
        ArrayList<String> arrayList = new ArrayList();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://avapp.000webhostapp.com/foody/server/GetProductFromSearch.php?");
            sb.append("key_name=" + input);
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.d("CCC", "e1: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("CCC", "e2: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
//            JSONObject jsonObject=new JSONObject(jsonResult.toString());
            JSONArray prediction = new JSONArray(jsonResult.toString());
            for (int i = 0; i < prediction.length(); i++) {
                arrayList.add(prediction.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            Log.d("CCC", "e3: " + e.getMessage());
            e.printStackTrace();
        }

        return arrayList;
    }
}
