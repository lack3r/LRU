package io.qbeat.lru;

public class DoubleLinkedListNode<T> {


    private T element;
    private DoubleLinkedListNode<T> next = null;
    private DoubleLinkedListNode<T> previous = null;

    DoubleLinkedListNode(T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public DoubleLinkedListNode<T> getNext() {
        return next;
    }

    public void setNext(DoubleLinkedListNode<T> next) {
        this.next = next;
    }

    public DoubleLinkedListNode<T> getPrevious() {
        return previous;
    }

    public void setPrevious(DoubleLinkedListNode<T> previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}