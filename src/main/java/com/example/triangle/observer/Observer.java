package com.example.triangle.observer;

import com.example.triangle.entity.Triangle;

/**
 * Interface for observers that react to changes in an Observable object.
 */
@FunctionalInterface // Можно сделать функциональным, т.к. один метод
public interface Observer {
    /**
     * Called by the observable when a change occurs.
     * @param triangle The triangle that was affected.
     * @param type The type of event (e.g., ADD, REMOVE).
     */
    void update(Triangle triangle, EventType type);
}