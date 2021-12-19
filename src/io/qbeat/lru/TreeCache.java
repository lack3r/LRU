package io.qbeat.lru;

import java.util.Map;
import java.util.TreeMap;

public class TreeCache<K>
{
    private TreeMap<K, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> cache = new TreeMap<>();

    public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> moveToTop(K currentKey, K updatedKey,DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode){
        ElementWithPriorityAndExpiryTimestamp element = partialCacheNode.getElement();
        if (currentKey != updatedKey) {
            partialCacheNode = addToDifferentCache(currentKey, updatedKey, partialCacheNode, element);
        } else {
            moveToTheTopInTheCorrespondingPartialCache(updatedKey, partialCacheNode);
        }
        return partialCacheNode;
}

    public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> addToDifferentCache(K currentKey, K updatedKey, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode, ElementWithPriorityAndExpiryTimestamp element) {
        // Remove element from list
        deleteFromPartialCache(currentKey, partialCacheNode);
        partialCacheNode = addFirstAndCreateCacheIfNotExist(updatedKey, element);
        return partialCacheNode;
    }

    public ElementWithPriorityAndExpiryTimestamp deleteLastElementFromCacheForKey(K key){
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForFirstPriority = cache.get(key);
        ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndTimestamp = cacheForFirstPriority.removeLast();
        if (cacheForFirstPriority.isEmpty()) {
            cache.remove(cache.firstKey());
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

    public void deleteFromPartialCache(K key, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode) {
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> partialCacheForKey = cache.get(key);
        partialCacheForKey.remove(priorityCacheNode);
        if (partialCacheForKey.isEmpty()) {
            cache.remove(key);
        }
    }

    public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> moveToTheTopInCorrespondingPartialCacheOrAddInDifferentOneIfNeeded(K currentKey, K updatedKey, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode) {
        ElementWithPriorityAndExpiryTimestamp element = partialCacheNode.getElement();
        if (currentKey != updatedKey) {
            partialCacheNode = addToDifferentCache(currentKey, updatedKey, partialCacheNode, element);
        } else {
            moveToTheTopInTheCorrespondingPartialCache(updatedKey, partialCacheNode);
        }
        return partialCacheNode;
    }

    public void moveToTheTopInTheCorrespondingPartialCache(K key, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode) {
        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForUpdatedPriority = cache.get(key);
        cacheForUpdatedPriority.moveToTheTop(priorityCacheNode);
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
            sb.append("Key Name " + entry.getKey() + ": ");
            sb.append(entry.getValue().toString());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    public String toStringWithNamedKey(String keyname) {
        if (cache.size() == 0) {
            return "Cache is empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> entry : cache.entrySet()) {
            sb.append(keyname +" "+ entry.getKey() + ": ");
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
