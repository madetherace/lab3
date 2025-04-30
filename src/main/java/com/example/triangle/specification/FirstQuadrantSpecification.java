package com.example.triangle.specification;

import com.example.triangle.entity.Point;
import com.example.triangle.entity.Triangle;

public class FirstQuadrantSpecification implements Specification<Triangle> {
    @Override
    public boolean test(Triangle triangle) {
        if (triangle == null) return false;
        // Проверяем, что все точки в первом квадранте (x >= 0, y >= 0)
        return isPointInFirstQuadrant(triangle.getPointA()) &&
                isPointInFirstQuadrant(triangle.getPointB()) &&
                isPointInFirstQuadrant(triangle.getPointC());
    }

    private boolean isPointInFirstQuadrant(Point p) {
        return p != null && p.getX() >= 0 && p.getY() >= 0;
    }
}