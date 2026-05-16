package BusinessLogic.Validators;

import Model.Orders;
import Model.Product;

public class OrderQuantityValidator {
    public void validate(Orders orders, Product product) {
        if (orders.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        }

        if (orders.getQuantity() > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock! Available: " + product.getQuantity());
        }
    }
}
