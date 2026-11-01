package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

// Generic repository for storing different types of objects
public class Repository<T> {

    private HashMap<String, T> data = new HashMap<>();

    // Save an object using its @Id field
    public T save(T entity) {

        try {
            Class<?> current = entity.getClass();

            while (current != null) {
                for (Field field : current.getDeclaredFields()) {

                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);

                        String id = String.valueOf(field.get(entity));
                        data.put(id, entity);

                        return entity;
                    }
                }

                current = current.getSuperclass();
            }

            throw new IllegalArgumentException("No @Id field found.");

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Find an object using its ID
    public T findById(String id) {
        return data.get(id);
    }

    // Return all stored objects
    public ArrayList<T> findAll() {
        return new ArrayList<>(data.values());
    }

    // Delete an object
    public boolean delete(String id) {
        return data.remove(id) != null;
    }

    // Save multiple objects
    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    // Calculate average of numeric values
    public static <N extends Number> double average(N[] values) {

        if (values.length == 0) {
            return 0;
        }

        double sum = 0;

        for (N value : values) {
            sum += value.doubleValue();
        }

        return sum / values.length;
    }
}