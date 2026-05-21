package DataAccess;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Connection.ConnectionFactory;

/**
 * Generic Data Access Object class that implements common database operations using Java Reflection and Streams.
 *
 * @param <T> the type of the model class
 */
public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    /**
     * Initializes the DAO and determines the runtime class type using reflection.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Returns the primary key column name in the database table.
     */
    private String getPrimaryKeyColumnName() {
        return type.getSimpleName().equalsIgnoreCase("Orders") ? "order_id" : "id";
    }

    /**
     * Returns the primary key field name in the Java class.
     */
    private String getPrimaryKeyFieldName() {
        return type.getSimpleName().equalsIgnoreCase("Orders") ? "ordersId" : "id";
    }

    /**
     * Maps a Java field name to its corresponding database column name.
     */
    private String mapFieldToColumn(String fieldName) {
        if (fieldName.equalsIgnoreCase("ordersId")) return "order_id";
        if (fieldName.equalsIgnoreCase("clientId")) return "id_client";
        if (fieldName.equalsIgnoreCase("productId")) return "id_product";
        return fieldName;
    }

    /**
     * Generates a SELECT query string filtered by a specific field.
     */
    private String createSelectQuery(String field) {
        return "SELECT * FROM \"" + type.getSimpleName().toLowerCase() + "\" WHERE " + field + " = ?";
    }

    /**
     * Retrieves all records from the corresponding database table.
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM \"" + type.getSimpleName().toLowerCase() + "\"";
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return new ArrayList<>();
    }

    /**
     * Finds a single record by its primary key ID.
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery(getPrimaryKeyColumnName());
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            List<T> objects = createObjects(resultSet);
            return objects.isEmpty() ? null : objects.get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Converts a database ResultSet into a list of Java objects using reflection and streams.
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();

        Constructor<?> ctor = Arrays.stream(type.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default constructor found for " + type.getName()));
        ctor.setAccessible(true);

        try {
            while (resultSet.next()) {
                T instance = (T) ctor.newInstance();

                Arrays.stream(type.getDeclaredFields()).forEach(field -> {
                    try {
                        field.setAccessible(true);
                        String dbColumnName = mapFieldToColumn(field.getName());
                        Object value = resultSet.getObject(dbColumnName);
                        if (value != null) {
                            field.set(instance, value);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error mapping column to field: " + field.getName(), e);
                    }
                });
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating objects via reflection: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts an object's fields into the database table using reflection and streams.
     */
    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;

        List<Field> fieldsToInsert = Arrays.stream(type.getDeclaredFields())
                .filter(f -> !f.getName().equalsIgnoreCase("id")
                        && !f.getName().equalsIgnoreCase("ordersId")
                        && !f.getName().equalsIgnoreCase("billId"))
                .collect(Collectors.toList());

        String columns = fieldsToInsert.stream()
                .map(f -> mapFieldToColumn(f.getName()))
                .collect(Collectors.joining(", "));

        String placeholders = fieldsToInsert.stream()
                .map(f -> "?")
                .collect(Collectors.joining(", "));

        String query = "INSERT INTO \"" + type.getSimpleName().toLowerCase() + "\" (" + columns + ") VALUES (" + placeholders + ")";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            for (int i = 0; i < fieldsToInsert.size(); i++) {
                Field field = fieldsToInsert.get(i);
                field.setAccessible(true);
                statement.setObject(i + 1, field.get(t));
            }
            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Updates an existing database record dynamically based on the object's field values.
     */
    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        String pkFieldName = getPrimaryKeyFieldName();

        List<Field> fieldsToUpdate = Arrays.stream(type.getDeclaredFields())
                .filter(f -> !f.getName().equalsIgnoreCase(pkFieldName))
                .collect(Collectors.toList());

        Field idField = Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(pkFieldName))
                .findFirst()
                .orElse(null);

        String setClause = fieldsToUpdate.stream()
                .map(f -> mapFieldToColumn(f.getName()) + " = ?")
                .collect(Collectors.joining(", "));

        String query = "UPDATE \"" + type.getSimpleName().toLowerCase() + "\" SET " + setClause + " WHERE " + getPrimaryKeyColumnName() + " = ?";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            for (int i = 0; i < fieldsToUpdate.size(); i++) {
                Field field = fieldsToUpdate.get(i);
                field.setAccessible(true);
                statement.setObject(i + 1, field.get(t));
            }

            if (idField != null) {
                idField.setAccessible(true);
                statement.setObject(fieldsToUpdate.size() + 1, idField.get(t));
            }

            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Deletes a database record matching the provided ID.
     */
    public void delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "DELETE FROM \"" + type.getSimpleName().toLowerCase() + "\" WHERE " + getPrimaryKeyColumnName() + " = ?";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No record found with ID " + id);
            }
            LOGGER.log(Level.INFO, type.getSimpleName() + " with ID " + id + " was deleted.");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
            throw e;
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}