package com.safedjio.internship.task3_concurrency;

import com.safedjio.internship.task3_concurrency.entity.Faction;
import com.safedjio.internship.task3_concurrency.entity.Inventory;
import com.safedjio.internship.task3_concurrency.entity.PartType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {
    @Test
    void faction_shouldCalculateBuiltRobotsCorrectly() {
        Inventory dummyFactory = new Inventory();
        Faction faction = new Faction("Test", dummyFactory);

        dummyFactory.addParts(PartType.HEAD, 2);
        dummyFactory.addParts(PartType.TORSO, 2);
        dummyFactory.addParts(PartType.ARM, 5);
        dummyFactory.addParts(PartType.LEG, 3);
    }

    @Test
    void inventory_shouldNotGoBelowZero_whenMultipleThreadsTakeLastPart() throws InterruptedException {
        Inventory inventory = new Inventory();

        inventory.addParts(PartType.HEAD, 1);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Поток ждет выстрела
                    inventory.takePart(PartType.HEAD); // Пытается забрать деталь
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(0, inventory.getCount(PartType.HEAD), "Количество деталей не должно быть отрицательным!");
    }
}