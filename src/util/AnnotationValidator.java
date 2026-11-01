package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import model.annotation.*;

public class AnnotationValidator {

    public static String[] validate(Object obj) {

        List<String> errors = new ArrayList<>();

        // Check fields in the class and its parent classes
        Class<?> currentClass = obj.getClass();

        while (currentClass != null) {

            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {

                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    // Check positive value
                    if (field.isAnnotationPresent(Positive.class)) {

                        Positive positive =
                                field.getAnnotation(Positive.class);

                        if (value instanceof Number &&
                                ((Number) value).doubleValue() <= 0) {

                            errors.add(field.getName() + " "
                                    + positive.message());
                        }
                    }

                    // Check maximum string length
                    if (field.isAnnotationPresent(MaxLength.class)) {

                        MaxLength maxLength =
                                field.getAnnotation(MaxLength.class);

                        if (value instanceof String &&
                                ((String) value).length() > maxLength.value()) {

                            errors.add(field.getName()
                                    + " must be at most "
                                    + maxLength.value()
                                    + " characters");
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Cannot access field: " + field.getName());
                }
            }

            // Move to the parent class
            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}