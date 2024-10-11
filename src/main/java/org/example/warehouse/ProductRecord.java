package org.example.warehouse;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRecord(UUID uuid, String product, Category category, BigDecimal price) {

    /**
     * Compact constructor:
     * Validates input parameters.
     * Does not need to assign values to fields, the default-record constructor does that.
     * @param uuid UUID: Product unique code, sets to randomUUID if null (check for uniqueness in calling class).
     * @param product String: Name of product, throws IllegalArgumentException for empty or null product name.
     * @param category Category: Category of product, throws IllegalArgumentException for null Category.
     * @param price BigDecimal: Price of product, assigns value 0 if null.
     */
    public ProductRecord {

        // Throws IllegalArgumentException if product name is empty or null, else passed value.
        if ( product == null || product.isEmpty() )
            throw new IllegalArgumentException("Product name can't be null or empty.");

        // Throws IllegalArgumentException if Category is empty or null, else passed value.
        if ( category == null )
            throw new IllegalArgumentException("Category can't be null.");

        // Assigns a random id to uuid if null, else passed value.
        if ( uuid == null )
            uuid = UUID.randomUUID();

        // Assigns the value 0 to price if price is null, else passed value.
        if ( price == null )
            price = BigDecimal.ZERO;
    }
}
