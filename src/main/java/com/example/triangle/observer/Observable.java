package com.example.triangle.observer;

import com.example.triangle.entity.Triangle;

/**
 * Interface for objects that can be observed.
 */
public interface Observable {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Triangle triangle, EventType type);
}