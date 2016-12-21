package no.westerdals.student.shiale14.client;

import no.westerdals.student.shiale14.shared.QuizState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Creates connection to "Forfatter Quiz" server, shows to user the quiz flow in the console,
 * receive user's answers and sends them to the server for check.
 *
 * Client shows the quiz flow on the basis of the information types represented by enum QuizState values.
 *
 * Created by Alexander Shipunov on 11.02.2016.
 */
public class Client {

    public static void main(String[] args) {

        final String SERVER = "localhost";
        final int PORT_NUMBER = 8000;

        try (Scanner userInput = new Scanner(System.in);
             Socket serverConnection = new Socket(SERVER, PORT_NUMBER);
             DataOutputStream output = new DataOutputStream(serverConnection.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(serverConnection.getInputStream());
             DataInputStream input = new DataInputStream(serverConnection.getInputStream())) {

            output.flush();

            while (true) {
                QuizState state = (QuizState) objectInputStream.readObject();
                System.out.print(input.readUTF());
                if (state == QuizState.YES_NO_QUESTION) {
                    String answer = userInput.nextLine();
                    output.writeUTF(answer);
                    output.flush();
                    if (answer.equals("nei") || answer.equals("n")) {
                        System.out.print(input.readUTF());
                        break;
                    }
                }

                if (state == QuizState.EXIT_MESSAGE) {
                    break;
                }

                if (state == QuizState.QUIZ_QUESTION || state == QuizState.WRONG_YES_NO_ANSWER) {
                    String answer = userInput.nextLine();
                    output.writeUTF(answer);
                    if (answer.equals("nei") || answer.equals("n")) {
                        System.out.print(input.readUTF());
                        break;
                    }
                }

                // This condition isn't necessary in this implementation.
                // if (state == QuizState.ANSWER_CHECK) {}
            }
        } catch (IOException e) {
            System.err.println("Connection between server and client wasn't established/was broken.\n");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Client tried to connect to wrong server");
            e.printStackTrace();
        }
    }
}
