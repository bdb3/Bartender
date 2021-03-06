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
import java.util.List;
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

    //these two constants used for advanced search url building
    public static final String BASE_ADV_SEARCH = "https://addb.absolutdrinks.com/";
    public static final String apiKey = "bb66369811204fb395a943c7008414df";
    protected static URL storedUrl = null;

    public static URL makeURL (){
        if (!(storedUrl == null)){
            return storedUrl;
        }

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

    public static URL encodeToUrl(String string){
        Uri uri = Uri.parse(string).buildUpon()
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

            //Log.v(TAG,"Made it before array of ingredients");

            //Ingredients node is a json array
            JSONArray ingredients = drink.getJSONArray("ingredients");
            List<String> ingred = new ArrayList();
            for(int k = 0;k<ingredients.length();k++){
                JSONObject ingr =ingredients.getJSONObject(k);
                String ing = ingr.getString("textPlain");
                ingred.add(ing);
                //Log.v(TAG,ing );
            }

            //Occasions node is a json array
            JSONArray occasions= drink.getJSONArray("occasions");
            List<String> occasion = new ArrayList();
            for(int j = 0;j<occasions.length();j++){
                JSONObject ingr =occasions.getJSONObject(j);
                String occ = ingr.getString("text");
                occasion.add(occ);
                //Log.v(TAG,"Occa: "+ occ );
            }

            //Tastes node is a json array
            JSONArray tastes = drink.getJSONArray("tastes");
            List<String> taste = new ArrayList();
            for(int z = 0;z<tastes.length();z++){
                JSONObject ingr =tastes.getJSONObject(z);
                String tas = ingr.getString("text");
                taste.add(tas);
                //Log.v(TAG,"Taste: "+ tas );
            }


            //used for debugging
            Log.v(TAG,"We made it to parsing");
            Log.v(TAG,"Drink Name: " + name);
            //Log.v(TAG,"Drink description: " + description);
            //Log.v(TAG,"Drink Color: " + color);
            //Log.v(TAG,"Drink rating: " + rating);

            DrinkItem info = new DrinkItem(id,name,description,color,skillname,rating,ingred,occasion,taste);
            result.add(info);
        }
        return result;
    }

    public static void parseJsonAdvancedSearch(String json) throws JSONException{

        ArrayList<DrinkItem> result = new ArrayList<>();
        JSONObject main = new JSONObject(json);
        JSONArray items = main.getJSONArray("result");
        //JSONArray skills = main.getJSONArray("skill");

        for(int i=0; i < items.length(); i++){
            JSONObject drink = items.getJSONObject(i);

            JSONObject glassResult = drink.getJSONObject("servedIn");
            FragSearch.allGlasses.add(glassResult.getString("text"));
            FragSearch.glassMap.put(glassResult.getString("text"), glassResult.getString("id"));

            JSONArray tasteArray = drink.getJSONArray("tastes");
            for (int j = 0; j < tasteArray.length(); j++){
                JSONObject tasteText = tasteArray.getJSONObject(j);
                FragSearch.allTastes.add(tasteText.getString("text"));

                //key ("text, "id")
                FragSearch.tasteMap.put(tasteText.getString("text"), tasteText.getString("id"));
            }

            JSONObject skillResult = drink.getJSONObject("skill");
            FragSearch.allSkills.add(skillResult.getString("name"));

            JSONArray timeArray = drink.getJSONArray("occasions");
            for (int j = 0; j < timeArray.length(); j++){
                JSONObject timeText = timeArray.getJSONObject(j);
                FragSearch.allTimes.add(timeText.getString("text"));
                FragSearch.timeMap.put(timeText.getString("text"), timeText.getString("id"));
            }

            FragSearch.allColors.add(drink.getString("color"));

        }

    }

    public static URL makeAdvancedSearchUrl (String drinkName, String ingredient, String skill, String taste, String glass, String time, String color){

        String searchParams = "";

        if (!drinkName.equals("")){
            drinkName = drinkName.trim();
            int spaceIndex = drinkName.indexOf(" ");
            if (spaceIndex != -1) drinkName = drinkName.substring(0, spaceIndex);
            searchParams= "quickSearch/drinks/" + drinkName.toLowerCase() + "/";
        } else {

            searchParams= "drinks/";
            if (!ingredient.equals("")){
                ingredient = ingredient.trim();
                searchParams= searchParams + "with/" + ingredient.toLowerCase() + "/";
            }
            if (!skill.equals("-All-")){
                searchParams= searchParams + "skill/" + skill.toLowerCase() + "/";
            }
            if (!color.equals("-All-")){
                searchParams= searchParams + "colored/" + color.toLowerCase() + "/";
            }
            if (!taste.equals("-All-")){
                String tasteID = FragSearch.tasteMap.get(taste);
                searchParams= searchParams + "tasting/" + tasteID + "/";
            }
            if (!glass.equals("-All-")){
                String glassID = FragSearch.glassMap.get(glass);
                searchParams= searchParams + "servedin/" + glassID + "/";
            }
            if (!time.equals("-All-")){
                String timeID = FragSearch.timeMap.get(time);
                searchParams= searchParams + "for/" + timeID + "/";
            }
        }


        Uri builtUri = Uri.parse(BASE_ADV_SEARCH).buildUpon()
                .appendPath(searchParams)
                .appendQueryParameter("apiKey", apiKey).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            storedUrl = url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);

        return url;
    }

    public static URL getStoredUrl(){
        return storedUrl;
    }

    public static void resetStoredUrl(){
        storedUrl = null;
    }

    public static boolean zeroDrinkResultsInJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        int numResults = jsonObject.getInt("totalResult");
        if (numResults == 0) return true;
        return false;
    }
}
