package io.qbeat.lru;

public class DoubleLinkedListNode<T> {
    Element element;
    DoubleLinkedListNode<T> next = null;
    DoubleLinkedListNode<T> previous = null;

    DoubleLinkedListNode(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}