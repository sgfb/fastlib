package com.fastlib;

/**
 * Created by sgfb on 17/1/4.
 */

public class JavaProject{

    public static void main(String[] args){
        String command="delete from 'com.fastlib.Bean' where sex =?";
        System.out.println(command.replaceFirst("[?]","女"));
    }
}