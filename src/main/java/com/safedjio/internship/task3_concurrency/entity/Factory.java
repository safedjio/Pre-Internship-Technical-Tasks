package com.safedjio.internship.task3_concurrency.entity;

import java.util.Random;

public class Factory implements Runnable {
    private final Inventory factoryInventory;
    private final Random random =  new Random();

    public Factory(Inventory factoryInventory) {
        this.factoryInventory = factoryInventory;
    }

    @Override
    public void run() {
        for(int day=1;day<=100;day++){
            int partsToProduce = random.nextInt(10) + 1;
            for(int i = 0;i < partsToProduce; i++){
                PartType[] allTypes = PartType.values();
                PartType randType = allTypes[random.nextInt(allTypes.length)];
                factoryInventory.addParts(randType, 1);
            }
            try {
                Thread.sleep(5);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
