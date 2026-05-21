package BusinessLogic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import BusinessLogic.Validators.OrderQuantityValidator;
import DataAccess.ClientDAO;
import DataAccess.OrdersDAO;
import DataAccess.ProductDAO;
import DataAccess.BillDAO;
import Model.Bill;
import Model.Client;
import Model.Orders;
import Model.Product;

/**
 * Business Logic Layer class for managing Orders, stock synchronization, and Bill generation.
 */
public class OrdersBLL {

    private OrdersDAO ordersDAO;
    private ProductDAO productDAO;
    private ClientDAO clientDAO;
    private BillDAO billDAO;
    private OrderQuantityValidator validator;

    /**
     * Initializes the orders business logic layer with required DAOs and validators.
     */
    public OrdersBLL() {
        ordersDAO = new OrdersDAO();
        productDAO = new ProductDAO();
        clientDAO = new ClientDAO();
        billDAO = new BillDAO();
        validator = new OrderQuantityValidator();
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     */
    public List<Orders> findAllOrders() {
        return ordersDAO.findAll();
    }

    /**
     * Finds an order by its unique identifier.
     *
     * @param id the identifier of the order
     * @return the found order object
     * @throws NoSuchElementException if no order with the specified id exists
     */
    public Orders findOrderById(int id) {
        Orders orders = ordersDAO.findById(id);
        if (orders == null) {
            throw new NoSuchElementException("The order with id =" + id + " was not found!");
        }
        return orders;
    }

    /**
     * Validates and creates a new order, updates product stock, and generates a bill log.
     *
     * @param orders the order object to be created
     * @throws NoSuchElementException if the linked product or client does not exist
     */
    public void createOrder(Orders orders) {
        Product product = productDAO.findById(orders.getProductId());
        Client client = clientDAO.findById(orders.getClientId());

        if (product == null) {
            throw new NoSuchElementException("Order failed: Product not found!");
        }
        if (client == null) {
            throw new NoSuchElementException("Order failed: Client not found!");
        }

        validator.validate(orders, product);

        ordersDAO.insert(orders);

        int newQuantity = product.getQuantity() - orders.getQuantity();
        product.setQuantity(newQuantity);
        productDAO.update(product);

        Bill bill = new Bill(
                0,
                client.getName(),
                product.getName(),
                orders.getQuantity(),
                LocalDateTime.now()
        );

        billDAO.insert(bill);
    }

    /**
     * Deletes an order by its unique identifier if it exists.
     *
     * @param id the identifier of the order to delete
     * @throws NoSuchElementException if the order with the specified id does not exist
     */
    public void deleteOrder(int id) throws Exception {
        Orders order = ordersDAO.findById(id);
        if (order == null) {
            throw new NoSuchElementException("Order not found for deletion!");
        }
        ordersDAO.delete(id);
    }
}