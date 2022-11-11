package com.example.assignment_pro1121_nhom3.utils;

public class CapitalizeWord {
    public static String CapitalizeWords(String originalString) {
        if (originalString.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String[] words = originalString.trim().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        for (int i = 0; i < words.length; i++) {
            result.append(words[i]).append(" ");
        }
        return result.toString();
    }
}
