package com.service.boot.basis.dao;

public class Pair<K, V> {
    public K key;
    public V value;

    public Pair(K key, V value){
        this.key = key;
        this.value = value;
    }
}
