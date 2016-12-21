package no.westerdals.student.shiale14.server;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test suite for Quiz class.
 *
 * Created by Alexander Shipunov on 21.02.2016.
 */
public class QuizTest {

    private Quiz quiz;

    @Before
    public void setUp() throws SQLException {
        DBHandler mockDB = mock(DBHandler.class);

        when(mockDB.getISBNList()).thenReturn(new ArrayList<>(Arrays.asList("isbnA", "isbnA", "isbnA")));
        when(mockDB.getNameAndTitle(anyString())).thenReturn(new String[]{"surnameA, nameA", "titleA"});

        quiz = new Quiz(mockDB);
        quiz.askQuestion();
    }

    @Test
    public void testAskQuestion()  {
        assertEquals("Hvem har skrevet titleA? ", quiz.askQuestion());
    }

    /**
     * User input changes to lower case by SocketProcessor.
     */
    @Test
    public void testCheckQuestion(){
        String correctAnswer = "nameA surnameA".toLowerCase();
        assertEquals(true, quiz.checkAnswer(correctAnswer));
    }

    @Test
    public void testGetCorrectAnswer() {
        String correctAnswer = "surnameA, nameA";
        assertEquals(correctAnswer, quiz.getCorrectAnswer());
    }

    /**
     * User input changes to lower case by SocketProcessor.
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testDifferentSpelling(){
        String answerOne = "NAMEA SURNAMEA".toLowerCase();
        String answerTwo = "NaMeA, Surnamea, ".toLowerCase();
        String answerThree = "NaMeA   , Surnamea       ".toLowerCase();
        String answerFour = "surnameA nameA".toLowerCase();
        String answerFive = "nameA surnameA   ".toLowerCase();
        assertEquals(true, quiz.checkAnswer(answerOne));
        assertEquals(true, quiz.checkAnswer(answerTwo));
        assertEquals(true, quiz.checkAnswer(answerThree));
        assertEquals(true, quiz.checkAnswer(answerFour));
        assertEquals(true, quiz.checkAnswer(answerFive));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testDifferentSpellingWithDots() throws SQLException{
        DBHandler mockDB = mock(DBHandler.class);

        when(mockDB.getISBNList()).thenReturn(new ArrayList<>(Arrays.asList("isbnA", "isbnA", "isbnA")));
        when(mockDB.getNameAndTitle(anyString())).thenReturn(new String[]{"surnameA, A. B.", "titleA"});

        quiz = new Quiz(mockDB);
        quiz.askQuestion();

        String answer = "A. B. SURNAMEA".toLowerCase();
        assertEquals(true, quiz.checkAnswer(answer));
    }

}
