package com.safedjio.internship.task3_concurrency.entity;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {
    private final Map<PartType, Integer> parts = new EnumMap<>(PartType.class);
    public Inventory() {
        for (PartType type : PartType.values()) {
            parts.put(type, 0);
        }
    }
    public synchronized void addParts(PartType type, int count) {
        parts.put(type, parts.get(type) + count);
    }
    public synchronized boolean takePart(PartType type) {
        int currentCount = parts.get(type);
        if(currentCount > 0) {
            parts.put(type, currentCount - 1);
            return true;
        }
        return false;
    }

    public synchronized int getCount(PartType type) {
        return parts.get(type);
    }
}
