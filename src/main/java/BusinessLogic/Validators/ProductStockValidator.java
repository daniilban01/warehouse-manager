package BusinessLogic.Validators;

import Model.Product;

public class ProductStockValidator implements Validator<Product> {
    private static final int MIN_STOCK = 0;

    @Override
    public void validate(Product p) {
        if (p.getQuantity() < MIN_STOCK) {
            throw new IllegalArgumentException("The product stock cannot be negative!");
        }
    }
}