package io.qbeat.lru;

import java.time.Instant;

public class LRUTime {
    public long getCurrentTimetoEpochMillis(){
        return  Instant.now().toEpochMilli();
    }
}
