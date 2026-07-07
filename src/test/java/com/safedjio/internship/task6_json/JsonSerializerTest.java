package com.safedjio.internship.task6_json;

import com.safedjio.internship.task6_json.annotation.Exclude;
import com.safedjio.internship.task6_json.annotation.JsonName;
import com.safedjio.internship.task6_json.exception.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    private JsonSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new JsonSerializer();
    }

    static class SimpleUser {
        String name = "John";
        int age = 30;
        String nullField = null;
        transient String temp = "secret";
        static String global = "global";
    }

    static class AnnotatedUser {
        @JsonName("first_name")
        String name = "Alice";

        @Exclude
        String password = "super_secret_password";
    }

    static class Company {
        String companyName = "TechCorp";
        List<String> employees = Arrays.asList("Bob", "Charlie");
        int[] departmentCodes = {101, 102};
    }

    static class Node {
        String name;
        Node next;

        Node(String name) {
            this.name = name;
        }
    }

    @Test
    void testSimpleObject_withNullTransientAndStatic() {
        SimpleUser user = new SimpleUser();
        String json = serializer.serialize(user);

        assertTrue(json.contains("\"name\":\"John\""), "Должно быть поле name");
        assertTrue(json.contains("\"age\":30"), "Должно быть поле age");
        assertFalse(json.contains("nullField"), "Null-поля должны игнорироваться");
        assertFalse(json.contains("temp"), "Transient-поля должны игнорироваться");
        assertFalse(json.contains("global"), "Static-поля должны игнорироваться");
    }

    @Test
    void testAnnotations_jsonNameAndExclude() {
        AnnotatedUser user = new AnnotatedUser();
        String json = serializer.serialize(user);

        assertTrue(json.contains("\"first_name\":\"Alice\""), "Поле name должно переименоваться в first_name");
        assertFalse(json.contains("\"name\":"), "Старого имени ключа \"name\": быть не должно");
        assertFalse(json.contains("password"), "Поле с @Exclude должно игнорироваться");
    }

    @Test
    void testCollectionsAndArrays() {
        Company company = new Company();
        String json = serializer.serialize(company);

        assertTrue(json.contains("\"employees\":[\"Bob\",\"Charlie\"]"));
        assertTrue(json.contains("\"departmentCodes\":[101,102]"));
    }

    @Test
    void testCircularReference_shouldThrowException() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        nodeA.next = nodeB;
        nodeB.next = nodeA;

        SerializationException exception = assertThrows(SerializationException.class, () -> {
            serializer.serialize(nodeA);
        });

        assertEquals("Circular reference detected!", exception.getMessage());
    }
}