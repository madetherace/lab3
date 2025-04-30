package com.example.triangle.specification;

import com.example.triangle.entity.Triangle;
import com.example.triangle.service.TriangleTypeService; // Нужен сервис для определения типа
import com.example.triangle.type.TriangleType;

import java.util.Objects;

public class TriangleTypeSpecification implements Specification<Triangle> {
    private final TriangleType expectedType;
    // Ленивая инициализация сервиса или передача через конструктор
    private static TriangleTypeService typeService;

    public TriangleTypeSpecification(TriangleType expectedType) {
        Objects.requireNonNull(expectedType, "Expected type cannot be null");
        this.expectedType = expectedType;
        if (typeService == null) { // Простая ленивая инициализация
            typeService = new TriangleTypeService();
        }
    }

    @Override
    public boolean test(Triangle triangle) {
        return triangle != null && typeService.determineType(triangle) == expectedType;
    }
}