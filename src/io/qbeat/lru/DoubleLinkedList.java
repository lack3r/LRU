package io.qbeat.lru;

public class DoubleLinkedList<T> {
    int size = 0;
    private DoubleLinkedListNode<Element> head = null;
    private DoubleLinkedListNode<Element> tail = null;

    DoubleLinkedListNode<Element> putFirst(Element value) {
        DoubleLinkedListNode<Element> node = new DoubleLinkedListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            head.previous = node;
            node.next = head;
            head = node;
        }
        size++;
        return node;
    }

    void moveToBeginning(DoubleLinkedListNode<Element> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        node.next = head;
        node.previous = null;

        head = node;
    }

    public void putLast(Element value) {
        DoubleLinkedListNode<Element> node = new DoubleLinkedListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.previous = tail;
            tail = node;
        }
        size++;
    }

    private Element remove(DoubleLinkedListNode<Element> node) {
        if (size == 1) {
            head = null;
            tail = null;
            size--;
            return node.element;
        }

        if (node == tail) {
            tail = node.previous;
        }

        if (node == head) {
            head = node.next;
        }

        if (node.previous != null) {
            node.previous.next = node.next;
        }

        if (node.next != null) {
            node.next.previous = node.previous;
        }
        size--;
        return node.element;
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
            sb.append(currentNode.element.toString());
            currentNode = currentNode.next;
        }
        return sb.toString();
    }
}

