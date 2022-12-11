package com.penelope.acousticrecipe.utils;

import com.penelope.acousticrecipe.data.recipe.CookingMethod;
import com.penelope.acousticrecipe.data.recipe.FoodType;
import com.penelope.acousticrecipe.data.recipe.Recipe;

public class NameUtils {

    public static FoodType getFoodType(String name) {
        switch (name) {
            case "밥":
                return FoodType.MAIN;
            case "반찬":
                return FoodType.SIDE;
            case "국&찌개":
                return FoodType.SOUP;
            case "후식":
                return FoodType.DESSERT;
            default:
                return FoodType.ETC;
        }
    }

    public static String getFoodTypeName(FoodType foodType) {
        switch (foodType) {
            case MAIN:
                return "밥";
            case SIDE:
                return "반찬";
            case SOUP:
                return "국&찌개";
            case DESSERT:
                return "후식";
            default:
                return "기타";
        }
    }

    public static CookingMethod getCookingMethod(String name) {
        switch (name) {
            case "끓이기":
                return CookingMethod.BOIL;
            case "찌기":
                return CookingMethod.STEAM;
            case "굽기":
                return CookingMethod.ROAST;
            case "튀기기":
                return CookingMethod.FRY;
            default:
                return CookingMethod.ETC;
        }
    }

    public static String getCookingMethodName(CookingMethod cookingMethod) {
        switch (cookingMethod) {
            case BOIL:
                return "끓이기";
            case STEAM:
                return "찌기";
            case ROAST:
                return "굽기";
            case FRY:
                return "튀기기";
            default:
                return "기타";
        }
    }

    public static String getNutrients(Recipe recipe) {

        StringBuilder strNutrients = new StringBuilder();

        int calories = recipe.getCalories();
        int carbohydrates = recipe.getCarbohydrate();
        int protein = recipe.getProtein();
        int fat = recipe.getFat();
        int sodium = recipe.getSodium();

        if (calories != 0) {
            strNutrients.append("열량 ").append(calories).append("kcal");
        }
        if (carbohydrates != 0) {
            if (strNutrients.length() != 0) {
                strNutrients.append(", ");
            }
            strNutrients.append("탄수화물 ").append(carbohydrates).append("g");
        }
        if (protein != 0) {
            if (strNutrients.length() != 0) {
                strNutrients.append(", ");
            }
            strNutrients.append("단백질 ").append(protein).append("g");
        }
        if (fat != 0) {
            if (strNutrients.length() != 0) {
                strNutrients.append(", ");
            }
            strNutrients.append("지방 ").append(fat).append("g");
        }
        if (sodium != 0) {
            if (strNutrients.length() != 0) {
                strNutrients.append(", ");
            }
            strNutrients.append("나트륨 ").append(sodium).append("mg");
        }

        return strNutrients.toString();
    }

}
