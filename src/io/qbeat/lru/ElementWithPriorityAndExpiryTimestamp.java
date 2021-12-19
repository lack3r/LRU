package io.qbeat.lru;

public class ElementWithPriorityAndExpiryTimestamp {
    private String key;
    private int value;
    private int priority;
    private long expiryTimestamp; // Time since Unix epoch till this expires

    ElementWithPriorityAndExpiryTimestamp(String key, int value, int priority, long timestamp) {
        this.key = key;
        this.value = value;
        this.priority = priority;
        this.expiryTimestamp = timestamp;
    }

    public void update(int value, int priority, long timestamp){
        this.value = value;
        this.priority = priority;
        this.expiryTimestamp = timestamp;
    }
    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

//    public void updateValue(int value) {
//        this.value = value;
//    }

    public int getPriority() {
        return priority;
    }

//    public void setPriority(int priority) {
//        this.priority = priority;
//    }

    public long getExpiryTimestamp() {
        return expiryTimestamp;
    }

//    public void setExpiryTimestamp(long expiryTimestamp) {
//        this.expiryTimestamp = expiryTimestamp;
//    }

    @Override
    public String toString() {
        return "{" + key + ", " + value + ", " + priority + "," + expiryTimestamp + '}';
    }
}
