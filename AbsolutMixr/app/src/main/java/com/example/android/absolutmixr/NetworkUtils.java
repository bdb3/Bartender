package com.example.android.absolutmixr;

import android.net.Uri;
import android.util.Log;

import com.example.android.absolutmixr.Model.DrinkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Leonard on 7/12/2017.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    //The url for the ADDb -> lists in JSON
    public static final String Base_Url = "https://addb.absolutdrinks.com/drinks/?apiKey=bb66369811204fb395a943c7008414df";
    //Will be used to call the picture to display using the id from Drink Item and add .png at the end
    public static final String Picture_url = "http://assets.absolutdrinks.com/drinks/transparent-background-white/soft-shadow/floor-reflection/100x200/";
    public static final String Query_Param = "q";

    public static URL makeURL (){
        Uri uri = Uri.parse(Base_Url).buildUpon()
                .build();
        URL url = null;
        try {
            String urlString = uri.toString();
            url = new URL(uri.toString());

        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url +")(*&^%$#@!)(*&^%$#@*&^%$#@*&^%$#@)(*&^%$#@(*&^%$#@)(*&^%$#@");
        return url;
    }

    //Passes a url sting and returns a JSON string
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return  scanner.next();
            }else{
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
// gets the parsed values and places them into the DrinkItem constructor
    public static ArrayList<DrinkItem> parseJSON(String json)throws JSONException {
        ArrayList<DrinkItem> result = new ArrayList<>();
        JSONObject main = new JSONObject(json);
        JSONArray items = main.getJSONArray("result");
        //JSONArray skills = main.getJSONArray("skill");

        for(int i=0; i < items.length(); i++){
            JSONObject drink = items.getJSONObject(i);

            String id =drink.getString("id");
            String name =drink.getString("name");
            String description =drink.getString("descriptionPlain");
            String color =drink.getString("color");
            String rating =drink.getString("rating");

            //Skill node is a json object
            JSONObject skill = drink.getJSONObject("skill");
            String skillname =skill.getString("name");

            Log.v(TAG,"We made it to parsing");
            Log.v(TAG,"Drink Name: " + name);
            Log.v(TAG,"Drink description: " + description);
            Log.v(TAG,"Drink Color: " + color);
            Log.v(TAG,"Drink rating: " + rating);

            DrinkItem info = new DrinkItem(id,name,description,color,skillname,rating);
            result.add(info);
        }
        return result;
    }
}