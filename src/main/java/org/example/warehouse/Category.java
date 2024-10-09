package org.example.warehouse;

import java.util.*;

/**
 *  - Has no public constructors
 *  - of(String name) returns instance of Category
 *  - of(String name) throws IllegalArgumentException if name is null
 *  - field String name should always have uppercase first letter
 *  - instance with same name equals same instance
 */
public class Category {

    private static Map<String, Category> categories = new HashMap<>();
    private String name;

    /**
     * Constructor, private.
     * One can not make instance through new Category(String name) outside of class.
     * @param name String: The name of the category.
     */
    private Category(String name) {
        this.name = name;
    }

    /**
     * Returns instance if the name is mapped as a key,
     * else creates a new instance with key parameter name (first letter converted to uppercase) and maps it.
     * @param name String: Throws IllegalArgumentException if name is null.
     * @return instance that is mapped to name or new instance if key is absent.
     */
    public static Category of(String name) throws IllegalArgumentException {

        // Check if name is null
        if (name == null)
            throw new IllegalArgumentException("Category name can't be null");

        name = capitalize(name);

        // Check if the categories map already contains the named Category, in that case, return the value,
        // else create a new Category instance and put it in the map with the name as the key.
        if (!categories.containsKey(name)) {
            categories.put(name, new Category(name));
        }

        return categories.get(name);
    }

    /**
     * Returns the category name.
     * @return name String
     */
    public String getName(){
        return name;
    }

    private static String capitalize(String str) {

        if (str.isEmpty())
            return str;

        if (str.length() == 1)
            return str.toUpperCase();

        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
