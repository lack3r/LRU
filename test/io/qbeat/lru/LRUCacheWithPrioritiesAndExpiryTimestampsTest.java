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
    public void shouldRemoveItemWithLowestPriorityWhenCapacityIsReachedAndAllHaveSamePriority(){
        cache.set("A", 10 ,2, 1000);
        cache.set("B", 2,3, 1000);
        cache.set("C", 2,1, 1000);
        cache.set("A", 10 ,0, 1000);
        cache.set("D", 10 ,2, 1000);
        assertEquals(null, cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(2, cache.get("C"));
        assertEquals(10, cache.get("D"));
        assertEquals(3, cache.size());
    }


    @Test
    public void shouldRemoveItemThatHasExpiredIfAnyWhenCapacityIsReached(){
        lruTimeForTests.setCurrentTimeInEpochMillis(500);
        cache.set("A", 10 ,2, 300);
        cache.set("B", 2,3, 400);
        cache.set("C", 2,1, 500);
        cache.set("A", 10 ,0, 600);
        cache.set("D", 10 ,2, 700);
        assertEquals(10, cache.get("A"));
        assertEquals(null, cache.get("B"));
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

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario1(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);

        assertEquals(-1, cache.get("A"));
        assertEquals(0, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(2, cache.get("D"));
        assertEquals(3, cache.get("E"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario2(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);

        assertEquals(-1, cache.get("A"));
        assertEquals(0, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(null, cache.get("D"));
        assertEquals(3, cache.get("E"));
        assertEquals(4, cache.get("F"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario3(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);

        assertEquals(-1, cache.get("A"));
        assertEquals(0, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(3, cache.get("E"));
        assertEquals(null, cache.get("F"));
        assertEquals(5, cache.get("G"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario4(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);
        cache.set("H", 6 ,5, 5009);

        assertEquals(-1, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(3, cache.get("E"));
        assertEquals(null, cache.get("F"));
        assertEquals(5, cache.get("G"));
        assertEquals(6, cache.get("H"));
        assertEquals(5, cache.size());
    }


    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario5(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);
        cache.set("H", 6 ,5, 5009);
        cache.set("I", 7 ,5, 5011);

        assertEquals(null, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(3, cache.get("E"));
        assertEquals(null, cache.get("F"));
        assertEquals(5, cache.get("G"));
        assertEquals(6, cache.get("H"));
        assertEquals(7, cache.get("I"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario6(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);
        cache.set("H", 6 ,5, 5009);
        cache.set("I", 7 ,5, 5011);
        cache.get("D");
        cache.set("A", 8 ,6, 6001);

        assertEquals(8, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(null, cache.get("E"));
        assertEquals(null, cache.get("F"));
        assertEquals(5, cache.get("G"));
        assertEquals(6, cache.get("H"));
        assertEquals(7, cache.get("I"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario7(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);
        cache.set("H", 6 ,5, 5009);
        cache.set("I", 7 ,5, 5011);
        cache.get("D");
        cache.set("A", 8 ,6, 6001);

        assertEquals(8, cache.get("A"));
        assertEquals(null, cache.get("B"));
        assertEquals(1, cache.get("C"));
        assertEquals(null, cache.get("E"));
        assertEquals(null, cache.get("F"));
        assertEquals(5, cache.get("G"));
        assertEquals(6, cache.get("H"));
        assertEquals(7, cache.get("I"));
        assertEquals(5, cache.size());
    }

    // Not a Unit Test per se. That is a scenario, that checks that everything was executed successfully
    @Test
    public void runScenario8(){
        cache = new LRUCacheWithPrioritiesAndExpiryTimestamps(5, lruTimeForTests);
        lruTimeForTests.setCurrentTimeInEpochMillis(0L);
        cache.set("A", -1 ,5, 905);
        cache.set("B", 0,1, 23049);
        cache.set("C", 1,5, 9038);
        cache.set("D", 2 ,9, 681);
        cache.set("E", 3 ,5, 3012);
        cache.get("C");
        lruTimeForTests.setCurrentTimeInEpochMillis(900L);
        cache.set("F", 4 ,0, 10004);
        cache.set("G", 5 ,5, 5004);
        cache.set("H", 6 ,5, 5009);
        cache.set("I", 7 ,5, 5011);
        cache.get("D");
        cache.set("A", 8 ,6, 6001);
        cache.set("D", 9 ,5, 6009);
        cache.get("F");

        assertEquals(5, cache.get("G"));
        assertEquals(6, cache.get("H"));
        assertEquals(7, cache.get("I"));
        assertEquals(8, cache.get("A"));
        assertEquals(9, cache.get("D"));
        assertEquals(5, cache.size());
    }
}