package com.examsecure.util;

public class Params {

    // Returns the number, or -1 if the text is missing or not a number
    public static int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}