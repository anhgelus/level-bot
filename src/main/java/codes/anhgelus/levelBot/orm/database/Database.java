package codes.anhgelus.levelBot.orm.database;

import codes.anhgelus.levelBot.orm.entity.Column;
import codes.anhgelus.levelBot.orm.entity.OrmEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Database {
    public final DatabaseCredentials credentials;
    private final Connection connection;

    public Database(@NotNull DatabaseCredentials credentials) throws SQLException {
        this.credentials = credentials;
        connection = credentials.connect();
    }

    public void insert(@NotNull List<Field> fields, String table, OrmEntity clazz) throws SQLException, IllegalAccessException {
        final var columns = new StringBuilder();
        final var values = new StringBuilder();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            columns.append(column.name()).append(",");
            values.append(field.get(clazz).toString());
        }
        final var stmt = connection.createStatement();
        stmt.execute("INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")");
        stmt.close();
    }

    public void update(@NotNull Field field, String table, OrmEntity clazz) throws SQLException, IllegalAccessException {
        Column column = field.getAnnotation(Column.class);
        final var stmt = connection.createStatement();
        stmt.execute("UPDATE " + table + " SET " + column.name() + " = " + field.get(clazz).toString());
        stmt.close();
    }

    public void update(@NotNull List<Field> fields, String table, OrmEntity clazz) throws SQLException, IllegalAccessException {
        final var values = new StringBuilder();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            values.append(column.name()).append(" = ").append(field.get(clazz).toString()).append(",");
        }
        final var stmt = connection.createStatement();
        stmt.execute("UPDATE " + table + " SET " + values);
        stmt.close();
    }

    public void createTable(@NotNull List<Field> fields, String table) throws SQLException {
        final var columns = new StringBuilder();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            columns.append(column.name()).append(" ").append(column.type());
            if (!column.nullable()) {
                columns.append(" ").append("NOT NULL");
            }
            columns.append(", ");
        }
        final var stmt = connection.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ")");
        stmt.close();
    }
}
