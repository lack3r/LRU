package io.qbeat.lru;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LRUCacheWithPriorities {

    private final int capacity;

    /**
     * An implementation of the Last Recently Used (LRU) Cache
     * Both Set and Get methods require a constant time
     * Does not support concurrency or expiry date.
     *
     * @param capacity The capacity the Cache should have
     */
    public LRUCacheWithPriorities(int capacity) {
        this.capacity = capacity;
    }

    // We need this, in order to be able to find an element in our cache in constant O(1) time
    private final HashMap<String, DoubleLinkedListNode<ElementWithPriority>> hashmapWithNodes = new HashMap<>();

    private final TreeMap<Integer, DoubleLinkedList<ElementWithPriority>> prioritiesMap = new TreeMap<>();

    // Time complexity: O(1)
    public Integer get(String key) {
        DoubleLinkedListNode<ElementWithPriority> node;
        // O(1)
        if (hashmapWithNodes.containsKey(key)) {
            node = hashmapWithNodes.get(key);
        } else {
            return null;
        }

        // O(1)
        moveToTheTopInPriorityList(node);
        return node.getElement().getValue();
    }

    private void moveToTheTopInPriorityList(DoubleLinkedListNode<ElementWithPriority> node) {
        int priority = node.getElement().getPriority();
        DoubleLinkedList<ElementWithPriority> cacheForPriority = prioritiesMap.get(priority);
        cacheForPriority.moveToTheTop(node);
    }

    // Time complexity: O(1)
    public void set(String key, int value, int priority) {
        if (hashmapWithNodes.containsKey(key)) {
            moveToTheTop(key, value, priority);
        } else {
            if (size() == capacity) {
                // Drop element to make capacity
                removeLast();
            }
            insertToTheTop(key, value, priority);
        }
    }

    private void moveToTheTop(String key, Integer value, int updatedPriority) {
        DoubleLinkedListNode<ElementWithPriority> elementDoubleLinkedListNode = hashmapWithNodes.get(key);
        int existingPriority = elementDoubleLinkedListNode.getElement().getPriority();

        elementDoubleLinkedListNode.getElement().update(value, updatedPriority);

        if (existingPriority != updatedPriority) {
            // Remove element from list
            DoubleLinkedList<ElementWithPriority> cacheForPriority = prioritiesMap.get(existingPriority);
            cacheForPriority.remove(elementDoubleLinkedListNode);
            if (cacheForPriority.isEmpty()) {
                prioritiesMap.remove(existingPriority);
            }
            prioritiesMap.putIfAbsent(updatedPriority, new DoubleLinkedList<>());

            DoubleLinkedList<ElementWithPriority> cacheForUpdatedPriority = prioritiesMap.get(updatedPriority);
            elementDoubleLinkedListNode = cacheForUpdatedPriority.putFirst(elementDoubleLinkedListNode.getElement());
        } else {
            DoubleLinkedList<ElementWithPriority> cacheForUpdatedPriority = prioritiesMap.get(updatedPriority);
            cacheForUpdatedPriority.moveToTheTop(elementDoubleLinkedListNode);
        }

        hashmapWithNodes.put(key, elementDoubleLinkedListNode);
    }

    public int size() {
        return hashmapWithNodes.size();
    }

    private void removeLast() {
        DoubleLinkedList<ElementWithPriority> cacheForFirstPriority = prioritiesMap.get(prioritiesMap.firstKey());
        ElementWithPriority elementWithPriority = cacheForFirstPriority.removeLast();
        if (cacheForFirstPriority.isEmpty()) {
            prioritiesMap.remove(prioritiesMap.firstKey());
        }
        hashmapWithNodes.remove(elementWithPriority.getKey());
    }

    private void insertToTheTop(String key, Integer value, int priority) {
        prioritiesMap.putIfAbsent(priority, new DoubleLinkedList<>());


        DoubleLinkedList<ElementWithPriority> cacheForUpdatedPriority = prioritiesMap.get(priority);
        DoubleLinkedListNode<ElementWithPriority> node = cacheForUpdatedPriority.putFirst(new ElementWithPriority(key, value, priority));

        hashmapWithNodes.put(key, node);
    }

    @Override
    public String toString() {
        if (prioritiesMap.size() == 0) {
            return "Cache is empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, DoubleLinkedList<ElementWithPriority>> entry : prioritiesMap.entrySet()) {
            sb.append("Priority ").append(entry.getKey()).append(": ");
            sb.append(entry.getValue().toString());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        LRUCacheWithPriorities lruCacheWithPriorities = new LRUCacheWithPriorities(5);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("A", 5, 4);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("B", 4, 1);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("C", 3, 7);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("D", 2, 8);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("E", 1, 5);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.get("C");
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("F", 4, 5);
        System.out.println(lruCacheWithPriorities);
        lruCacheWithPriorities.set("E", 2, 5);
        System.out.println(lruCacheWithPriorities);
    }
}
