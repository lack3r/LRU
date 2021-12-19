package io.qbeat.lru;

import java.util.Map;
import java.util.TreeMap;

public class TreeCache<K>
{
    // We need this, for the ordering of our list. The DoubleLinkedList is the perfect structure since we could:
    // Add an element on the top or tail in constant time.
    // Move an element to the top of the list, given that we have the node, again in constant time
    private TreeMap<K, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> cache = new TreeMap<>();
    final private String keyName;

    public TreeCache(String keyName) {
        this.keyName = keyName;
    }

    public ElementWithPriorityAndExpiryTimestamp deleteLastElementFromCacheForKey(K key){
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForFirstPriority = cache.get(key);
        ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndTimestamp = cacheForFirstPriority.removeLast();
        if (cacheForFirstPriority.isEmpty()) {
            cache.remove(key);
        }
        return elementWithPriorityAndTimestamp;
    }

    public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> insertToTheTop(K key, ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp){
        if (!cache.containsKey(key)) {
            cache.put(key , new DoubleLinkedList<>());
        }

        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForUpdatedPriority = cache.get(key);
        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> node = cacheForUpdatedPriority.putFirst(elementWithPriorityAndExpiryTimestamp);
        return node;
    }

    public void delete(K key, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode) {
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> partialCacheForKey = cache.get(key);
        partialCacheForKey.remove(priorityCacheNode);
        if (partialCacheForKey.isEmpty()) {
            cache.remove(key);
        }
    }

    public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> moveToTop(K currentKey, K updatedKey, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode) {
        ElementWithPriorityAndExpiryTimestamp element = partialCacheNode.getElement();
        if (currentKey != updatedKey) {
            partialCacheNode = addToDifferentCache(currentKey, updatedKey, partialCacheNode, element);
        } else {
            moveToTopForKey(updatedKey, partialCacheNode);
        }
        return partialCacheNode;
    }

    private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> addToDifferentCache(K currentKey, K updatedKey, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode, ElementWithPriorityAndExpiryTimestamp element) {
        // Remove element from list
        delete(currentKey, partialCacheNode);
        partialCacheNode = addFirstAndCreateCacheIfNotExist(updatedKey, element);
        return partialCacheNode;
    }

    public void moveToTopForKey(K key, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> node) {
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForKey = cache.get(key);
        cacheForKey.moveToTheTop(node);
    }

    private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> addFirstAndCreateCacheIfNotExist(K key, ElementWithPriorityAndExpiryTimestamp element) {
        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode;
        if (!cache.containsKey(key)) {
            cache.put(key, new DoubleLinkedList<>());
        }
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> partialCacheForKey = cache.get(key);
        partialCacheNode = partialCacheForKey.putFirst(element);
        return partialCacheNode;
    }

    @Override
    public String toString() {
        if (cache.size() == 0) {
            return "Cache is empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> entry : cache.entrySet()) {
            sb.append(keyName + " " + entry.getKey() + ": ");
            sb.append(entry.getValue().toString());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    public int size(){
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public K firstKey() {
        return cache.firstKey();
    }

    public DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> get(K key) {
        return cache.get(key);
    }

    public DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> remove(K key) {
        return cache.remove(key);
    }
}
