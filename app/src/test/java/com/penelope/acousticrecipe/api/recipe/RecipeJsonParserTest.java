package com.penelope.acousticrecipe.api.recipe;

import junit.framework.TestCase;

import java.util.Arrays;

public class RecipeJsonParserTest extends TestCase {

    public void testParse() {

        String str = "first line\\nsecond line";
        String[] lines = str.split("\\\n");
        String[] lines2 = str.split("\\\\n");

        System.out.println(str);
        System.out.println(Arrays.toString(lines));
        System.out.println(Arrays.toString(lines2));
    }
}