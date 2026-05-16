package Presentation;

import BusinessLogic.ClientBLL;
import Model.Client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientView extends JFrame {
    private JTable table;
    private ClientBLL clientBLL;
    private TableGenerator tableGenerator;

    public ClientView() {
        clientBLL = new ClientBLL();
        tableGenerator = new TableGenerator();

        setTitle("Client Management");
        setSize(800, 500);
        setLayout(new BorderLayout());

        table = new JTable();
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Client");
        JButton btnEdit = new JButton("Edit Client");
        JButton btnDelete = new JButton("Delete Client");

        btnAdd.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField ageField = new JTextField();

            Object[] message = {
                    "Name:", nameField,
                    "Address:", addressField,
                    "Email:", emailField,
                    "Age:", ageField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Add New Client", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText();
                    String address = addressField.getText();
                    String email = emailField.getText();
                    int age = Integer.parseInt(ageField.getText());

                    Client newClient = new Client(name, address, email, age);
                    clientBLL.insertClient(newClient);

                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Client added successfully!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Age must be a number!");
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

            int id = (int) table.getValueAt(selectedRow, 0);
            String currentName = table.getValueAt(selectedRow, 1).toString();
            String currentAddress = table.getValueAt(selectedRow, 2).toString();
            String currentEmail = table.getValueAt(selectedRow, 3).toString();
            int currentAge = Integer.parseInt(table.getValueAt(selectedRow, 4).toString());

            JTextField nameField = new JTextField(currentName);
            JTextField addressField = new JTextField(currentAddress);
            JTextField emailField = new JTextField(currentEmail);
            JTextField ageField = new JTextField(String.valueOf(currentAge));

            Object[] message = {
                    "Name:", nameField,
                    "Address:", addressField,
                    "Email:", emailField,
                    "Age:", ageField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Client ID: " + id, JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    Client updatedClient = new Client(id, nameField.getText(), addressField.getText(), emailField.getText(), Integer.parseInt(ageField.getText()));
                    clientBLL.updateClient(updatedClient);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Item updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: Age must be a number!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

                    clientBLL.deleteClient(id);

                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Item deleted successfully!");

                } catch (Exception ex) {
                    String errorMsg = ex.getMessage();
                    if (errorMsg.contains("violates foreign key constraint")) {
                        JOptionPane.showMessageDialog(this, "Error: Cannot delete this item because it is used in an existing order!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error during deletion: " + errorMsg);
                    }
                }
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        List<Client> clients = clientBLL.findAllClients();
        DefaultTableModel model = tableGenerator.createTable(clients);
        table.setModel(model);
    }
}