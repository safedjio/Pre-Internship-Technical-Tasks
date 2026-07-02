package com.safedjio.internship.task3_concurrency;

import com.safedjio.internship.task3_concurrency.entity.Faction;
import com.safedjio.internship.task3_concurrency.entity.Factory;
import com.safedjio.internship.task3_concurrency.entity.Inventory;
import com.safedjio.internship.task3_concurrency.entity.PartType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {

    private static final Logger logger = Logger.getLogger(Simulation.class.getName());

    public static void main(String[] args) {
        logger.info("Симуляция Фабрики Роботов началась (100 дней/ночей)...");

        Inventory sharedInventory = new Inventory();

        Factory factory = new Factory(sharedInventory);
        Faction world = new Faction("World", sharedInventory);
        Faction wednesday = new Faction("Wednesday", sharedInventory);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(factory);
        executor.submit(world);
        executor.submit(wednesday);

        executor.shutdown();

        try{
            boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
            if(!finished){
                logger.severe("Симуляция зависла!");
                return;
            }
        }catch(InterruptedException e){
            logger.log(Level.SEVERE, "Главный поток был прерван", e);
            Thread.currentThread().interrupt();
        }logger.info("--- СТАТИСТИКА ОБЩЕГО СКЛАДА (Остатки) ---");
        for (PartType type : PartType.values()) {
            logger.info(type + ": " + sharedInventory.getCount(type));
        }

        int worldRobots = world.getBuiltRobotsCount();
        int wednesdayRobots = wednesday.getBuiltRobotsCount();

        logger.info("--- РЕЗУЛЬТАТЫ ФРАКЦИЙ ---");
        logger.info("Фракция WORLD собрала роботов: " + worldRobots);
        logger.info("Фракция WEDNESDAY собрала роботов: " + wednesdayRobots);

        if (worldRobots > wednesdayRobots) {
            logger.info("ПОБЕДИТЕЛЬ: WORLD!");
        } else if (wednesdayRobots > worldRobots) {
            logger.info("ПОБЕДИТЕЛЬ: WEDNESDAY!");
        } else {
            logger.info("Ничья! (Tie)");
        }
    }
}