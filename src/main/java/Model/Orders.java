package Model;

/**
 * Model class representing an Order entity in the system.
 */
public class Orders {
    private int ordersId;
    private int clientId;
    private int productId;
    private int quantity;

    /**
     * Default constructor required for reflection and object instantiation.
     */
    public Orders(){}

    /**
     * Constructs a new Order with all necessary references and the requested quantity.
     *
     * @param id_client  the unique identifier of the client placing the order
     * @param id_product the unique identifier of the product being ordered
     * @param quantity   the number of units requested
     */
    public Orders(int id_client, int id_product, int quantity) {
        this.clientId = id_client;
        this.productId = id_product;
        this.quantity = quantity;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(int ordersId) {
        this.ordersId = ordersId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString(){
        return "Order [id=" + clientId +", id=" + productId +", quantity=" + quantity + "]";
    }
}