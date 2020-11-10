package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores data for records activity and allows read/write access in Shared Preferences
 */

public class RecordsConfig {
    private static final String NAME_KEY = "list_key1";
    private static final String RESULT_KEY = "list_key2";
    private static final String DATE_KEY = "list_key3";
    private static final String IMG_KEY = "list_key4";
    private static final String CHOICE_KEY = "list_key5";
    // save list as json
    public static void writeResultInPref(Context context, List<String> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(RESULT_KEY, jsonString);
        editor.apply();
    }
    //read list
    public static List<String> readResultFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(RESULT_KEY, "");
        if (jsonString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> list = gson.fromJson(jsonString, type);
            return list;
        }
        else
        {
            return null;
        }
    }

    public static void writeNameInPref(Context context, List<String> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME_KEY, jsonString);
        editor.apply();
    }
    //read list
    public static List<String> readNameFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(NAME_KEY, "");
        if (jsonString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> list = gson.fromJson(jsonString, type);
            return list;
        }
        else
        {
            return null;
        }
    }

    public static void writeDateInPref(Context context, List<String> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(DATE_KEY, jsonString);
        editor.apply();
    }
    //read list
    public static List<String> readDateFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(DATE_KEY, "");
        if (jsonString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> list = gson.fromJson(jsonString, type);
            return list;
        }
        else
        {
            return null;
        }
    }

    public static void writeImageInPref(Context context, List<Integer> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(IMG_KEY, jsonString);
        editor.apply();
    }
    //read list
    public static List<Integer> readImageFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(IMG_KEY, "");
        if (jsonString!=null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
            List<Integer> list = gson.fromJson(jsonString, type);
            return list;
        }
        else
        {
            return null;
        }
    }
    public static void writeChoiceInPref(Context context, List<String> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CHOICE_KEY, jsonString);
        editor.apply();
    }
    //read list
    public static List<String> readChoiceFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(CHOICE_KEY, "");
        if (jsonString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> list = gson.fromJson(jsonString, type);
            return list;
        }
        else
        {
            return null;
        }

    }
}
