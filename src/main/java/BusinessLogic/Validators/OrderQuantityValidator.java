package BusinessLogic.Validators;

import Model.Orders;
import Model.Product;

/**
 * Validator for checking order quantity against available stock.
 */
public class OrderQuantityValidator {

    /**
     * Validates that the order quantity is greater than 0 and does not exceed the stock.
     *
     * @param orders  the order containing the requested quantity
     * @param product the product containing the available stock
     * @throws IllegalArgumentException if the quantity is invalid or exceeds available stock
     */
    public void validate(Orders orders, Product product) {
        if (orders.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        }

        if (orders.getQuantity() > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock! Available: " + product.getQuantity());
        }
    }
}