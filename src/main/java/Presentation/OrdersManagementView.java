package Presentation;

import BusinessLogic.ClientBLL;
import BusinessLogic.ProductBLL;
import BusinessLogic.OrdersBLL;
import Model.Client;
import Model.Product;
import Model.Bill;
import DataAccess.BillDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrdersManagementView extends JFrame {
    private OrdersBLL ordersBLL;
    private ClientBLL clientBLL;
    private ProductBLL productBLL;
    private TableGenerator tableGenerator;

    public OrdersManagementView() {
        ordersBLL = new OrdersBLL();
        clientBLL = new ClientBLL();
        productBLL = new ProductBLL();
        tableGenerator = new TableGenerator();

        setTitle("Orders & Bills Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Create Orders", createOrdersPanel());
        tabbedPane.addTab("View Bills", createBillsPanel());

        add(tabbedPane);
        setLocationRelativeTo(null);
    }

    private JPanel createOrdersPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Client> clientCombo = new JComboBox<>();
        clientBLL.findAllClients().forEach(clientCombo::addItem);

        JComboBox<Product> productCombo = new JComboBox<>();
        productBLL.findAllProducts().forEach(productCombo::addItem);

        JTextField quantityField = new JTextField(10);
        JButton btnCreateOrders = new JButton("Place Order");

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1;
        formPanel.add(clientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        formPanel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnCreateOrders, gbc);

        JTable ordersTable = new JTable();
        Runnable refreshOrders = () -> {
            try {
                List<Model.Orders> ordersList = ordersBLL.findAllOrders();
                String[] columns = {"Order ID", "Client ID", "Client Name", "Product ID", "Product Name", "Quantity"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);

                for (Model.Orders o : ordersList) {
                    Client client = clientBLL.findClientById(o.getId_client());
                    Product product = productBLL.findProductById(o.getId_product());

                    String clientName = (client != null) ? client.getName() : "Unknown Client";
                    String productName = (product != null) ? product.getName() : "Unknown Product";

                    Object[] row = {
                            o.getOrdersId(),
                            o.getId_client(),
                            clientName,
                            o.getId_product(),
                            productName,
                            o.getQuantity()
                    };
                    model.addRow(row);
                }
                ordersTable.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading orders table: " + e.getMessage());
            }
        };
        refreshOrders.run();

        JScrollPane tableScroll = new JScrollPane(ordersTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Recent Orders"));

        btnCreateOrders.addActionListener(e -> {
            try {
                Client c = (Client) clientCombo.getSelectedItem();
                Product p = (Product) productCombo.getSelectedItem();
                int qty = Integer.parseInt(quantityField.getText());

                Model.Orders orders = new Model.Orders(c.getId(), p.getId(), qty);
                ordersBLL.createOrder(orders);

                JOptionPane.showMessageDialog(this, "Order placed and Bill generated!");
                refreshOrders.run();
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity format!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable billsTable = new JTable();

        BillDAO billDAO = new BillDAO();
        List<Bill> bills = billDAO.findAll();
        billsTable.setModel(tableGenerator.createTable(bills));

        JButton btnRefreshBills = new JButton("Refresh Bills List");
        btnRefreshBills.addActionListener(e -> {
            billsTable.setModel(tableGenerator.createTable(billDAO.findAll()));
        });

        panel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.add(btnRefreshBills);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }
}