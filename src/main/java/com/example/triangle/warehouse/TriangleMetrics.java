package com.example.triangle.warehouse;

/**
 * Holds calculated metrics (area, perimeter) for a triangle.
 */
public class TriangleMetrics {
    private final double area;
    private final double perimeter;

    public TriangleMetrics(double area, double perimeter) {
        this.area = area;
        this.perimeter = perimeter;
    }

    public double getArea() {
        return area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    @Override
    public String toString() {
        // Используем String.format для аккуратного вывода
        return String.format("Metrics[Area=%.2f, Perimeter=%.2f]", area, perimeter);
    }
}