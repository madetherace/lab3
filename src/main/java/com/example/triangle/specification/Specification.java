package com.example.triangle.specification;

import java.util.Objects; // Импорт Objects остается нужным для and/or

/**
 * Interface representing the Specification pattern.
 * Defines a test method to check if an object satisfies the criteria.
 * Can be used similar to java.util.function.Predicate.
 *
 * @param <T> The type of object the specification applies to.
 */
@FunctionalInterface
public interface Specification<T> {
    /**
     * Checks if the given object satisfies the specification criteria.
     * @param item The object to test.
     * @return true if the object satisfies the criteria, false otherwise.
     */
    boolean test(T item);

    // Можно добавить default методы для AND, OR, NOT, как в Predicate
    default Specification<T> and(Specification<? super T> other) {
        Objects.requireNonNull(other, "Other specification cannot be null");
        return (t) -> test(t) && other.test(t);
    }

    default Specification<T> or(Specification<? super T> other) {
        Objects.requireNonNull(other, "Other specification cannot be null");
        return (t) -> test(t) || other.test(t);
    }

    // Метод negate() остается, даже если пока не используется
    default Specification<T> negate() {
        return (t) -> !test(t);
    }
}