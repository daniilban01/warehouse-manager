package DataAccess;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Connection.ConnectionFactory;

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private String getPrimaryKeyColumnName() {
        if (type.getSimpleName().equalsIgnoreCase("Orders")) {
            return "order_id";
        }
        return "id";
    }

    private String getPrimaryKeyFieldName() {
        if (type.getSimpleName().equalsIgnoreCase("Orders")) {
            return "ordersId";
        }
        return "id";
    }

    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM \"").append(type.getSimpleName().toLowerCase()).append("\" WHERE ").append(field).append(" = ?");
        return sb.toString();
    }

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

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor<?> ctor = null;
        for (Constructor<?> c : type.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                ctor = c;
                break;
            }
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    String dbColumnName = fieldName;

                    if (fieldName.equalsIgnoreCase("ordersId")) {
                        dbColumnName = "order_id";
                    }

                    Object value = resultSet.getObject(dbColumnName);
                    if (value != null) {
                        field.set(instance, value);
                    }
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating objects via reflection: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder("INSERT INTO \"");
        query.append(type.getSimpleName().toLowerCase()).append("\" (");

        Field[] fields = type.getDeclaredFields();
        List<Field> fieldsToInsert = new ArrayList<>();

        for (Field field : fields) {
            String name = field.getName();
            if (!name.equalsIgnoreCase("id") && !name.equalsIgnoreCase("ordersId") && !name.equalsIgnoreCase("bill_id")) {
                fieldsToInsert.add(field);
            }
        }

        for (int i = 0; i < fieldsToInsert.size(); i++) {
            query.append(fieldsToInsert.get(i).getName());
            if (i < fieldsToInsert.size() - 1) query.append(", ");
        }

        query.append(") VALUES (");
        for (int i = 0; i < fieldsToInsert.size(); i++) {
            query.append("?");
            if (i < fieldsToInsert.size() - 1) query.append(", ");
        }
        query.append(")");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query.toString());

            for (int i = 0; i < fieldsToInsert.size(); i++) {
                fieldsToInsert.get(i).setAccessible(true);
                statement.setObject(i + 1, fieldsToInsert.get(i).get(t));
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

    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder("UPDATE \"");
        query.append(type.getSimpleName().toLowerCase()).append("\" SET ");

        Field[] fields = type.getDeclaredFields();
        List<Field> fieldsToUpdate = new ArrayList<>();
        Field idField = null;
        String pkFieldName = getPrimaryKeyFieldName();

        try {
            for (Field field : fields) {
                if (!field.getName().equalsIgnoreCase(pkFieldName)) {
                    fieldsToUpdate.add(field);
                } else {
                    idField = field;
                }
            }

            for (int i = 0; i < fieldsToUpdate.size(); i++) {
                query.append(fieldsToUpdate.get(i).getName()).append(" = ?");
                if (i < fieldsToUpdate.size() - 1) query.append(", ");
            }
            query.append(" WHERE ").append(getPrimaryKeyColumnName()).append(" = ?");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query.toString());

            for (int i = 0; i < fieldsToUpdate.size(); i++) {
                fieldsToUpdate.get(i).setAccessible(true);
                statement.setObject(i + 1, fieldsToUpdate.get(i).get(t));
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