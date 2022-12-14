package com.example.assignment_pro1121_nhom3.utils;

import java.util.ArrayList;

public class ListFilterUtil {
    public static boolean checkMaxSizeListFilter(ArrayList<String> listID) {
        if (listID.size() > 10) {
            return true;
        }
        return false;
    }
}
