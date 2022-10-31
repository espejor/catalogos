package com.example.catalogos.services;

import java.util.ArrayList;

public class ListFeatures {

    public static boolean listContains(ArrayList<int[]> list, int[] element){
        for (int i = 0; i < list.size (); i++) {
            boolean match = false;

            for (int j = 0; j < list.get (i).length; j++) {
                if (list.get (i)[j] != (element[j])) {   // No coincide
                    match = false;
                    break;
                } else {
                    match = true;
                }
            }
            if (match)
                return true;
        }
        return false;
    }


    public static boolean listContains(ArrayList<String[]> list, String[] element){
        for (int i = 0; i < list.size (); i++) {
            boolean match = false;

            for (int j = 0; j < list.get (i).length; j++) {
                if (list.get (i)[j] == null){
                    if(element[j] == null)
                        match = true;
                    else {
                        match = false;
                        break;
                    }
                }else{
                    if (!list.get (i)[j].equals (element[j])) {   // No coincide
                        match = false;
                        break;
                    }else {
                        match = true;
                    }
                }
            }
            if (match)
                return true;
        }
        return false;
    }
}
