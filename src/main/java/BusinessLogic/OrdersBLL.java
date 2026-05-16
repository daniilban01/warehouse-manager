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

public class OrdersBLL {

    private OrdersDAO ordersDAO;
    private ProductDAO productDAO;
    private ClientDAO clientDAO;
    private BillDAO billDAO;
    private OrderQuantityValidator validator;

    public OrdersBLL() {
        ordersDAO = new OrdersDAO();
        productDAO = new ProductDAO();
        clientDAO = new ClientDAO();
        billDAO = new BillDAO();
        validator = new OrderQuantityValidator();
    }

    public List<Orders> findAllOrders() {
        return ordersDAO.findAll();
    }

    public Orders findOrderById(int id) {
        Orders orders = ordersDAO.findById(id);
        if (orders == null) {
            throw new NoSuchElementException("The order with id =" + id + " was not found!");
        }
        return orders;
    }

    public void createOrder(Orders orders) {
        Product product = productDAO.findById(orders.getId_product());
        Client client = clientDAO.findById(orders.getId_client());

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

    public void deleteOrder(int id) throws Exception {
        Orders order = ordersDAO.findById(id);
        if (order == null) {
            throw new NoSuchElementException("Order not found for deletion!");
        }
        ordersDAO.delete(id);
    }
}