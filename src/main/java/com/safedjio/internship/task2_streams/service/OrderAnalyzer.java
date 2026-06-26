package com.safedjio.internship.task2_streams.service;

import com.safedjio.internship.task2_streams.entity.Customer;
import com.safedjio.internship.task2_streams.entity.Order;
import com.safedjio.internship.task2_streams.entity.OrderItem;
import com.safedjio.internship.task2_streams.entity.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAnalyzer {
    public Set<String> getUniqueCities(List<Order> orders) {
        return orders.stream()
                .map(order -> order.getCustomer().getCity())
                .collect(Collectors.toSet());
    }

    public double getTotalIncomeFromDelivered(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    public String getMostPopularProduct(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No products found");
    }

    public double getAverageCheck(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(OrderItem::getTotalPrice)
                        .sum())
                .average()
                .orElse(0.0);
    }

    public List<Customer> getLoyalCustomers(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomer,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
