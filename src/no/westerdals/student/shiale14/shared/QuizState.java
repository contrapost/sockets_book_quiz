package no.westerdals.student.shiale14.shared;

import java.io.Serializable;

/**
 * Types of quiz state which determine what kind of
 * information transfers between server and client.
 *
 * Created by Alexander Shipunov on 17.02.2016.
 */
public enum QuizState implements Serializable {
    YES_NO_QUESTION,        // Checking if user wants to start/continue the quiz
    QUIZ_QUESTION,          // Question about the book
    ANSWER_CHECK,           // Checking the answer
    WRONG_YES_NO_ANSWER,    // Wrong answer on the YES_NO_QUESTION (different from "yes" or "no" option)
    EXIT_MESSAGE            // Greeting message when user stop the quiz or there are no more questions (books) in DB
}
