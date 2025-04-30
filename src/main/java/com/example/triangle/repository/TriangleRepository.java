package com.example.triangle.repository;

import com.example.triangle.entity.Triangle;
import com.example.triangle.observer.EventType;
import com.example.triangle.observer.Observable;
import com.example.triangle.observer.Observer;
import com.example.triangle.service.TriangleCalculationService; // Нужен для сортировки
import com.example.triangle.service.TriangleTypeService;      // Нужен для сортировки
import com.example.triangle.specification.Specification; // Нужен для query
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton class acting as a repository for Triangle objects.
 * Implements Observable to notify observers (like Warehouse) about changes.
 * Provides methods for adding, querying (using Specifications), and sorting triangles.
 *
 * IMPORTANT: This is a non-thread-safe Singleton as per requirements.
 */
public class TriangleRepository implements Observable {

    private static final Logger logger = LogManager.getLogger(TriangleRepository.class);

    // --- Non-Thread-Safe Singleton Implementation ---
    private static TriangleRepository instance;

    private TriangleRepository() {
        // Приватный конструктор
        this.triangles = new ArrayList<>();
        this.observers = new ArrayList<>();
        // Сервисы нужны для сортировки, создаем их здесь или передаем через getInstance
        this.calculationService = new TriangleCalculationService();
        this.typeService = new TriangleTypeService();
        logger.info("TriangleRepository Singleton instance created.");
    }

    /**
     * Gets the single instance of TriangleRepository (non-thread-safe lazy initialization).
     * @return The singleton instance.
     */
    public static TriangleRepository getInstance() {
        // НЕ потокобезопасная ленивая инициализация
        if (instance == null) {
            instance = new TriangleRepository();
        }
        return instance;
    }
    // --- End Singleton Implementation ---

    private final List<Triangle> triangles;
    private final List<Observer> observers;
    // Зависимости для сортировки
    private final TriangleCalculationService calculationService;
    private final TriangleTypeService typeService;


    // --- Repository Methods ---

    /**
     * Adds a single triangle to the repository and notifies observers.
     * @param triangle The triangle to add. Returns false if triangle is null or already exists (by ID).
     */
    public boolean add(Triangle triangle) {
        if (triangle == null) {
            logger.warn("Attempted to add a null triangle.");
            return false;
        }
        // Проверка на дубликат по ID (опционально, но полезно)
        if (findById(triangle.getTriangleId()).isPresent()) {
            logger.warn("Triangle with ID {} already exists. Not added.", triangle.getTriangleId());
            return false;
        }

        boolean added = triangles.add(triangle);
        if (added) {
            logger.debug("Added Triangle ID {} to repository.", triangle.getTriangleId());
            notifyObservers(triangle, EventType.ADD);
        }
        return added;
    }

    /**
     * Adds multiple triangles to the repository.
     * @param triangleList The list of triangles to add.
     */
    public void addAll(List<Triangle> triangleList) {
        if (triangleList == null) return;
        int count = 0;
        for (Triangle t : triangleList) {
            if (add(t)) { // Используем add() для нотификации и проверки дубликатов
                count++;
            }
        }
        logger.info("Attempted to add {} triangles, successfully added {}.", triangleList.size(), count);
    }

    /**
     * Removes a triangle from the repository and notifies observers.
     * @param triangle The triangle to remove.
     * @return true if the triangle was found and removed, false otherwise.
     */
    public boolean remove(Triangle triangle) {
        if (triangle == null) return false;
        boolean removed = triangles.remove(triangle);
        if (removed) {
            logger.debug("Removed Triangle ID {} from repository.", triangle.getTriangleId());
            notifyObservers(triangle, EventType.REMOVE);
        }
        return removed;
    }

    /**
     * Removes a triangle by its ID.
     * @param id The ID of the triangle to remove.
     * @return true if a triangle with the ID was found and removed, false otherwise.
     */
    public boolean removeById(long id) {
        Optional<Triangle> triangleOpt = findById(id);
        if (triangleOpt.isPresent()) {
            return remove(triangleOpt.get()); // Делегируем удаление и нотификацию
        } else {
            logger.warn("Attempted to remove non-existent Triangle ID {}", id);
            return false;
        }
    }

