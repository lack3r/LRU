package io.qbeat.lru;

import java.time.Instant;

public class LRUTime {
    public long getCurrentTimeToEpochMillis(){
        return  Instant.now().toEpochMilli();
    }
}
