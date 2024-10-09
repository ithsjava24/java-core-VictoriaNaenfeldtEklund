package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;

/**
 * - should have no public constructors
 * - can be created by calling getInstance(), instance will not save to class.
 * - can be created by calling getIntsance(String name), instance will be saved to map in class.
 *      - should be the same instance when using the same name
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
        if (warehouseName == null)
            throw new IllegalArgumentException("Warehouse name can't be null");

        warehouseName = capitalize(warehouseName);

        // Check if the warehouses map already contains the named warehouse, in that case, return the Warehouse instance,
        // else create a new Warehouse instance and put it in the map with the name as the key.
        if (!warehouses.containsKey(warehouseName)) {
            warehouses.put(warehouseName, new Warehouse(warehouseName));
        }

        return warehouses.get(warehouseName);
    }

    public ProductRecord addProduct(UUID uuid, String product, Category category, BigDecimal price) throws IllegalArgumentException {

        if (uuid == null){
            uuid = UUID.randomUUID();
        }

        var hasUUID = products.stream()
                .map(ProductRecord::uuid)
                .anyMatch(uuid::equals);

        if (hasUUID){
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        }

        ProductRecord productRecord = new ProductRecord(uuid, product, category, price);
        products.add(productRecord);

        return productRecord;
    }

    /**
     * Returns the values from products map as an unmodifiable list.
     * @return List<ProductRecord>
     */
    public List<ProductRecord> getProducts() {
        return List.copyOf(products);
    }

    /**
     * Returns product with unique uuid as an Optional<ProductRecord>.
     * @param uuid UUID: the products unique code.
     * @return Optional<ProductRecord>: ProductRecord if uuid exists in product map, else empty Optional.
     */
    public Optional<ProductRecord> getProductById(UUID uuid) {
        return products.stream()
                .filter(productRecord -> productRecord.uuid().equals(uuid))
                .findFirst();
    }

    /**
     * Changes a products price, updates the change in products map and adds the old product to changedProducts list.
     * @param uuid UUID: products unique code, throws IllegalArgumentException if uuid does not exist.
     * @param newPrice BigDecimal: the new price.
     */
    public void updateProductPrice(UUID uuid, BigDecimal newPrice) throws IllegalArgumentException {

        if(getProductById(uuid).isEmpty()){
            throw new IllegalArgumentException("Product with that id doesn't exist.");
        }

        for (ProductRecord productRecord : products) {
            if (productRecord.uuid().equals(uuid)) {
                changedProducts.add(productRecord);
                products.remove(productRecord);
                addProduct(uuid, productRecord.product(), productRecord.category(), newPrice);
                break;
            }
        }
    }

    public List<ProductRecord> getChangedProducts() {
        return List.copyOf(changedProducts);
    }

    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {

        Map<Category, List<ProductRecord>> map = new HashMap<>();

        for( ProductRecord productRecord : products ){

            Category category = productRecord.category();

            if(!map.containsKey(category)){
                map.put(category, new ArrayList<>());
            }
            map.get(category).add(productRecord);
        }

        return map;
    }

    /**
     * find all products belonging to a category"
     * "find multiple products from same category")
     * @param category
     * @return List<ProductRecord> : list with products that groups by passed category, else empty list.
     */
    public List<ProductRecord> getProductsBy(Category category) {
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
