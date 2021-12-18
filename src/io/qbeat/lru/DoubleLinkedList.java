package io.qbeat.lru;

public class DoubleLinkedList<T> {
    private int size = 0;
    private DoubleLinkedListNode<Element> head = null;
    private DoubleLinkedListNode<Element> tail = null;

    DoubleLinkedListNode<Element> putFirst(Element value) {
        DoubleLinkedListNode<Element> node = new DoubleLinkedListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            head.setPrevious(node);
            node.setNext(head);
            head = node;
        }
        size++;
        return node;
    }

    void moveToTheTop(DoubleLinkedListNode<Element> node) {
        if (node == head){
            return;
        }

        if (node.getPrevious() != null){
            node.getPrevious().setNext(node.getNext());
        }

        if (node.getNext() != null){
            node.getNext().setPrevious(node.getPrevious());
        }

        node.setPrevious(null);
        head.setPrevious(node);
        node.setNext(head);
        head = node;
    }

    public void putLast(Element value) {
        DoubleLinkedListNode<Element> node = new DoubleLinkedListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.setNext(node);
            node.setPrevious(tail);
            tail = node;
        }
        size++;
    }

    private Element remove(DoubleLinkedListNode<Element> node) {
        if (size == 1) {
            head = null;
            tail = null;
            size--;
            return node.getElement();
        }

        if (node == tail) {
            tail = node.getPrevious();
        }

        if (node == head) {
            head = node.getNext();
        }

        if (node.getPrevious() != null) {
            node.getPrevious().setNext(node.getNext());
        }

        if (node.getNext() != null) {
            node.getNext().setPrevious(node.getPrevious());
        }
        size--;
        return node.getElement();
    }


    Element removeLast() {
        // O(1)
        return remove(tail);
    }

    @Override
    public String toString() {
        if (head == null) {
            return "List is empty";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Elements: ");
        DoubleLinkedListNode<Element> currentNode = head;
        while (currentNode != null) {
            sb.append(currentNode.toString());
            currentNode = currentNode.getNext();
        }
        return sb.toString();
    }
}

