package DataAccess;

import Model.Bill;
import Connection.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object class for handling database operations related to Bills.
 */
public class BillDAO {
    protected static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    /**
     * Inserts a new bill record into the database.
     *
     * @param bill the bill object containing log data to insert
     */
    public void insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "INSERT INTO Bill (client_name, product_name, quantity, orderTime) VALUES (?, ?, ?, ?)";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            statement.setString(1, bill.clientName());
            statement.setString(2, bill.productName());
            statement.setInt(3, bill.quantity());
            statement.setTimestamp(4, Timestamp.valueOf(bill.orderTime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:insert ERROR - " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Finds a single bill record by its unique ID.
     *
     * @param id the identifier of the bill
     * @return the found Bill object, or null if no match is found
     */
    /**
     * Finds a single bill record by its unique ID.
     *
     * @param id the identifier of the bill
     * @return the found Bill object, or null if no match is found
     */
    public Bill findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM Bill WHERE bill_id = ?";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Bill(
                        resultSet.getInt("bill_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getTimestamp("orderTime").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Retrieves all bill records from the database.
     *
     * @return a list of all existing bills
     */
    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM Bill";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(new Bill(
                        resultSet.getInt("bill_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("product_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getTimestamp("orderTime").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return list;
    }
}