package com.safedjio.internship.task3_concurrency.entity;

import java.util.Random;

public class Faction implements Runnable{
    private final String name;
    private final Inventory factoryInventory;
    private final Inventory privateInventory;
    private final Random random = new Random();

    public Faction(String name, Inventory factoryInventory) {
        this.name = name;
        this.factoryInventory = factoryInventory;
        this.privateInventory = new Inventory();
    }

    @Override
    public void run() {
        for(int night = 1; night <= 100; night++){
            int partsGatheredTonight = 0;
            int attempts = 0;
            while(partsGatheredTonight < 5 && attempts < 20){
                PartType randType = PartType.values()[random.nextInt(PartType.values().length)];
                if(factoryInventory.takePart(randType)){
                    privateInventory.addParts(randType, 1);
                    partsGatheredTonight++;
                }
                attempts++;
            }
            try{
                Thread.sleep(5);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
    public int getBuiltRobotsCount() {
        int heads = privateInventory.getCount(PartType.HEAD);
        int torsos = privateInventory.getCount(PartType.TORSO);
        int arms = privateInventory.getCount(PartType.ARM);
        int legs = privateInventory.getCount(PartType.LEG);

        int possibleByArms = arms / 2;
        int possibleByLegs = legs / 2;

        return Math.min(Math.min(heads, torsos), Math.min(possibleByArms, possibleByLegs));
    }

    public String getName() {
        return name;
    }
}
