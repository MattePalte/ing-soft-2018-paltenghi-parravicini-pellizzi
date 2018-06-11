package project.ing.soft.model;

import java.io.Serializable;

public class Pair<T,V> implements Serializable {
    private T key;
    private V value;

    public Pair(T key, V value){
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
