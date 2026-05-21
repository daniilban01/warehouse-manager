package Presentation;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Utility class that uses Java Reflection to dynamically generate a DefaultTableModel from any list of objects.
 */
public class TableGenerator {

    /**
     * Dynamically creates a table model using the object fields as columns and their values as rows.
     *
     * @param objects the list of model objects to convert into table data
     * @return a populated DefaultTableModel, or an empty model if the list is null or empty
     */
    public DefaultTableModel createTable(List<?> objects) {
        if (objects == null || objects.isEmpty()) {
            return new DefaultTableModel();
        }

        Class<?> type = objects.get(0).getClass();
        Field[] fields = type.getDeclaredFields();

        Vector<String> columnNames = Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.toCollection(Vector::new));

        Vector<Vector<Object>> data = new Vector<>();
        for (Object obj : objects) {
            Vector<Object> row = new Vector<>();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    row.add(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }
}