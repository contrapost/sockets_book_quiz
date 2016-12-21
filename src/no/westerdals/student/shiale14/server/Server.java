package no.westerdals.student.shiale14.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;

/**
 * Creates server socket and connection to database that contains information about books.
 * Username and password for connection to DB can be passed as arguments while starting the server
 * through command line or as program arguments while using IDE.
 * <p>
 * Server is running as long it's not explicitly stopped by user.
 * <p>
 * Server creates a thread for executing a quiz activity per connected client. Quiz activity executes
 * by a SocketProcessor class that implements Runnable interface.
 * <p>
 * Created by Alexander Shipunov Shipunov on 11.02.2016.
 */
public class Server {

    public static void main(String[] args) throws SQLException {

        final String USER_NAME = args[0];
        final String PASSWORD = args[1];
        final int PORT_NUMBER = 8000;

        try (DBHandler db = new DBHandler(USER_NAME, PASSWORD);
             ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            System.out.println("\"Forfatter Quiz\" server started running at " + new Date().toString() + ".");

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientConnection = serverSocket.accept();
                System.err.println("Client with ip address " +
                        clientConnection.getInetAddress().toString() + " is accepted at " + new Date().toString() + ".");
                new Thread(new SocketProcessor(clientConnection, db)).start();
            }
        } catch (IOException e) {
            System.err.println("Connection between server and client was broken / wasn't established.");
            e.printStackTrace();
        }
    }
}
