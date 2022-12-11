package com.penelope.acousticrecipe.api.recipe;

import com.penelope.acousticrecipe.data.recipe.CookingMethod;
import com.penelope.acousticrecipe.data.recipe.FoodType;
import com.penelope.acousticrecipe.data.recipe.Recipe;
import com.penelope.acousticrecipe.utils.NameUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeJsonParser {

    public static List<Recipe> parse(JSONObject response) {

        List<Recipe> recipes = new ArrayList<>();

        try {
            JSONObject object = response.getJSONObject("COOKRCP01");
            JSONArray row = object.getJSONArray("row");

            for (int i = 0; i < row.length(); i++) {
                JSONObject recipeObject = row.getJSONObject(i);
                Recipe recipe = parseRecipe(recipeObject);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

            return recipes;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Recipe parseRecipe(JSONObject obj) {

        try {
            String id = obj.getString("RCP_SEQ");
            String name = obj.getString("RCP_NM");
            String imageBig = obj.getString("ATT_FILE_NO_MK");
            String imageSmall = obj.getString("ATT_FILE_NO_MAIN");
            String hashtag = obj.getString("HASH_TAG");

            // 요리종류 추출
            String strFoodType = obj.getString("RCP_PAT2");
            FoodType foodType = NameUtils.getFoodType(strFoodType);

            // 요리방법 추출
            String strCookingMethod = obj.getString("RCP_WAY2");
            CookingMethod cookingMethod = NameUtils.getCookingMethod(strCookingMethod);

            // 영양정보 추출
            String strCalories = obj.has("INFO_ENG") ? obj.getString("INFO_ENG") : null;
            String strCarbohydrate = obj.has("INFO_CAR") ? obj.getString("INFO_CAR") : null;
            String strProtein = obj.has("INFO_PRO") ? obj.getString("INFO_PRO") : null;
            String strFat = obj.has("INFO_FAT") ? obj.getString("INFO_FAT") : null;
            String strSodium = obj.has("INFO_NA") ? obj.getString("INFO_NA") : null;
            int calories = (strCalories == null || strCalories.isEmpty()) ? 0 : Integer.parseInt(strCalories);
            int carbohydrate = (strCarbohydrate == null || strCarbohydrate.isEmpty()) ? 0 : Integer.parseInt(strCarbohydrate);
            int protein = (strProtein == null || strProtein.isEmpty()) ? 0 : Integer.parseInt(strProtein);
            int fat = (strFat == null || strFat.isEmpty()) ? 0 : Integer.parseInt(strFat);
            int sodium = (strSodium == null || strSodium.isEmpty()) ? 0 : Integer.parseInt(strSodium);

            // 재료 추출
            String strIngredients = obj.getString("RCP_PARTS_DTLS");
            String[] lines = strIngredients.split("\\n");
            List<String> ingredients = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                if (i % 2 == 1) {
                    String[] words = lines[i].split(",");
                    for (String word : words) {
                        ingredients.add(word.trim());
                    }
                }
            }
            // 매뉴얼 추출
            List<String> manuals = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                String key = String.format(Locale.getDefault(), "MANUAL%02d", i + 1);
                String strManual = obj.getString(key);
                if (strManual.trim().isEmpty()) {
                    break;
                }
                manuals.add(strManual);
            }

            // 매뉴얼 이미지 추출
            List<String> manualImages = new ArrayList<>();
            for (int i = 0; i < manuals.size(); i++) {
                String key = String.format(Locale.getDefault(), "MANUAL_IMG%02d", i + 1);
                String strManualImage = obj.getString(key);
                manualImages.add(strManualImage);
            }

            return new Recipe(
                    id, name, imageBig, imageSmall, ingredients,
                    manuals, manualImages,
                    foodType, cookingMethod, hashtag,
                    calories, carbohydrate, protein, fat, sodium
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
