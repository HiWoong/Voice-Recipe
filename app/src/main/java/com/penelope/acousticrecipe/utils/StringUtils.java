package com.penelope.acousticrecipe.utils;

import java.util.List;

public class StringUtils {

    public static String join(List<String> strings, String joint) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i != strings.size() - 1) {
                sb.append(joint);
            }
        }

        return sb.toString();
    }

    public static String getHashTagString(List<String> tags) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tags.size(); i++) {
            sb.append("#").append(tags.get(i));
            if (i != tags.size() - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
