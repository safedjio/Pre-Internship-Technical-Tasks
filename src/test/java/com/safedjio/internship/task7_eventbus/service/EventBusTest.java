package com.safedjio.internship.task7_eventbus.service;

import com.safedjio.internship.task7_eventbus.annotation.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
    }

    static class BaseEvent {}
    static class ChildEvent extends BaseEvent {}
    static class AnotherEvent {}

    static class TestListener {
        int baseEventCount = 0;
        int childEventCount = 0;

        @Subscribe
        public void onBaseEvent(BaseEvent event) {
            baseEventCount++;
        }

        @Subscribe
        public void onChildEvent(ChildEvent event) {
            childEventCount++;
        }
    }

    static class AnotherListener {
        int eventCount = 0;

        @Subscribe
        public void onBaseEvent(BaseEvent event) {
            eventCount++;
        }
    }

    @Test
    void testSubscription_shouldInvokeMethodExactlyOnce() {
        TestListener listener = new TestListener();
        eventBus.register(listener);
        eventBus.post(new BaseEvent());

        assertEquals(1, listener.baseEventCount, "Метод должен быть вызван ровно 1 раз");
        assertEquals(0, listener.childEventCount, "Другие методы не должны вызываться");
    }

    @Test
    void testTypeHierarchy_subClassEventShouldTriggerSuperClassListeners() {
        TestListener listener = new TestListener();
        eventBus.register(listener);
        eventBus.post(new ChildEvent());

        assertEquals(1, listener.childEventCount, "Слушатель конкретного класса должен сработать");
        assertEquals(1, listener.baseEventCount, "Слушатель суперкласса должен сработать");
    }

    @Test
    void testUnregister_shouldNotReceiveEventsAfterUnregister() {
        TestListener listener = new TestListener();
        eventBus.register(listener);
        eventBus.unregister(listener);
        eventBus.post(new BaseEvent());

        assertEquals(0, listener.baseEventCount, "После отписки метод не должен вызываться");
    }

    @Test
    void testMultipleSubscribers_shouldDeliverToOneEventToAll() {
        TestListener listener1 = new TestListener();
        AnotherListener listener2 = new AnotherListener();
        eventBus.register(listener1);
        eventBus.register(listener2);
        eventBus.post(new BaseEvent());

        assertEquals(1, listener1.baseEventCount);
        assertEquals(1, listener2.eventCount);
    }

    @Test
    void testThreadSafety_shouldNotThrowConcurrentModificationException() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    TestListener listener = new TestListener();

                    eventBus.register(listener);
                    eventBus.post(new BaseEvent());
                    eventBus.unregister(listener);

                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    e.printStackTrace();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(0, exceptionCount.get(), "Были выброшены исключения во время многопоточной работы!");
    }
}