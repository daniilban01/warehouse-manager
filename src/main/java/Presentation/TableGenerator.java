package Presentation;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class TableGenerator {

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