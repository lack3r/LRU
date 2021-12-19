package io.qbeat.lru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LRUCacheWithPrioritiesAndExpiryTimestampsTest {

    LRUTimeForTests lruTimeForTests = new LRUTimeForTests();
    LRUCacheWithPrioritiesAndExpiryTimestamps cache;

    @BeforeEach
    public void init(){
        lruTimeForTests = new LRUTimeForTests();
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(3, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
    }

    @Test
    public void shouldAddElementIfCapacityIsNotReached(){
        cache.set("A", 10 ,1, 1000);
        assertEquals(10, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldUpdateValueIfElementAlreadyInCache(){
        cache.set("A", 10 ,2, 1000);
        cache.set("A", 2,2, 1000);
        assertEquals(2, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldRemoveItemWithLowestPriorityWhenCapacityIsReached(){
        cache.set("A", 10 ,2, 1000);
        cache.set("B", 2,3, 1000);
        cache.set("C", 2,1, 1000);
        cache.set("A", 10 ,0, 1000);
        cache.set("D", 10 ,2, 1000);
        //assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(2, cache.get("C"));
        assertEquals(10, cache.get("D"));
        assertEquals(3, cache.size());
    }


    @Test
    public void shouldReturnNullIfElementNotInCache(){
        final Integer value = cache.get("B");
        assertEquals(null, value);
        assertEquals(0, cache.size());
    }

    @Test
    public void shouldNotRemoveAnyElementWhenCapacityIsFullButSetIsCalledWithExistingElement(){
        cache.set("A", 10 , 1, 1000);
        cache.set("B", 2, 1, 1000);
        cache.set("C", 3,1, 1000 );
        cache.set("B", 8,1 , 1000);
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldDeleteLastUsedElementWhenCapacityReached(){
        cache.set("A", 10,3, 1000);
        cache.set("B", 2,3 , 1000);
        cache.set("C", 3,3, 1000);
        cache.set("D", 8,3, 1000);
        assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldGetMoveItemsOnTopOfCache(){
        cache.set("A", 10,3, 1000);
        cache.set("B", 2,3, 1000);
        cache.set("C", 3,3, 1000);
        cache.get("A");
        cache.set("D", 8,3, 1000);
        assertEquals(10, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldNotExistCapacity(){
        cache.set("A", 10,3, 1000);
        cache.set("B", 2,3, 1000);
        cache.set("C", 3,3, 1000);
        cache.set("D", 8,3, 1000);
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldRemoveOnlyElementIfCapacityIsOneAndNewOneIsInserted(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(1, lruTimeForTests);
        cache.set("A", 10,3, 1000);
        cache.set("B", 2,3, 1000);
        assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
    }
}