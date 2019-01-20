package com.ajeetsingh.java.memory;

import java.util.ArrayList;
import java.util.List;

public class StringMemoryLeak {
    static final int MB = 1024*512;

    static String createLongString(int length){
        StringBuilder sb = new StringBuilder(length);
        for(int i=0; i < length; i++)
            sb.append('a');
        sb.append(System.nanoTime());
        return sb.toString();
    }

    public static void main(String[] args){
        List<String> substrings = new ArrayList<>();
        for(int i=0; i< 1000_000_000; i++){
            String longStr = createLongString(MB);
            String subStr = longStr.substring(1,100);
            substrings.add(subStr);
        }
    }
}
