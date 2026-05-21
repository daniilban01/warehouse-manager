package BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import BusinessLogic.Validators.ProductStockValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ProductDAO;
import Model.Product;

/**
 * Business Logic Layer class for managing Product operations, including stock validation and database access.
 */
public class ProductBLL {

    private List<Validator<Product>> validators;
    private ProductDAO productDAO;

    /**
     * Initializes the product business logic layer with its validators and data access object.
     */
    public ProductBLL() {
        validators = new ArrayList<Validator<Product>>();
        validators.add((Validator<Product>) new ProductStockValidator());
        productDAO = new ProductDAO();
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the identifier of the product
     * @return the found product object
     * @throws NoSuchElementException if no product with the specified id exists
     */
    public Product findProductById(int id) {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return p;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a list of all products
     */
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Validates and inserts a new product into the database.
     *
     * @param product the product object to be inserted
     */
    public void insertProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        productDAO.insert(product);
    }

    /**
     * Validates and updates an existing product's details.
     *
     * @param product the product object to be updated
     */
    public void updateProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        productDAO.update(product);
    }

    /**
     * Deletes a product by its unique identifier if it exists.
     *
     * @param id the identifier of the product to delete
     * @throws NoSuchElementException if the product with the specified id does not exist
     */
    public void deleteProduct(int id) throws Exception {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        productDAO.delete(id);
    }
}