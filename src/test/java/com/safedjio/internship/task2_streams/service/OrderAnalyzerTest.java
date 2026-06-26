package com.safedjio.internship.task2_streams.service;

import com.safedjio.internship.task2_streams.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderAnalyzerTest {
    private OrderAnalyzer analyzer;
    private List<Order> sampleOrders;

    @BeforeEach
    void setUp() {
        analyzer = new OrderAnalyzer();

        Customer john = new Customer("1", "John Doe", "New York");
        Customer alice = new Customer("2", "Alice", "London");
        OrderItem laptop = new OrderItem("Laptop", 1, 1000.0, Category.ELECTRONICS);
        OrderItem mouse = new OrderItem("Mouse", 2, 50.0, Category.ELECTRONICS);
        OrderItem book = new OrderItem("Java Book", 5, 20.0, Category.BOOKS);

        Order order1 = new Order("O-1", null, john, List.of(laptop, mouse), OrderStatus.DELIVERED);
        Order order2 = new Order("O-2", null, alice, List.of(book), OrderStatus.CANCELLED);
        Order order3 = new Order("O-3", null, john, List.of(mouse), OrderStatus.DELIVERED);

        sampleOrders = List.of(order1, order2, order3);
    }

    @Test
    void shouldReturnCorrectUniqueCities() {
        Set<String> cities = analyzer.getUniqueCities(sampleOrders);

        assertEquals(2, cities.size());
        assertTrue(cities.contains("New York"));
        assertTrue(cities.contains("London"));
    }

    @Test
    void shouldCalculateTotalIncomeFromDeliveredOrders() {
        double income = analyzer.getTotalIncomeFromDelivered(sampleOrders);
        assertEquals(1200.0, income, 0.001);
    }
}