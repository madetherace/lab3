package com.example.triangle.specification;

import com.example.triangle.entity.Triangle;
import com.example.triangle.service.TriangleCalculationService; // Нужен сервис

public class AreaRangeSpecification implements Specification<Triangle> {
    private final double minArea;
    private final double maxArea;
    private static TriangleCalculationService calculationService; // Ленивая инициализация

    public AreaRangeSpecification(double minArea, double maxArea) {
        if (minArea > maxArea) {
            throw new IllegalArgumentException("Min area cannot be greater than max area");
        }
        this.minArea = minArea;
        this.maxArea = maxArea;
        if (calculationService == null) {
            calculationService = new TriangleCalculationService();
        }
    }

    @Override
    public boolean test(Triangle triangle) {
        if (triangle == null) return false;
        double area = calculationService.calculateArea(triangle);
        return area >= minArea && area <= maxArea;
    }
}