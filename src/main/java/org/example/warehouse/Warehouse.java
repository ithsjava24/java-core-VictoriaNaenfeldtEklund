package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Warehouse class
 * Instances are created by methods getInstance() or getInstance(String name).
 * Holds product list and changedProduct list.
 * Methods to add product, change product price, get all products, get product with uuid,
 * get changed products, get all products mapped with their category, get products with a specific category,
 * check if warehouse contains any products.
 */
public class Warehouse {

    private static Map<String, Warehouse> warehouses = new HashMap<>();
    private List<ProductRecord> products = new ArrayList<>();
    private List<ProductRecord> changedProducts = new ArrayList<>();
    private String name;

    private Warehouse(){}

    private Warehouse(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns new instance of Warehouse.
     * Does not save in map.
     * @return Warehouse: new
     */
    public static Warehouse getInstance() {
        return new Warehouse();
    }

    /**
     * Returns instance if the name is mapped as a key,
     * else creates a new instance with key parameter name (first letter converted to uppercase) and maps it.
     * @param warehouseName String: Throws IllegalArgumentException if name is null.
     * @return instance that is mapped to name or new instance if key is absent.
     */
    public static Warehouse getInstance(String warehouseName) throws IllegalArgumentException {

        // Check if name is null or empty
        if (warehouseName == null) {
            throw new IllegalArgumentException("Warehouse name can't be null");
        }

        warehouseName = capitalize(warehouseName);

        // Check if the warehouses map already contains the named warehouse, in that case, return the Warehouse instance,
        // else create a new Warehouse instance and put it in the map with the name as the key.
        if (!warehouses.containsKey(warehouseName)) {
            warehouses.put(warehouseName, new Warehouse(warehouseName));
        }

        return warehouses.get(warehouseName);
    }

    /**
     * Adds new product to product list in warehouse.
     * @param uuid UUID: products unique code
     * @param productName String: products name
     * @param category Category: product category
     * @param price BigDecimal: product price
     * @return ProductRecord: new ProductRecord
     * @throws IllegalArgumentException if the product list in the warehouse already contains a product with the uuid.
     */
    public ProductRecord addProduct(UUID uuid, String productName, Category category, BigDecimal price) throws IllegalArgumentException {

        // if getProductByID(uuid) returns an empty Optional if the uuid does not exist in the product-list (will never be null).
        // if getProductByID(uuid) returns a Optional the uuid exists in the product-list.
        getProductById(uuid).ifPresent(_ -> {
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        });

        // Checks if uuid == null
        // (could only be null and not a value in product-list at first loop since previous test throws an exception if present)
        // second loop it checks if the UUID.randomUUID() has given us a uuid-value that already exists in product-list.
        while (uuid == null || getProductById(uuid).isPresent()){
            uuid = UUID.randomUUID();
        }

        // new ProductRecord
        //      throws new IllegalArgumentException if
        //          - product (String) is null or empty
        //          - category (Category) is null
        //      sets
        //          - price to 0 if null
        //          - uuid to uuid.randomUUID() if null
        ProductRecord productRecord = new ProductRecord(uuid, productName, category, price);
        products.add(productRecord);

        return productRecord;
    }

    /**
     * Returns a copy of the product list as an unmodifiable list.
     * @return List<ProductRecord>: unmodifiable
     */
    public List<ProductRecord> getProducts() {
        return List.copyOf(products);
    }

    /**
     * Returns product with unique uuid as an Optional<ProductRecord>.
     * findFirst() will not generate a NullPointerException as no product in the productList will be null (checks in addProduct)
     * @param uuid UUID: products unique code.
     * @return Optional<ProductRecord>: ProductRecord if uuid exists in warehouse productList, else empty Optional.
     */
    public Optional<ProductRecord> getProductById(UUID uuid) {
        return products.stream()
                .filter(productRecord -> productRecord.uuid().equals(uuid))
                .findFirst();
    }

    /**
     * Changes the price for a product.
     * Adds the old product to changedProducts list and removes it from product list.
     * Adds the new product to product list.
     * @param uuid UUID: products unique code.
     * @param newPrice BigDecimal: products new price
     * @throws IllegalArgumentException if product with passed uuid does not exist in product list.
     */
    public void updateProductPrice(UUID uuid, BigDecimal newPrice) throws IllegalArgumentException {

        getProductById(uuid).ifPresentOrElse(
                productRecord -> {
                    changedProducts.add(productRecord);
                    products.remove(productRecord);
                    addProduct(uuid, productRecord.product(), productRecord.category(), newPrice);
                },
                () -> {
                    throw new IllegalArgumentException("Product with that id doesn't exist.");
                }
        );
    }

    /**
     * Returns a copy of the changedProducts list as an unmodifiable list.
     * @return List<ProductRecord>: unmodifiable
     */
    public List<ProductRecord> getChangedProducts() {
        return List.copyOf(changedProducts);
    }

    /**
     * Returns product list as a map grouped by their Category.
     * @return Map<Category, List<ProductRecord>>:
     */
    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {
        return products.stream().collect(Collectors.groupingBy(ProductRecord::category));
    }

    /**
     * Returns all products that has the Category of passed parameter as list.
     * @param category Category: the category of products to get
     * @return List<ProductRecord> : list with products that groups by passed category, else empty list.
     */
    public List<ProductRecord> getProductsBy(Category category) {
        // getProductsGroupedByCategories() returns the product list as a map grouped by their Categories.
        return getProductsGroupedByCategories().getOrDefault(category, List.of());
    }

    private static String capitalize(String str) {

        if (str.isEmpty())
            return str;

        if (str.length() == 1)
            return str.toUpperCase();

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
