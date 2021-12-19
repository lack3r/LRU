# Least Recently Used (LRU) Cache

This repository has three implementations of LRUs:

| No   | Name                                          | `get` Time Complexity | `set` Time Complexity | Description                                                  |
| ---- | --------------------------------------------- | --------------------- | --------------------- | ------------------------------------------------------------ |
| 1    | **LRUCache**                                  | `O(1)`                | `O(1)`                | When capacity is reached, the *Least Recently Used* item is dropped. |
| 2    | **LRUCacheWithPriorities**                    | `O(1)`                | `O(log n)`            | The set method gets the priority for each item as well. When capacity is reached, it identifies the items that have the lowest priority, and then drops the *Least Recently Used* item, out of those items. |
| 3    | **LRUCacheWithPrioritiesAndExpiryTimestamps** | `O(1)`                | `O(log n)`            | The set method gets both the priority and an expiry timestamp (in [Epoch time](https://en.wikipedia.org/wiki/Unix_time)) for each item as well. When capacity is reached, if there is an element that has been expired, it drops that. If not, it identifies the items that have the lowest priority, and then drops the *Least Recently Used* item, out of those items. |

*Table A*

**Unit Testing/Coverage**

As of the time of writing, dozens of Unit tests have been implemented that currently cover ***100% of all three LRU's implementation*** (excluding the `toString` method).



**Contract & Explanation**

The Caches all have a certain capacity.

Each of the three Caches has a `get(key)` method and a `set(key,value,...)` method. 

The `get` signals that the element was requested/used and has to go on top of the corresponding cache.

When the `set` method is called:

* If an element is in the cache (can be identified by `key`), its value, priority (if exists) and expiryTimestamp (if exists) should be updated.

* If the element is not in the cache but the capacity is not reached, the element is added

* If the element is not in the cache and the capacity is reached, an existing element is dropped based on the policies mentioned in the *Table A*, and the new item is added. 

* In short, if an item does not exist in the cache, it will always be inserted (regardless if it has lower priority etc. than the elements already in cache)

  

**Implementation Details & Data Structures**

The following data structures have been used:

- `HashMap`, to identify if an element exists in the cache in constant time `O(1)`
- `DoubleLinkedList` (custom developed), to allow shifting items on the top of the stack in constant time `O(1)`
- `TreeCache`, based on Java's TreeMap implementation, that holds information regarding the priorities and expiries, that can be both retrieved and added in logarithmic time `O(log n)`

The structures are implemented using Java Generics, so they can be re-used if needed in other cases as well.

