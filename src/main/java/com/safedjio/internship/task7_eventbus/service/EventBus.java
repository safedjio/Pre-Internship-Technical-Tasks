package com.safedjio.internship.task7_eventbus.service;

import com.safedjio.internship.task7_eventbus.annotation.Subscribe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    private static class Subscriber {
        final Object listenerInstance;
        final Method method;

        Subscriber(Object listenerInstance, Method method) {
            this.listenerInstance = listenerInstance;
            this.method = method;
        }

        void invoke(Object event) {
            try {
                method.setAccessible(true);
                method.invoke(listenerInstance, event);
            } catch (Exception e) {
                System.err.println("Ошибка при доставке события: " + e.getMessage());
            }
        }
    }

    private final Map<Class<?>, List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public void register(Object listener) {
        Method[] methods = listener.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Метод с @Subscribe должен иметь ровно 1 параметр!");
                }

                Class<?> eventType = method.getParameterTypes()[0];
                subscribers.putIfAbsent(eventType, new CopyOnWriteArrayList<>());
                subscribers.get(eventType).add(new Subscriber(listener, method));
            }
        }
    }

    public void unregister(Object listener) {
        for (List<Subscriber> subscriberList : subscribers.values()) {
            subscriberList.removeIf(subscriber -> subscriber.listenerInstance == listener);
        }
    }

    public void post(Object event) {
        if (event == null) return;
        List<Class<?>> eventTypes = getAllSuperclassesAndInterfaces(event.getClass());

        for (Class<?> type : eventTypes) {
            List<Subscriber> eventSubscribers = subscribers.get(type);
            if (eventSubscribers != null) {
                for (Subscriber subscriber : eventSubscribers) {
                    subscriber.invoke(event);
                }
            }
        }
    }

    private List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> clazz) {
        List<Class<?>> result = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            result.add(current);
            for (Class<?> iface : current.getInterfaces()) {
                if (!result.contains(iface)) {
                    result.add(iface);
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }
}