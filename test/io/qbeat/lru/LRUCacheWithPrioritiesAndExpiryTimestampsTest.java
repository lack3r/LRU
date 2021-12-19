package io.qbeat.lru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LRUCacheWithPrioritiesAndExpiryTimestampTest {

    LRUCacheWithPriorities cache;

    @BeforeEach
    public void init(){
        cache = new LRUCacheWithPriorities(3);
    }

    @Test
    public void shouldAddElementIfCapacityIsNotReached(){
        cache.set("A", 10 ,1);
        assertEquals(10, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldUpdateValueIfElementAlreadyInCache(){
        cache.set("A", 10 ,2);
        cache.set("A", 2,2);
        assertEquals(2, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldRemoveItemWithLowestPriorityWhenCapacityIsReached(){
        cache.set("A", 10 ,2);
        cache.set("B", 2,3);
        cache.set("C", 2,1);
        cache.set("A", 10 ,0);
        cache.set("D", 10 ,2);
        assertEquals(null, cache.get("A"));
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
        cache.set("A", 10 , 1);
        cache.set("B", 2, 1);
        cache.set("C", 3,1 );
        cache.set("B", 8,1 );
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldDeleteLastUsedElementWhenCapacityReached(){
        cache.set("A", 10,3);
        cache.set("B", 2,3 );
        cache.set("C", 3,3);
        cache.set("D", 8,3);
        assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldGetMoveItemsOnTopOfCache(){
        cache.set("A", 10,3);
        cache.set("B", 2,3);
        cache.set("C", 3,3);
        cache.get("A");
        cache.set("D", 8,3);
        assertEquals(10, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldNotExistCapacity(){
        cache.set("A", 10,3);
        cache.set("B", 2,3);
        cache.set("C", 3,3);
        cache.set("D", 8,3);
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldRemoveOnlyElementIfCapacityIsOneAndNewOneIsInserted(){
        cache = new LRUCacheWithPriorities(1);
        cache.set("A", 10,3);
        cache.set("B", 2,3);
        assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
    }
}