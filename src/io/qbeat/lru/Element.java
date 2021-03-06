package io.qbeat.lru;

public class Element {
    private final String key;
    private int value;

    Element(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void updateValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + key + "," + value + "}";
    }
}
