package com.example.android.huntgather;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Aidan on 29/04/2018.
 */

class MyComparator implements Comparator<String> {

    private Map<String, String> map;

    public MyComparator(Map<String, String> map) {
        this.map = map;
    }

    public int compare(String a, String b) {
        return map.get(a).compareTo(map.get(b));
    }
}