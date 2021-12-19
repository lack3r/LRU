package io.qbeat.lru;

import java.util.HashMap;
import java.util.Optional;

public class LRUCacheWithPrioritiesAndExpiryTimestamps {
    private int capacity;

    // This does not belong in the LRU.
    // To get the currentEpochTime, we should call the following instead:
    // long now = Instant.now().toEpochMilli();
    // However, this is here just to facilitate unit testing the LRU
    // And for no other purpose
    // TODO: Do this properly: https://stackoverflow.com/a/2425739
    private Optional<Long> currentEpochTime = Optional.empty();

    /**
     * An implementation of the Last Recently Used (LRU) Cache
     * Both Set and Get methods require a constant time
     * Does not support concurrency or expiry date.
     *
     * @param capacity The capacity the Cache should have
     */
    public LRUCacheWithPrioritiesAndExpiryTimestamps(int capacity) {
        this.capacity = capacity;
    }

    class NodesPair{
        // The two nodes point to the same element.
        // However, we need both nodes, since they represent nodes in different lists
        private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode;
        private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode;

        // TODO think if it would be better to create two marker classes, one of the priority cache
        // and one for the expiry cache, to avoid confusion regarding the order in the constructor
        public NodesPair(DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode) {
            this.priorityCacheNode = priorityCacheNode;
            this.expiryCacheNode = expiryCacheNode;
        }

        public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> getPriorityCacheNode() {
            return priorityCacheNode;
        }

        public DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> getExpiryCacheNode() {
            return expiryCacheNode;
        }
    }

    // We need this, in order to be able to find an element in our cache in constant O(1) time
    private HashMap<String, NodesPair> hashmapWithNodes = new HashMap<>();
    TreeCache<Integer> prioritiesCache = new TreeCache<>("Priority");
    TreeCache<Long> expiryTimestampsCache = new TreeCache<>("ExpiryTimestamp");

    // Time complexity: O(logn)
    public Integer get(String key) {
        NodesPair nodesPair;
        // O(1)
        if (hashmapWithNodes.containsKey(key)) {
            nodesPair = hashmapWithNodes.get(key);
        } else {
            return null;
        }

        // O(logn)
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
            if (size() == capacity) {
                // Drop element to make capacity
                removeExpiredItemOrItemWithLowestPriorityOrLastUsed();
            }
            insertToTheTop(key, value, priority, expiryTimestamp);
        }
    }

    private void updateElementAndMoveToTheTopInCorrespondingTreeCaches(String key, Integer value, int updatedPriority, long updatedTimestamp) {
        final NodesPair initialNodesPair = hashmapWithNodes.get(key);

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode = initialNodesPair.getPriorityCacheNode();
        final ElementWithPriorityAndExpiryTimestamp element = priorityCacheNode.getElement();
        int existingPriority = element.getPriority();
        final long existingExpiryTimestamp = element.getExpiryTimestamp();

        priorityCacheNode = prioritiesCache.moveToTop(existingPriority, updatedPriority, priorityCacheNode);

        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode = initialNodesPair.getExpiryCacheNode();
        expiryCacheNode = expiryTimestampsCache.moveToTop(existingExpiryTimestamp, updatedTimestamp, expiryCacheNode);

        element.update(value, updatedPriority, updatedTimestamp);

        NodesPair updatedNodesPair = new NodesPair(priorityCacheNode, expiryCacheNode);
        hashmapWithNodes.put(key, updatedNodesPair);
    }

    public int size() {
        return hashmapWithNodes.size();
    }

    private void removeExpiredItemOrItemWithLowestPriorityOrLastUsed() {

        // remove item if expired
        boolean wasItemRemoved = removeExpiredItemIfAny();
        if (wasItemRemoved){
            return;
        }

        final ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp = prioritiesCache.deleteLastElementFromCacheForKey(prioritiesCache.firstKey());
        hashmapWithNodes.remove(elementWithPriorityAndExpiryTimestamp);
    }

    private boolean removeExpiredItemIfAny() {
        if (expiryTimestampsCache.isEmpty()){
            return false;
        }

        final Long lowestExpiryTimestamp = expiryTimestampsCache.firstKey();
        if (lowestExpiryTimestamp < currentEpochTime.get() ){
            DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> lowestExpiryItems = expiryTimestampsCache.get(lowestExpiryTimestamp);
            ElementWithPriorityAndExpiryTimestamp elementWithPriorityAndExpiryTimestamp = lowestExpiryItems.removeLast();
            if (lowestExpiryItems.isEmpty()){
                expiryTimestampsCache.remove(lowestExpiryTimestamp);
            }

            // Get the node to be removed from both the priorities list and the hashmap
            DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> node = hashmapWithNodes.get(elementWithPriorityAndExpiryTimestamp.getKey()).getExpiryCacheNode();
            int priority = node.getElement().getPriority();
            prioritiesCache.deleteFromPartialCache(priority, node);
            hashmapWithNodes.remove(node);
            return true;
        }
        return false;
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
        sb.append(System.getProperty("line.separator"));
        sb.append("===============");
        sb.append(System.getProperty("line.separator"));
        sb.append(prioritiesCache.toString());
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        sb.append("Expiry Timestamps Cache");
        sb.append(System.getProperty("line.separator"));
        sb.append("===============");
        sb.append(System.getProperty("line.separator"));
        sb.append(expiryTimestampsCache.toString());

        return sb.toString();
    }

    public static void main(String[] args) {
        LRUCacheWithPrioritiesAndExpiryTimestamps lruCacheWithPrioritiesAndExpiryTimestamps = new LRUCacheWithPrioritiesAndExpiryTimestamps(5);
        lruCacheWithPrioritiesAndExpiryTimestamps.setCurrentEpochTime(Optional.of(0L));
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("A", 5, 4, 1000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("B", 4, 1, 2);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("C", 3, 7, 600);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("D", 2, 8, 2000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("E", 1, 5, 5000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.get("C");
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("F", 4, 5, 5000);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
        lruCacheWithPrioritiesAndExpiryTimestamps.set("E", 2, 5, 2900);
        System.out.println(lruCacheWithPrioritiesAndExpiryTimestamps);
    }

    public Optional<Long> getCurrentEpochTime() {
        return currentEpochTime;
    }

    public void setCurrentEpochTime(Optional<Long> currentEpochTime) {
        this.currentEpochTime = currentEpochTime;
    }
}
