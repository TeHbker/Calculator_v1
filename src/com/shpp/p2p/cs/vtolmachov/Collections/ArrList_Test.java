package com.shpp.p2p.cs.vtolmachov.Collections;

import java.util.LinkedList;

public class ArrList_Test{
    public static void main(String[] args){
        run();
    }

    public static void run(){
        ArrList<Integer> list = new ArrList<>();
        list.add(1);
        list.add(2);
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        LinkedList<Integer> linkedList = new LinkedList();
    }
}
