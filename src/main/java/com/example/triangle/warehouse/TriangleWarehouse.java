package com.example.triangle.warehouse;

import com.example.triangle.entity.Triangle;
import com.example.triangle.observer.EventType;
import com.example.triangle.observer.Observer;
import com.example.triangle.service.TriangleCalculationService; // Нужен для расчетов
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton class acting as a cache for calculated triangle metrics (area, perimeter).
 * Implements Observer to automatically update metrics when triangles are added/removed
 * from the observed repository.
 *
 * IMPORTANT: This is a non-thread-safe Singleton as per requirements.
 */
public class TriangleWarehouse implements Observer {

    private static final Logger logger = LogManager.getLogger(TriangleWarehouse.class);

    // --- Non-Thread-Safe Singleton Implementation ---
    private static TriangleWarehouse instance;

    private TriangleWarehouse() {
        // Приватный конструктор для Singleton
        this.metricsMap = new HashMap<>();
        // Получаем сервис для расчетов (можно передать через getInstance, если нужно)
        this.calculationService = new TriangleCalculationService();
        logger.info("TriangleWarehouse Singleton instance created.");
    }

    /**
     * Gets the single instance of TriangleWarehouse (non-thread-safe lazy initialization).
     * @return The singleton instance.
     */
    public static TriangleWarehouse getInstance() {
        // НЕ потокобезопасная ленивая инициализация
        if (instance == null) {
            instance = new TriangleWarehouse();
        }
        return instance;
    }
    // --- End Singleton Implementation ---

    private final Map<Long, TriangleMetrics> metricsMap;
    private final TriangleCalculationService calculationService;

    /**
     * Updates or adds metrics for a given triangle.
     * @param triangle The triangle to calculate metrics for.
     */
    private void calculateAndStoreMetrics(Triangle triangle) {
        if (triangle == null) {
            logger.warn("Attempted to calculate metrics for a null triangle.");
            return;
        }
        long id = triangle.getTriangleId();
        double area = calculationService.calculateArea(triangle);
        double perimeter = calculationService.calculatePerimeter(triangle);
        TriangleMetrics metrics = new TriangleMetrics(area, perimeter);
        metricsMap.put(id, metrics);
        logger.debug("Stored/Updated metrics for Triangle ID {}: {}", id, metrics);
    }

    /**
     * Removes metrics for a given triangle ID.
     * @param triangleId The ID of the triangle whose metrics should be removed.
     */
    private void removeMetrics(long triangleId) {
        if (metricsMap.containsKey(triangleId)) {
            metricsMap.remove(triangleId);
            logger.debug("Removed metrics for Triangle ID {}", triangleId);
        } else {
            logger.warn("Attempted to remove metrics for non-existent Triangle ID {}", triangleId);
        }
    }

    /**
     * Retrieves the stored metrics for a given triangle ID.
     * @param triangleId The ID of the triangle.
     * @return An Optional containing the TriangleMetrics if found, otherwise empty.
     */
    public Optional<TriangleMetrics> getMetrics(long triangleId) {
        return Optional.ofNullable(metricsMap.get(triangleId));
    }

    /**
     * Checks if metrics exist for a given triangle ID.
     * @param triangleId The ID of the triangle.
     * @return true if metrics are stored, false otherwise.
     */
    public boolean containsMetrics(long triangleId) {
        return metricsMap.containsKey(triangleId);
    }

    /**
     * Clears all stored metrics.
     */
    public void clearAllMetrics() {
        metricsMap.clear();
        logger.info("All metrics cleared from Warehouse.");
    }

    /**
     * Returns the number of triangles for which metrics are stored.
     */
    public int size() {
        return metricsMap.size();
    }


    /**
     * Handles updates from the Observable (Repository).
     * Calculates and stores metrics when a triangle is added, removes them when removed.
     * @param triangle The affected triangle.
     * @param type The type of event.
     */
    @Override
    public void update(Triangle triangle, EventType type) {
        logger.info("Warehouse received update: Type={}, Triangle ID={}", type, triangle.getTriangleId());
        switch (type) {
            case ADD:
                calculateAndStoreMetrics(triangle);
                break;
            case REMOVE:
                removeMetrics(triangle.getTriangleId());
                break;
            // case UPDATE: // Если бы была поддержка изменения
            //     calculateAndStoreMetrics(triangle);
            //     break;
            default:
                logger.warn("Received unhandled event type: {}", type);
                break;
        }
    }
}