package com.safedjio.internship.task2_streams.service;

import com.safedjio.internship.task2_streams.entity.Category;
import com.safedjio.internship.task2_streams.entity.Customer;
import com.safedjio.internship.task2_streams.entity.Order;
import com.safedjio.internship.task2_streams.entity.OrderItem;
import com.safedjio.internship.task2_streams.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
        Customer bob = new Customer("3", "Bob", "New York");

        OrderItem laptop = new OrderItem("Laptop", 1, 1000.0, Category.ELECTRONICS); // Итого: 1000
        OrderItem mouse = new OrderItem("Mouse", 2, 50.0, Category.ELECTRONICS);     // Итого: 100
        OrderItem book = new OrderItem("Java Book", 5, 20.0, Category.BOOKS);        // Итого: 100
        OrderItem phone = new OrderItem("Phone", 1, 800.0, Category.ELECTRONICS);    // Итого: 800

        Order order1 = new Order("O-1", LocalDateTime.now(), john, List.of(laptop, mouse), OrderStatus.DELIVERED);
        Order order2 = new Order("O-2", LocalDateTime.now(), alice, List.of(book), OrderStatus.CANCELLED);
        Order order3 = new Order("O-3", LocalDateTime.now(), john, List.of(mouse), OrderStatus.DELIVERED);
        Order order4 = new Order("O-4", LocalDateTime.now(), bob, List.of(phone, book), OrderStatus.DELIVERED);
        Order order5 = new Order("O-5", LocalDateTime.now(), john, List.of(mouse), OrderStatus.DELIVERED);
        Order order6 = new Order("O-6", LocalDateTime.now(), john, List.of(mouse), OrderStatus.DELIVERED);
        Order order7 = new Order("O-7", LocalDateTime.now(), john, List.of(mouse), OrderStatus.DELIVERED);
        Order order8 = new Order("O-8", LocalDateTime.now(), john, List.of(mouse), OrderStatus.DELIVERED);

        sampleOrders = List.of(order1, order2, order3, order4, order5, order6, order7, order8);
    }

    @Test
    void shouldReturnCorrectUniqueCities() {
        Set<String> cities = analyzer.getUniqueCities(sampleOrders);

        assertEquals(2, cities.size(), "Должно быть только 2 уникальных города");
        assertTrue(cities.contains("New York"));
        assertTrue(cities.contains("London"));
    }

    @Test
    void shouldCalculateTotalIncomeFromDeliveredOrders() {
        double income = analyzer.getTotalIncomeFromDelivered(sampleOrders);
        assertEquals(2500.0, income, 0.001); // 0.001 - это дельта (погрешность) для double
    }

    @Test
    void shouldReturnMostPopularProduct() {
        String popular = analyzer.getMostPopularProduct(sampleOrders);
        assertEquals("Mouse", popular);
    }

    @Test
    void shouldCalculateAverageCheckForDeliveredOrders() {
        double avg = analyzer.getAverageCheck(sampleOrders);
        assertEquals(357.1428, avg, 0.001);
    }

    @Test
    void shouldReturnCustomersWithMoreThan5Orders() {
        List<Customer> loyal = analyzer.getLoyalCustomers(sampleOrders);
        assertEquals(1, loyal.size(), "Только один покупатель сделал >5 заказов");
        assertEquals("John Doe", loyal.get(0).getName());
    }
}