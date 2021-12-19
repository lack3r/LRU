package io.qbeat.lru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {
    LRUCache cache;

    @BeforeEach
    public void init(){
        cache = new LRUCache(3);
    }

    @Test
    public void shouldAddElementIfCapacityIsNotReached(){
        cache.set("A", 10);
        assertEquals(10, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldUpdateValueIfElementAlreadyInCache(){
        cache.set("A", 10);
        cache.set("A", 2);
        assertEquals(2, cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    public void shouldReturnNullIfElementNotInCache(){
        final Integer value = cache.get("B");
        assertNull(value);
        assertEquals(0, cache.size());
    }

    @Test
    public void shouldNotRemoveAnyElementWhenCapacityIsFullButSetIsCalledWithExistingElement(){
        cache.set("A", 10);
        cache.set("B", 2);
        cache.set("C", 3);
        cache.set("B", 8);
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldDeleteLastUsedElementWhenCapacityReached(){
        cache.set("A", 10);
        cache.set("B", 2);
        cache.set("C", 3);
        cache.set("D", 8);
        assertNull(cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldGetMoveItemsOnTopOfCache(){
        cache.set("A", 10);
        cache.set("B", 2);
        cache.set("C", 3);
        cache.get("A");
        cache.set("D", 8);
        assertEquals(10, cache.get("A"));
        assertNull(cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(8, cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldNotExistCapacity(){
        cache.set("A", 10);
        cache.set("B", 2);
        cache.set("C", 3);
        cache.set("D", 8);
        assertEquals(3, cache.size());
    }

    @Test
    public void shouldRemoveOnlyElementIfCapacityIsOneAndNewOneIsInserted(){
        cache = new LRUCache(1);
        cache.set("A", 10);
        cache.set("B", 2);
        assertNull(cache.get("A"));
        assertEquals(2, cache.get("B"));
    }
}