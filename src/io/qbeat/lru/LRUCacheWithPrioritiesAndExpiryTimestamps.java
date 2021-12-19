package io.qbeat.lru;

import java.util.HashMap;

public class LRUCacheWithPrioritiesAndExpiryTimestamps {
    private final int capacity;
    private final LRUTime lruTime;

    /**
     * An implementation of the Last Recently Used (LRU) Cache
     * Get has timeComplexity O(n)
     * Set has timeComplexity O(logn)
     * Does not support concurrency.
     *
     * @param capacity The capacity the Cache should have
     */
    public LRUCacheWithPrioritiesAndExpiryTimestamps(int capacity, LRUTime lruTime) {
        this.capacity = capacity;
        this.lruTime = lruTime;
    }

    class NodesPair{
        // The two nodes point to the same element.
        // However, we need both nodes, since they represent nodes in different lists
        private final DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode;
        private final DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode;

        NodesPair(DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode) {
            this.priorityCacheNode = priorityCacheNode;
            this.expiryCacheNode = expiryCacheNode;
        }

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> getPriorityCacheNode() {
            return priorityCacheNode;
        }

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> getExpiryCacheNode() {
            return expiryCacheNode;
        }
    }

    // We need this, in order to be able to find an element in our cache in constant O(1) time
    private final HashMap<String, NodesPair> hashmapWithNodes = new HashMap<>();
    private final TreeCache<Integer> prioritiesCache = new TreeCache<>("Priority");
    private final TreeCache<Long> expiryTimestampsCache = new TreeCache<>("ExpiryTimestamp");

    // Time complexity: O(1)
    public Integer get(String key) {
        NodesPair nodesPair;
        // O(1)
        if (hashmapWithNodes.containsKey(key)) {
            nodesPair = hashmapWithNodes.get(key);
        } else {
            return null;
        }

        // O(1)
        moveToTheTopInBothTreeCaches(nodesPair);
        return nodesPair.getPriorityCacheNode().getElement().getValue();
    }

    private void moveToTheTopInBothTreeCaches(NodesPair nodesPair) {
        final ElementWithPriorityAndExpiryTimestamp element = nodesPair.getPriorityCacheNode().getElement();
        prioritiesCache.moveToTopForKey(element.getPriority(), nodesPair.getPriorityCacheNode());

        // These line might not be needed.
        expiryTimestampsCache.moveToTopForKey(element.getExpiryTimestamp(), nodesPair.getExpiryCacheNode());
    }

    // Time complexity: O(logn)
    public void set(String key, int value, int priority, int expiryTimestamp) {
        if (hashmapWithNodes.containsKey(key)) {
            updateElementAndMoveToTheTopInCorrespondingTreeCaches(key, value, priority, expiryTimestamp);
        } else {
            dropElementIfCapacityIsReached();
            insertToTheTop(key, value, priority, expiryTimestamp);
        }
    }

    private void dropElementIfCapacityIsReached() {
        if (size() == capacity) {
            removeExpiredItemOrItemWithLowestPriorityOrLastUsed();
        }
    }

    private void updateElementAndMoveToTheTopInCorrespondingTreeCaches(String key, Integer updatedValue, int updatedPriority, long updatedTimestamp) {
        final NodesPair initialNodesPair = hashmapWithNodes.get(key);

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode = initialNodesPair.getPriorityCacheNode();
        final ElementWithPriorityAndExpiryTimestamp element = priorityCacheNode.getElement();
        int existingPriority = element.getPriority();
        priorityCacheNode = prioritiesCache.moveToTop(existingPriority, updatedPriority, priorityCacheNode);

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode = initialNodesPair.getExpiryCacheNode();
        final long existingExpiryTimestamp = element.getExpiryTimestamp();
        expiryCacheNode = expiryTimestampsCache.moveToTop(existingExpiryTimestamp, updatedTimestamp, expiryCacheNode);

        element.update(updatedValue, updatedPriority, updatedTimestamp);

        NodesPair updatedNodesPair = new NodesPair(priorityCacheNode, expiryCacheNode);
        hashmapWithNodes.put(key, updatedNodesPair);
    }

    public int size() {
        return hashmapWithNodes.size();
    }

    private void removeExpiredItemOrItemWithLowestPriorityOrLastUsed() {

        boolean wasItemRemoved = removeExpiredItemIfAny();
        if (wasItemRemoved){
            return;
        }

        final ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp = prioritiesCache.deleteLastElementFromCacheForKey(prioritiesCache.firstKey());
        expiryTimestampsCache.delete(elementWithPriorityAndExpiryTimestamp.getExpiryTimestamp(),hashmapWithNodes.get(elementWithPriorityAndExpiryTimestamp.getKey()).expiryCacheNode);
        hashmapWithNodes.remove(elementWithPriorityAndExpiryTimestamp.getKey());
    }

    private boolean removeExpiredItemIfAny() {
        if (expiryTimestampsCache.isEmpty()){
            return false;
        }

        final Long lowestExpiryTimestamp = expiryTimestampsCache.firstKey();
        if (lowestExpiryTimestamp < lruTime.getCurrentTimeToEpochMillis() ){
            removeExpiredItem(lowestExpiryTimestamp);
            return true;
        }
        return false;
    }

    private void removeExpiredItem(Long lowestExpiryTimestamp) {
        final ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp = expiryTimestampsCache.deleteLastElementFromCacheForKey(lowestExpiryTimestamp);

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> node = hashmapWithNodes.get(elementWithPriorityAndExpiryTimestamp.getKey()).getPriorityCacheNode();
        int priority = node.getElement().getPriority();
        prioritiesCache.delete(priority, node);
        hashmapWithNodes.remove(node.getElement().getKey());
    }

    private void insertToTheTop(String key, Integer value, int priority, long expiryTimestamp) {
        final ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp = new ElementWithPriorityAndExpiryTimestamp(key, value, priority, expiryTimestamp);
        final DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode = prioritiesCache.insertToTheTop(priority, elementWithPriorityAndExpiryTimestamp);
        final DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryDatetimeCacheNode = expiryTimestampsCache.insertToTheTop(expiryTimestamp, elementWithPriorityAndExpiryTimestamp);

        final NodesPair nodesPair = new NodesPair(priorityCacheNode, expiryDatetimeCacheNode);
        hashmapWithNodes.put(key, nodesPair);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Priorities Cache");
        final String newLine = System.getProperty("line.separator");
        sb.append(newLine);
        sb.append("===============");
        sb.append(newLine);
        sb.append(prioritiesCache.toString());
        sb.append(newLine);
        sb.append(newLine);

        sb.append("Expiry Timestamps Cache");
        sb.append(newLine);
        sb.append("===============");
        sb.append(newLine);
        sb.append(expiryTimestampsCache.toString());

        return sb.toString();
    }

    public static void main(String[] args) {
        final LRUTimeForTests lruTimeForTests = new LRUTimeForTests();
        LRUCacheWithPrioritiesAndExpiryTimestamps lruCacheWithPrioritiesAndExpiryTimestamps = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("A", 1, 7, 8000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("B", 2, 3, 40006);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("C", 3, 7, 8000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("D", 4, 11, 600);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("E", 5, 7, 8010);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.get("C");
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruTimeForTests.setCurrentTimeInEpochMillis(1000L);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("F", 4, 7, 6000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("G", 1, 7, 6010);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("H", 0, 7, 6020);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("I", -1, 7, 6030);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.get("D");
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("A", -3, 8, 7003);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("D", 8, 7, 7015);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.get("F");
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
    }
}
