package system.connector;

import java.sql.*;

public class Connector {
    private static Connection connection = null;
    private static DataConnector data = null;

    public static Connection get_connection() throws SQLException {
        if (connection == null && data != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException excepcion) {}
            connection = DriverManager.getConnection("jdbc:mysql://" + data.get_host()
                + "/biblioteca?serverTimezone=UTC", data.get_user(), data.get_pass());
        }

        return connection;
    }

    public static void set_data(DataConnector data) {
        Connector.data = data;
    }

    public static void close_connection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException excepcion) {}
    }

    private Connector() {}
}