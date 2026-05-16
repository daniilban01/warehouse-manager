package BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import BusinessLogic.Validators.ProductStockValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ProductDAO;
import Model.Product;

public class ProductBLL {

    private List<Validator<Product>> validators;
    private ProductDAO productDAO;

    public ProductBLL() {
        validators = new ArrayList<Validator<Product>>();
        validators.add((Validator<Product>) new ProductStockValidator());
        productDAO = new ProductDAO();
    }

    public Product findProductById(int id) {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return p;
    }

    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    public void insertProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        productDAO.insert(product);
    }

    public void updateProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        productDAO.update(product);
    }

    public void deleteProduct(int id) throws Exception {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        productDAO.delete(id);
    }
}