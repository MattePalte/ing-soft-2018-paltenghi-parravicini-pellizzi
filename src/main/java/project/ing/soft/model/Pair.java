package project.ing.soft.model;

import java.io.Serializable;

public class Pair<T extends Serializable,V extends Serializable> implements Serializable {
    private final T key;
    private final V value;

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
