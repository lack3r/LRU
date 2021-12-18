package io.qbeat.lru;

import java.util.HashMap;

public class LRUCache {

    private int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    private HashMap<String, DoubleLinkedListNode<Element>> hashmapWithNodes = new HashMap<>();
    private DoubleLinkedList<DoubleLinkedListNode<Element>>  orderedCache = new DoubleLinkedList<>();

    // O(1)
    public Integer get(String key) {
        DoubleLinkedListNode<Element> value;
        // O(1)
        if (hashmapWithNodes.containsKey(key)) {
            value = hashmapWithNodes.get(key);
        } else {
            return null;
        }

        // O(1)
        orderedCache.moveToBeginning(value);

        return value.element.getValue();
    }

    // O(1)
    public void set(String key, Integer value) {
        if (size() == capacity) {
            // Drop element to make capacity
            removeLast();
        }

        insertFirst(key, value);
    }

    public int size() {
        return hashmapWithNodes.size();
    }

    private void removeLast() {
        Element element = orderedCache.removeLast();
        hashmapWithNodes.remove(element.getKey());
    }

    private void insertFirst(String key, Integer value) {
        DoubleLinkedListNode<Element> node = orderedCache.putFirst(new Element(key, value));
        hashmapWithNodes.put(key, node);
    }

    @Override
    public String toString() {
        return orderedCache.toString();
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(5);
        System.out.println(lruCache);
        lruCache.set("A", 5);
        System.out.println(lruCache);
        lruCache.set("B", 4);
        System.out.println(lruCache);
        lruCache.set("C", 3);
        System.out.println(lruCache);
        lruCache.set("D", 2);
        System.out.println(lruCache);
        lruCache.set("E", 1);
        System.out.println(lruCache);
        lruCache.get("C");
        System.out.println(lruCache);
        lruCache.set("F", 4);
        System.out.println(lruCache);

    }
}
