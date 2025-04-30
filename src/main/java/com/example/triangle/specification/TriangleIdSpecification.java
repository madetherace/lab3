package com.example.triangle.specification;

import com.example.triangle.entity.Triangle;

public class TriangleIdSpecification implements Specification<Triangle> {
    private final long id;

    public TriangleIdSpecification(long id) {
        this.id = id;
    }

    @Override
    public boolean test(Triangle triangle) {
        return triangle != null && triangle.getTriangleId() == id;
    }
}