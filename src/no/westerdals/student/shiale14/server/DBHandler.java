package no.westerdals.student.shiale14.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Alexander on 11.02.2016.
 *
 */
public class DBHandler implements AutoCloseable {

    private ConnectToDB db;
    private PreparedStatement pstGetISBNList;
    private PreparedStatement pstGetNameAndTitle;

    @SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
    public DBHandler(String user, String password) throws SQLException {
        String serverName = "localhost";
        String databaseName = "pg4100innlevering2";
        db = new ConnectToDB(serverName, databaseName, user, password);
        Connection connection = db.getConnection();
        String tableName = "bokliste";
        pstGetISBNList = connection.prepareStatement("SELECT ISBN FROM " + tableName);
        pstGetNameAndTitle = connection.prepareStatement("SELECT forfatter, tittel FROM " + tableName
                + " WHERE ISBN = ?");
    }

    public void close() throws SQLException {
        db.close();
        pstGetISBNList.close();
        pstGetNameAndTitle.close();
    }


    public ArrayList<String> getISBNList() throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        ResultSet rs = pstGetISBNList.executeQuery();
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        rs.close();
        return list;
    }


    public String[] getNameAndTitle(String isbn) throws SQLException {
        String[] nameAndTitle = new String[2];
        pstGetNameAndTitle.setString(1, isbn);
        ResultSet rs = pstGetNameAndTitle.executeQuery();
        rs.next();
        nameAndTitle[0] = rs.getString(1);
        nameAndTitle[1] = rs.getString(2);
        rs.close();
        return nameAndTitle;
    }

}
