package com.safedjio.internship.task1_linkedlist;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    @Test
    void new_list_shouldBeEmpty() {
        MyLinkedList<String> list = new MyLinkedList<>();
        assertEquals(0, list.size());
    }

    @Test
    void addFirst_and_addLast_shouldWorkCorrectly() {
        MyLinkedList<String> list = new MyLinkedList<>();

        list.addLast("B");
        list.addFirst("A");
        list.addLast("C");

        assertEquals(3, list.size());
        assertEquals("A", list.getFirst());
        assertEquals("C", list.getLast());
        assertEquals("B", list.get(1));
    }

    @Test
    void add_byIndex_shouldInsertInCorrectPositions() {
        MyLinkedList<Integer> list = new MyLinkedList<>();

        list.add(0, 10);
        assertEquals(10, list.get(0));

        list.add(0, 5);
        assertEquals(5, list.getFirst());

        list.add(2, 15);
        assertEquals(15, list.getLast());

        list.add(1, 7);
        assertEquals(7, list.get(1));
        assertEquals(4, list.size());
    }

    @Test
    void remove_methods_shouldDeleteAndReturnElements() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("X");
        list.addLast("Y");
        list.addLast("Z");

        String removedLast = list.removeLast();
        assertEquals("Z", removedLast);
        assertEquals(2, list.size());

        String removedFirst = list.removeFirst();
        assertEquals("X", removedFirst);
        assertEquals(1, list.size());

        String removedIndex = list.remove(0);
        assertEquals("Y", removedIndex);
        assertEquals(0, list.size());
    }

    @Test
    void removing_last_element_shouldMakeListEmpty() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addLast(99);
        list.removeFirst();
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
    }

    @Test
    void get_and_remove_withInvalidIndex_shouldThrowException() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("Hello");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(5));
    }
}