    /**
     * Returns an unmodifiable view of all triangles in the repository.
     * @return An unmodifiable list of triangles.
     */
    public List<Triangle> getAll() {
        return Collections.unmodifiableList(triangles);
    }

    /**
     * Finds a triangle by its ID.
     * @param id The ID to search for.
     * @return An Optional containing the triangle if found, otherwise empty.
     */
    public Optional<Triangle> findById(long id) {
        // Простой линейный поиск, для больших репозиториев лучше Map<Long, Triangle>
        for (Triangle t : triangles) {
            if (t.getTriangleId() == id) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * Clears all triangles from the repository.
     * Note: This currently does NOT notify observers for each removed triangle.
     * Consider iterating and calling remove() if individual notifications are needed.
     */
    public void clear() {
        // Если нужна нотификация для каждого, нужно итерировать и вызывать remove()
        // Или добавить EventType.CLEAR_ALL и специальную обработку в Warehouse
        triangles.clear();
        logger.info("Repository cleared.");
        // Возможно, стоит оповестить Warehouse, чтобы он тоже очистился
        // notifyObservers(null, EventType.CLEAR_ALL); // Потребует доработки Observer/Warehouse
    }

    /**
     * Returns the number of triangles in the repository.
     */
    public int size() {
        return triangles.size();
    }

    // --- Query Method using Specification ---

    /**
     * Selects triangles from the repository that satisfy the given specification.
     * @param specification The specification criteria.
     * @return A list of triangles matching the specification.
     */
    public List<Triangle> query(Specification<Triangle> specification) {
        List<Triangle> result = new ArrayList<>();
        for (Triangle triangle : triangles) {
            if (specification.test(triangle)) { // Используем метод test() из спецификации
                result.add(triangle);
            }
        }
        logger.debug("Query with specification {} found {} results.", specification.getClass().getSimpleName(), result.size());
        return result;
        // Альтернатива с Stream API:
        // return triangles.stream()
        //        .filter(specification::test) // Используем Predicate-подобный интерфейс
        //        .collect(Collectors.toList());
    }

    // --- Sorting Methods ---

    /**
     * Returns a new list of triangles sorted by ID.
     */
    public List<Triangle> sortById() {
        return triangles.stream()
                .sorted(Comparator.comparingLong(Triangle::getTriangleId))
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list of triangles sorted by area (ascending).
     * Requires calculation service.
     */
    public List<Triangle> sortByArea() {
        return triangles.stream()
                .sorted(Comparator.comparingDouble(calculationService::calculateArea))
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list of triangles sorted by perimeter (ascending).
     * Requires calculation service.
     */
    public List<Triangle> sortByPerimeter() {
        return triangles.stream()
                .sorted(Comparator.comparingDouble(calculationService::calculatePerimeter))
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list of triangles sorted by the X-coordinate of Point A (ascending).
     */
    public List<Triangle> sortByPointACoordinateX() {
        return triangles.stream()
                .sorted(Comparator.comparingDouble(t -> t.getPointA().getX()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list of triangles sorted by Type (using enum's natural order).
     * Requires type service.
     */
    public List<Triangle> sortByType() {
        return triangles.stream()
                .sorted(Comparator.comparing(typeService::determineType))
                .collect(Collectors.toList());
    }


    // --- Observable Implementation ---

    @Override
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            logger.debug("Observer {} attached.", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        logger.debug("Observer {} detached.", observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(Triangle triangle, EventType type) {
        logger.debug("Notifying {} observers about event {} for Triangle ID {}", observers.size(), type, triangle.getTriangleId());
        // Создаем копию списка, чтобы избежать ConcurrentModificationException, если observer решит отписаться внутри update()
        List<Observer> observersCopy = new ArrayList<>(observers);
        for (Observer observer : observersCopy) {
            observer.update(triangle, type);
        }
    }
}