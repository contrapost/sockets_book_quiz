package no.westerdals.student.shiale14.server;

import no.westerdals.student.shiale14.shared.QuizState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Implements Runnable interface to let Server proceed multiple client connection.
 *
 * Main goal of this class is to perform communication with client.
 * Communication performs by transferring objects that represents type
 * of quiz questions and questions themselves and receiving answers from client.
 *
 * Initialize object of Quiz class that supplies SocketProcessor with questions and answers.
 *
 * Created by Alexander Shipunov on 13.02.2016.
 */
class SocketProcessor implements Runnable {
    private final Socket CLIENT_CONNECTION;
    private final Quiz QUIZ;
    private DataInputStream input;
    private DataOutputStream output;
    private ObjectOutputStream objectOutputStream;
    private boolean firstQuestion;

    SocketProcessor(Socket socket, DBHandler db) {
        CLIENT_CONNECTION = socket;
        QUIZ = new Quiz(db);
        firstQuestion = true;

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("I/O exception caused by problems with getting input or output stream " +
                    "from/to client while creating a thread for a new client.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            objectOutputStream.writeObject(QuizState.YES_NO_QUESTION);
            objectOutputStream.flush();
            output.writeUTF("Vil du delta i forfatter-QUIZ? (ja/nei) ");
            output.flush();
            if (userWantsToContinue()) {
                holdQuiz();
            }
        } catch (IOException e) {
            System.err.println("I/O exception caused by problems with output stream " +
                    "to client while starting to hold the quiz activity.");
            e.printStackTrace();
        }
    }

    private void holdQuiz() {
        int scores = 0;

        try {
            while (QUIZ.hasQuestions() && !CLIENT_CONNECTION.isClosed()) {
                objectOutputStream.writeObject(QuizState.QUIZ_QUESTION);
                objectOutputStream.flush();
                output.writeUTF(QUIZ.askQuestion());
                output.flush();
                String answer = input.readUTF().toLowerCase();
                objectOutputStream.writeObject(QuizState.ANSWER_CHECK);
                objectOutputStream.flush();
                if (QUIZ.checkAnswer(answer)) {
                    scores += 100;
                    output.writeUTF("Riktig! Du har " + scores + " poeng.");
                    output.flush();
                } else {
                    output.writeUTF("Feil - det er " + QUIZ.getCorrectAnswer());
                    output.flush();
                }

                if (QUIZ.hasQuestions()) {
                    objectOutputStream.writeObject(QuizState.YES_NO_QUESTION);
                    objectOutputStream.flush();
                    output.writeUTF("\nVil du fortsette? (j/n) ");
                    output.flush();
                    firstQuestion = false;
                    userWantsToContinue();
                } else {
                    objectOutputStream.writeObject(QuizState.EXIT_MESSAGE);
                    objectOutputStream.flush();
                    output.writeUTF("\n***********************************************************************" +
                            "\nDet er ingen flere spørsmål. Quiz er over. Du har " + scores + " poeng." +
                            "\n***********************************************************************");
                    output.flush();
                    System.out.println("Client with ip address " + CLIENT_CONNECTION.getInetAddress().toString() +
                            " has finished the quiz at " + new Date().toString() + ".");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O exception caused by problems with getting input or output stream " +
                    "from/to client while holding the quiz.");
            e.printStackTrace();
        }
    }

    private boolean userWantsToContinue() {
        try {
            String answer;
            while (!CLIENT_CONNECTION.isClosed()) {
                answer = input.readUTF();
                if (answer.toLowerCase().equals("ja") || answer.toLowerCase().equals("j")) {
                    return true;
                } else if (answer.toLowerCase().equals("nei") || answer.toLowerCase().equals("n")) {
                    if (firstQuestion) {
                        output.writeUTF("Kanskje neste gang...");
                    } else {
                        output.writeUTF("Takk for at du deltok!");
                    }
                    output.flush();
                    output.close();
                    input.close();
                    objectOutputStream.close();
                    CLIENT_CONNECTION.close();
                } else {
                    objectOutputStream.writeObject(QuizState.WRONG_YES_NO_ANSWER);
                    objectOutputStream.flush();
                    output.writeUTF("Vennligst svar ja eller nei: ");
                    objectOutputStream.flush();
                }
            }
            if (CLIENT_CONNECTION.isClosed()) {
                System.out.println("Client with ip address " + CLIENT_CONNECTION.getInetAddress().toString() +
                        " has aborted connection at " + new Date().toString() + ".");
            }
        } catch (IOException e) {
            System.err.println("I/O exception caused by problems with getting input or output stream " +
                    "from/to client while checking if client wants to start/continue the quiz.");
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
