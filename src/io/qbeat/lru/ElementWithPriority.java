package io.qbeat.lru;

public class ElementWithPriority {
    private String key;
    private int value;

    private int priority;

    ElementWithPriority(String key, int value, int priority) {
        this.key = key;
        this.value = value;
        this.priority = priority;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void update(int value, int priority) {
        this.value = value;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "{" + key + ", " + value + ", " + priority + '}';
    }
}
