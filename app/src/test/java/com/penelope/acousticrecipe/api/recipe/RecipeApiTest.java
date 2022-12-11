package com.penelope.acousticrecipe.api.recipe;

import com.penelope.acousticrecipe.data.recipe.Recipe;

import junit.framework.TestCase;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeApiTest extends TestCase {

    public void testGet() {

        List<Recipe> recipes = RecipeApi.get();
        System.out.println(recipes == null ? "null" : recipes.toString());
    }

    public void testMatch() {

        Pattern pattern = Pattern.compile("([0-9]+)\\s*분");

        String test1 = "대파를 10분 삶아주세요";
        String test2 = "양파를 4~5 분 삶아주세요";

        Matcher m1 = pattern.matcher(test1);
        Matcher m2 = pattern.matcher(test2);

        if (m1.find()) {
            String strNumber = m1.group(1);
            System.out.println("case 1: " + strNumber);
        }
        if (m2.find()) {
            String strNumber = m2.group(1);
            System.out.println("case 2: " + strNumber);
        }
    }
}