package Model;

/**
 * Model class representing a Product entity in the system.
 */
public class Product {
    private int id;
    private String name;
    private int quantity;

    /**
     * Default constructor required for reflection and object instantiation.
     */
    public Product(){}

    /**
     * Constructs a new Product without an ID (useful for insert operations).
     *
     * @param name     the name of the product
     * @param quantity the stock quantity of the product
     */
    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Constructs a new Product with all fields initialized.
     *
     * @param id       the unique identifier of the product
     * @param name     the name of the product
     * @param quantity the stock quantity of the product
     */
    public Product(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product [" + "id=" + id + ", name=" + name + ", quantity=" + quantity + ']';
    }
}