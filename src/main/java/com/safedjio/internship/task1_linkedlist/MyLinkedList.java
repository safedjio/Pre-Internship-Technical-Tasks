package com.safedjio.internship.task1_linkedlist;

import java.util.NoSuchElementException;

public class MyLinkedList<E> {
    private int size;
    private Node<E> head;
    private Node<E> tail;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, Node<E> next, E item) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    public int size() {
        return size;
    }

    public void addFirst(E item) {
        final Node<E> h = head;
        final Node<E> newNode = new Node<>(null, h, item);
        head = newNode;

        if (h == null) {
            tail = newNode;
        }else  {
            h.prev = newNode;
        }
        size++;
    }

    public void addLast(E item) {
        final Node<E> t = tail;
        final Node<E> newNode = new Node<>(t,null, item);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.next  = newNode;
        }
        size++;
    }

    public void add(int index, E item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) {
            addLast(item);
        }  else {
            Node<E> next = node(index);
            Node<E> prev = next.prev;
            Node<E> newNode = new Node<>(prev,next,item);
            next.prev = newNode;
            if (prev == null) {
                head = newNode;
            }  else {
                prev.next = newNode;
            }
            size++;
        }
    }

    public E getFirst() {
        if (head == null) throw new NoSuchElementException("List is empty");
        return head.item;
    }

    public E getLast() {
        if (tail == null) throw new NoSuchElementException("List is empty");
        return tail.item;
    }

    public E get(int index) {
        checkItemIndex(index);
        return node(index).item;
    }

    public E removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        final E item = head.item;
        final Node<E> next = head.next;

        head.item = null;
        head.next = null;
        head = next;
        if (next == null) {
            tail = null;
        }  else {
            next.prev = null;
        }
        size--;
        return item;
    }

    public E removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        final E item = tail.item;
        final Node<E> prev = tail.prev;

        tail.item = null;
        tail.prev = null;
        tail = prev;
        if (prev == null) {
            head = null;
        }  else {
            prev.next = null;
        }
        size--;
        return item;
    }

    public E remove(int index) {
        checkItemIndex(index);
        return unlink(node(index));
    }

    private void checkItemIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private Node<E> node(int index) {
        checkItemIndex(index);
        if (index < (size >> 1)) {
            Node<E> node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            return node;
        } else {
            Node<E> node = tail;
            for (int i = size-1; i > index; i--) {
                node = node.prev;
            }
            return node;
        }
    }

    private E unlink(Node<E> node) {
        final E element = node.item;
        final Node<E> prev = node.prev;
        final Node<E> next = node.next;

        if (prev == null) {
            head = next;
        }  else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        }   else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
        size--;
        return element;
    }
}
