package com.penelope.acousticrecipe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrefUtils {

    public static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static List<String> getVisitedRecipes(Context context) {
        String history = getPref(context).getString("visited", "");
        String[] split = history.split(",");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static void addVisitedRecipe(Context context, String id) {
        List<String> visited = getVisitedRecipes(context);
        while (visited.contains(id)) {
            visited.remove(id);
        }
        visited.add(id);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < visited.size(); i++) {
            sb.append(visited.get(i));
            if (i != visited.size() - 1) {
                sb.append(",");
            }
        }
        getPref(context).edit().putString("visited", sb.toString()).apply();
    }

}
