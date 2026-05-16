package Presentation;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private JButton btnClients;
    private JButton btnProducts;
    private JButton btnOrders;
    private JLabel lblTitle;

    public MainView() {
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        lblTitle = new JLabel("Warehouse Manager", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));

        btnClients = new JButton("Client Management");
        btnProducts = new JButton("Product Management");
        btnOrders = new JButton("Orders Management");

        btnClients.addActionListener(e -> {
            new ClientView().setVisible(true);
        });

        btnProducts.addActionListener(e -> {
            new ProductView().setVisible(true);
        });

        btnOrders.addActionListener(e -> {
            new OrdersManagementView().setVisible(true);
        });

        add(lblTitle);
        add(btnClients);

        add(btnProducts);
        add(btnOrders);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}