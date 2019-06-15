package com.example.android.newsappstage1;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public static final String LOG_TAG = NewsActivity.class.getName();

    private QueryUtils(){
        //KENOS KAI PRIVATE
        //KANEIS DE PREPEI NA FTIAKSEI QUERYUTILS OBJECT
    }

    public static List<Story> fetchNewsData(String requestUrl){
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to theURL and receive a json response back
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Story> stories = extractFeatureFromJSON(jsonResponse);

        //Return List of stories
        return stories;
    }

    private static URL createUrl(String requestUrl){
        URL url = null;
        try{
            url = new URL(requestUrl);//KANOUME TRY MONO KAI MONO GIA NA DIMIOURGISOUME TO URL OBJECT
        }catch(MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse="";
        //if the URL is null , then return early.
        if(url ==null) return jsonResponse;

        //an URL oxi null
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            //kathe mia apo tis akolouthes methodous mporei na petaksei IOException
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET"); // ti theloume na kanoume me to http request. Edw theloume na paroume (GET) apo to server
            urlConnection.connect();//edw ginetai kanonika i sundesi

            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: "+ urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }finally {
            //EDW PREPEI NA KLEISOUME TI SUNDESI KAI TO INPUTSTREAM
            if (urlConnection != null)
                urlConnection.disconnect();//stamatame ti sundesi giati pirame auto pou thelame

            if(inputStream!=null)
                inputStream.close();//closing it could throw IOException
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Story> extractFeatureFromJSON(String newsJSON){
        if(TextUtils.isEmpty(newsJSON)) return null; //an dld to JSON den exei tpt mesa

        ArrayList<Story> stories = new ArrayList<>();
        try{
            JSONObject root = new JSONObject(newsJSON);
            JSONObject response = root.getJSONObject("response");
            JSONArray articles = response.getJSONArray("results");
            for(int i=0; i<articles.length(); i++){
                JSONObject story = articles.getJSONObject(i);
                String title = story.optString("webTitle");
                String section = story.optString("sectionName");
                String date = story.optString("webPublicationDate");
                String url = story.optString("webUrl");
                JSONArray tags = story.optJSONArray("tags");
                String author= "";
                if(tags.length()>0){
                JSONObject tag = tags.getJSONObject(0);
                author = tag.optString("webTitle");
                }
                Story s = new Story(title, section, date, url, author);
                stories.add(s);
            }
        }catch(JSONException e){
            //KANOUME CATCH TO ERROR POU MPOREI NA DIMIOURGITHEI APO PANW WSTE NA MIN CRASHAREI I EFARMOGI
            Log.e("QueryUtils", "Problem parsing the Elon Musk news JSON results", e);
        }
        return stories;
    }



}
