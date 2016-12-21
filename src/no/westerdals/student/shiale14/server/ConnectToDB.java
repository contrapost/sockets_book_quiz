package no.westerdals.student.shiale14.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Alexander on 11.02.2016.
 *
 */
public class ConnectToDB implements AutoCloseable{

    Connection connection;

    public ConnectToDB(String server, String database, String user,
                       String password) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection("jdbc:mysql://" + server + "/"
                + database, user, password);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

}
