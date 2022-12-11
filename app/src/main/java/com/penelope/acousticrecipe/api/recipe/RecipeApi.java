package com.penelope.acousticrecipe.api.recipe;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.penelope.acousticrecipe.data.recipe.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RecipeApi {

    public static final String URL_FORMAT = "https://openapi.foodsafetykorea.go.kr/api/{SERVICE_KEY}/COOKRCP01/json/1/5";
    public static final String ARG_SERVICE_KEY = "{SERVICE_KEY}";
    public static final String SERVICE_KEY = "a0b50f135a6445839719";

    @WorkerThread
    public static List<Recipe> get() {

        String strUrl = URL_FORMAT
                .replace(ARG_SERVICE_KEY, SERVICE_KEY);

        try {
            // API 를 호출하고 xml 을 파싱한다
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Log.d("TAG", "get: " + json);

            int first = json.indexOf("{");
            int last = json.lastIndexOf("}");
            if (first == -1 || last == -1) {
                return null;
            }

            JSONObject response = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
            return RecipeJsonParser.parse(response);

        } catch (IOException |JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
