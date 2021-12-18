package io.qbeat.lru;

public class DoubleLinkedListNode<T> {


    private Element element;
    private DoubleLinkedListNode<T> next = null;
    private DoubleLinkedListNode<T> previous = null;

    DoubleLinkedListNode(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
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
        return "{" + element.getKey() + "," + element.getValue() + "}";
    }
}