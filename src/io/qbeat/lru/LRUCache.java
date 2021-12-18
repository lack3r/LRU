package io.qbeat.lru;

import java.util.HashMap;

public class LRUCache {

    private int capacity;

    /**
     * An implementation of the Last Recently Used (LRU) Cache
     * Both Set and Get methods require a constant time
     * Does not support concurrency, priorities or expiry date.
     *
     * @param capacity The capacity the Cache should have
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    // We need this, in order to be able to find an element in our cache in constant O(1) time
    private HashMap<String, DoubleLinkedListNode<Element>> hashmapWithNodes = new HashMap<>();
    // We need this, for the ordering of our list. The DoubleLinkedList is the perfect structure since we could:
    // Add an element on the top or tail in constant time.
    // Move an element to the top of the list, given that we have the node, again in constant time
    private DoubleLinkedList<Element> orderedCache = new DoubleLinkedList<>();

    // Time complexity: O(1)
    public Integer get(String key) {
        DoubleLinkedListNode<Element> node;
        // O(1)
        if (hashmapWithNodes.containsKey(key)) {
            node = hashmapWithNodes.get(key);
        } else {
            return null;
        }

        // O(1)
        orderedCache.moveToTheTop(node);

        return node.getElement().getValue();
    }

    // Time complexity: O(1)
    public void set(String key, Integer value) {
        if (hashmapWithNodes.containsKey(key)) {
            moveToTheTop(key, value);
        } else {
            if (size() == capacity) {
                // Drop element to make capacity
                removeLast();
            }
            insertToTheTop(key, value);
        }
    }

    private void moveToTheTop(String key, Integer value) {
        DoubleLinkedListNode<Element> elementDoubleLinkedListNode = hashmapWithNodes.get(key);
        elementDoubleLinkedListNode.getElement().updateValue(value);
        orderedCache.moveToTheTop(elementDoubleLinkedListNode);
        hashmapWithNodes.put(key, elementDoubleLinkedListNode);
    }

    public int size() {
        return hashmapWithNodes.size();
    }

    private void removeLast() {
        Element element = orderedCache.removeLast();
        hashmapWithNodes.remove(element.getKey());
    }

    private void insertToTheTop(String key, Integer value) {
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
        // We expect C to be moved to the top of the cache, since we have requested it.
        lruCache.get("C");
        System.out.println(lruCache);
        // We expect the last used element (A) to be removed here.
        lruCache.set("F", 4);
        System.out.println(lruCache);
        lruCache.set("E", 2);
        System.out.println(lruCache);
    }
}
