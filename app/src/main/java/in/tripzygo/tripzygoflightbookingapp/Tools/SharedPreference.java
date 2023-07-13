package in.tripzygo.tripzygoflightbookingapp.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;

public class SharedPreference {

    // Avoid magic numbers.
    private static final int MAX_SIZE = 3;


    public SharedPreference() {
        super();
    }

    public static void storeLogin(Context context, String s) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString("login", s);
        editor.apply();
    }

    public static String loadLogin(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        String Login = "Logged out";
        settings = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        if (settings.contains("login")) {
            Login = settings.getString("login", "");
            return Login;
        }
        return Login;
    }

    public static void storeData(Context context, Bundle bundle) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        System.out.println("bundle = " + bundle.getClass());
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch (JSONException e) {
                //Handle exception here
            }
        }
        String data = json.toString();
        editor.putString("bundle", data);
        editor.apply();
    }

    public static void storeUser(Context context, User user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String data = gson.toJson(user);
        editor.putString("user", data);
        editor.apply();

    }

    public static User loadUser(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        User user = new User();
        settings = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (settings.contains("user")) {
            String data = settings.getString("user", null);
            Gson gson = new Gson();
            user = gson.fromJson(data, new TypeToken<User>() {
            }.getType());
        } else
            return user;
        return user;
    }

    public static Bundle loadData(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        Bundle bundle = new Bundle();
        bundle.putString("dataSaved", "notSaved");
        settings = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        if (settings.contains("bundle")) {
            String data = settings.getString("bundle", null);
            System.out.println("data = " + data);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = jsonObject.getString(key);
                    bundle.putString(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            Gson gson = new Gson();
//            bundle = gson.fromJson(data, new TypeToken<Bundle>(){}.getType());
            System.out.println("bundle = " + bundle);
            bundle.putString("dataSaved", "saved");
        } else
            return bundle;
        return bundle;
    }

    public static void storeList(Context context, String pref_name, String key, List countries) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(countries);
        editor.putString(key, jsonFavorites);
        editor.apply();
    }

    public static List<AirportCode> loadList(Context context, String pref_name, String key) {

        SharedPreferences settings;
        List<AirportCode> favorites;
        settings = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        if (settings.contains(key)) {
            String jsonFavorites = settings.getString(key, null);
            Gson gson = new Gson();
            List<AirportCode> favoriteItems = gson.fromJson(jsonFavorites, new TypeToken<List<AirportCode>>() {
            }.getType());
            favorites = favoriteItems;
            favorites = new ArrayList<>(favorites);
        } else
            return null;
        return (List<AirportCode>) favorites;
    }

//    public static void addList(Context context, String pref_name, String key, String exercise_name) {
//        List<String> favorites = loadList(context, pref_name, key);
//        if (favorites == null)
//            favorites = new ArrayList<>();
//
//        if (favorites.size() > MAX_SIZE) {
//            favorites.clear();
//            deleteList(context, pref_name);
//        }
//
//        if (favorites.contains(exercise_name)) {
//
//            favorites.remove(exercise_name);
//
//        }
//        favorites.add(exercise_name);
//
//        storeList(context, pref_name, key, favorites);
//
//    }

    public static void deleteList(Context context, String pref_name) {

        SharedPreferences myPrefs = context.getSharedPreferences(pref_name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.apply();
    }


//    public static void removeList(Context context,String pref_name, String key, String country) {
//        ArrayList favorites = loadList(context, pref_name,key);
//        if (favorites != null) {
//            favorites.remove(country);
//            storeList(context, pref_name, key, favorites);
//        }
//    }


}