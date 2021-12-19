package io.qbeat.lru;

public class LRUTimeForTests extends LRUTime {
    private long currentTimeInEpochMillis;

    @Override
    public long getCurrentTimetoEpochMillis() {
        return currentTimeInEpochMillis;
    }

    public void setCurrentTimeInEpochMillis(long currentTimeInEpochMillis) {
        this.currentTimeInEpochMillis = currentTimeInEpochMillis;
    }
}
