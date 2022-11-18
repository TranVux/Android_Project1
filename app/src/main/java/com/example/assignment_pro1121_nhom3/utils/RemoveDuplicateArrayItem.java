package com.example.assignment_pro1121_nhom3.utils;

import android.util.Log;

import java.util.ArrayList;

public class RemoveDuplicateArrayItem {
    public static <O> ArrayList<O> getList(ArrayList<O> originalList) {
        ArrayList<O> newList = new ArrayList<>();
        for (O o : originalList) {
            if (!newList.contains(o)) {
                newList.add(o);
            }
        }
        return newList;
    }
}
