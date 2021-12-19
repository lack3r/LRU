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
    // We need this, for the ordering of our list. The DoubleLinkedList is the perfect structure since we could:
    // Add an element on the top or tail in constant time.
    // Move an element to the top of the list, given that we have the node, again in constant time
//    private DoubleLinkedList<Element> orderedCache = new DoubleLinkedList<>();

//    private TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> prioritiesMap = new TreeMap<>();
    TreeCache<Integer> prioritiesCache = new TreeCache<>();
    TreeCache<Long> expiryTimestampsCache = new TreeCache<>();
    // This treemap, also has the same values as above, but NOT THE SAME DoubleLinkedListNodes!
    // each node has a different next and previous
//    private TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> expiryTimestampsWithElements = new TreeMap<>();

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
        moveToTheTopInBothMaps(nodesPair);
        return nodesPair.getPriorityCacheNode().getElement().getValue();
    }

    private void moveToTheTopInBothMaps(NodesPair nodesPair) {
        int priority = nodesPair.getPriorityCacheNode().getElement().getPriority();
        prioritiesCache.moveToTheTopInTheCorrespondingPartialCache(priority, nodesPair.getPriorityCacheNode());

        // These lines might not be needed. Let's think about them
        long expiryTimestamp = nodesPair.getPriorityCacheNode().getElement().getExpiryTimestamp();
        expiryTimestampsCache.moveToTheTopInTheCorrespondingPartialCache(expiryTimestamp, nodesPair.getPriorityCacheNode());
    }

    // Time complexity: O(1)
    public void set(String key, int value, int priority, int expiryTimestamp) {
        if (hashmapWithNodes.containsKey(key)) {
            moveToTheTop(key, value, priority, expiryTimestamp);
        } else {
            if (size() == capacity) {
                // Drop element to make capacity
                removeExpiredItemOrItemWithLowestPriorityOrLastUsed();
            }
            insertToTheTop(key, value, priority, expiryTimestamp);
        }
    }

    private void moveToTheTop(String key, Integer value, int updatedPriority, long updatedTimestamp) {
        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode = hashmapWithNodes.get(key).getPriorityCacheNode();
        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> expiryCacheNode = hashmapWithNodes.get(key).getExpiryCacheNode();
        final ElementWithPriorityAndExpiryTimestamp element = priorityCacheNode.getElement();
        int existingPriority = element.getPriority();
        final long existingExpiryTimestamp = element.getExpiryTimestamp();

        priorityCacheNode = prioritiesCache.moveToTheTopInCorrespondingPartialCacheOrAddInDifferentOneIfNeeded(existingPriority, updatedPriority, priorityCacheNode);
        expiryCacheNode = expiryTimestampsCache.moveToTheTopInCorrespondingPartialCacheOrAddInDifferentOneIfNeeded(existingExpiryTimestamp, updatedTimestamp, expiryCacheNode);

        element.update(value, updatedPriority, updatedTimestamp);

        NodesPair nodesPair = new NodesPair(priorityCacheNode, expiryCacheNode);
        hashmapWithNodes.put(key, nodesPair);
    }

//    private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> moveToTheTopInCorrespondingPartialCacheOrAddInDifferentOneIfNeeded(Object currentKey, Object updatedKey, TreeCache<?> cache, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode) {
//        ElementWithPriorityAndExpiryTimestamp element = partialCacheNode.getElement();
//        if (currentKey != updatedKey) {
//            partialCacheNode = cache.addToDifferentCache(currentKey, updatedKey, partialCacheNode, element);
//        } else {
//            moveToTheTopInTheCorrespondingPartialCache(updatedKey, partialCache, partialCacheNode);
//        }
//        return partialCacheNode;
//    }

//    private void moveToTheTopInTheCorrespondingPartialCache(int key, TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> partialCache, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode) {
//        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> cacheForUpdatedPriority = partialCache.get(key);
//        cacheForUpdatedPriority.moveToTheTop(priorityCacheNode);
//    }

//    private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> addToDifferentCache(int currentKey, int updatedKey, TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> partialCache, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode, ElementWithPriorityAndExpiryTimestamp element) {
//        // Remove element from list
//        deleteFromPartialCache(currentKey, partialCache, partialCacheNode);
//        partialCacheNode = addFirstAndCreateCacheIfNotExist(updatedKey, partialCache, element);
//        return partialCacheNode;
//    }

//    private void deleteFromPartialCache(int key, TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> partialCache, DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> priorityCacheNode) {
//        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> partialCacheForKey = partialCache.get(key);
//        partialCacheForKey.remove(priorityCacheNode);
//        if (partialCacheForKey.isEmpty()) {
//            partialCache.remove(key);
//        }
//    }

//    private DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> addFirstAndCreateCacheIfNotExist(int key, TreeMap<Integer, DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp>> partialCache, ElementWithPriorityAndExpiryTimestamp element) {
//        DoubleLinkedListNode<ElementWithPriorityAndExpiryTimestamp> partialCacheNode;
//        if (!partialCache.containsKey(key)) {
//            partialCache.put(key, new DoubleLinkedList<>());
//        }
//        DoubleLinkedList<ElementWithPriorityAndExpiryTimestamp> partialCacheForKey = partialCache.get(key);
//        partialCacheNode = partialCacheForKey.putFirst(element);
//        return partialCacheNode;
//    }

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
        return prioritiesCache.toStringWithNamedKey("Priority");
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
