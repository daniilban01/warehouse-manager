package BusinessLogic.Validators;

import Model.Product;

/**
 * Validator for checking if a product's stock is valid.
 */
public class ProductStockValidator implements Validator<Product> {

    /** The minimum allowed stock level. */
    private static final int MIN_STOCK = 0;

    /**
     * Validates that the product's quantity is not negative.
     *
     * @param p the product to validate
     * @throws IllegalArgumentException if the stock quantity is less than 0
     */
    @Override
    public void validate(Product p) {
        if (p.getQuantity() < MIN_STOCK) {
            throw new IllegalArgumentException("The product stock cannot be negative!");
        }
    }
}