package Presentation;

import BusinessLogic.ProductBLL;
import Model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductView extends JFrame {
    private JTable table;
    private ProductBLL productBLL;
    private TableGenerator tableGenerator;

    public ProductView() {
        productBLL = new ProductBLL();
        tableGenerator = new TableGenerator();

        setTitle("Product Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        table = new JTable();
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Product");
        JButton btnEdit = new JButton("Edit Product");
        JButton btnDelete = new JButton("Delete Product");

        btnAdd.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField quantityField = new JTextField();
            Object[] message = { "Product Name:", nameField, "Quantity:", quantityField };

            int option = JOptionPane.showConfirmDialog(this, message, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Product p = new Product(nameField.getText(), Integer.parseInt(quantityField.getText()));
                    productBLL.insertProduct(p);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a number!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit!");
                return;
            }

            int id = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
            String currentName = table.getValueAt(selectedRow, 1).toString();
            int currentQuantity = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());

            JTextField nameField = new JTextField(currentName);
            JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
            Object[] message = { "Product Name:", nameField, "Quantity:", quantityField };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Product ID: " + id, JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Product updatedProduct = new Product(id, nameField.getText(), Integer.parseInt(quantityField.getText()));
                    productBLL.updateProduct(updatedProduct);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a number!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                        productBLL.deleteProduct(id);
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    } catch (Exception ex) {
                        if (ex.getMessage().contains("violates foreign key constraint")) {
                            JOptionPane.showMessageDialog(this, "Error: Cannot delete product! It is part of an order.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first!");
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private void refreshTable() {
        try {
            List<Product> products = productBLL.findAllProducts();
            table.setModel(tableGenerator.createTable(products));
        } catch (Exception e) {
            System.err.println("Refresh failed: " + e.getMessage());
        }
    }
}