package com.example.triangle.specification;

import com.example.triangle.entity.Triangle;
import com.example.triangle.service.TriangleCalculationService; // Нужен сервис

public class PerimeterRangeSpecification implements Specification<Triangle> {
    private final double minPerimeter;
    private final double maxPerimeter;
    private static TriangleCalculationService calculationService; // Ленивая инициализация

    public PerimeterRangeSpecification(double minPerimeter, double maxPerimeter) {
        if (minPerimeter > maxPerimeter) {
            throw new IllegalArgumentException("Min perimeter cannot be greater than max perimeter");
        }
        this.minPerimeter = minPerimeter;
        this.maxPerimeter = maxPerimeter;
        if (calculationService == null) {
            calculationService = new TriangleCalculationService();
        }
    }

    @Override
    public boolean test(Triangle triangle) {
        if (triangle == null) return false;
        double perimeter = calculationService.calculatePerimeter(triangle);
        return perimeter >= minPerimeter && perimeter <= maxPerimeter;
    }
}