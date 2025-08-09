package com.shpp.p2p.cs.vtolmachov.Collections;

public class ArrList<E>{
    private E[]  array;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public ArrList(){
        array = (E[]) new Object[size];
    }

    @SuppressWarnings("unchecked")
    public void add(E e){
        E[] newArr = (E[]) new Object[size + 1];
        for (int i = 0; i < size; i++){
            newArr[i] = array[i];
        }
        System.arraycopy(array, 0, newArr, 0, size);
        newArr[size] = e;

        array = newArr;
        size++;

    }

    public E get(int i){
        return array[i];
    }

    public void set(int i, E e){
        array[i] = e;
    }

    public void remove(int i){
        for(; i < size; i++){
            array[i] = array[i + 1];
        }
        array[size - 1] = null;
        size--;
    }
}
