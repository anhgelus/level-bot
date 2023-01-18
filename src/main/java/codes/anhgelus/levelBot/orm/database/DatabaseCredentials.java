package codes.anhgelus.levelBot.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record DatabaseCredentials(String host, String port, String database, String user, String password) {
    public String getJdbcUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl());
    }
}
