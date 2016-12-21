package no.westerdals.student.shiale14.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Communicates with the database, populates an array list with primary keys (ISBN)
 * from the database, formulates questions by sending query to the database and
 * populates array with name-title pair.
 *
 * Divides the user input and correct answer into separate strings and
 * populates array lists with these strings.
 *
 * Checks if the lists are equal.
 *
 * Created by Alexander Shipunov on 21.02.2016.
 */
class Quiz {

    private final DBHandler DB; //TODO
    private ArrayList<String> isbnList;
    private String[] nameAndTitle;

    Quiz(DBHandler db) {
        DB = db;
        try {
            isbnList = db.getISBNList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        nameAndTitle = null;
    }

    boolean hasQuestions() {
        return !isbnList.isEmpty();
    }

    String askQuestion() {
        generateRandomNameAndTitle();
        return String.format("Hvem har skrevet %s? ", nameAndTitle[1]);
    }

    boolean checkAnswer(String answer) {
        String correctName = nameAndTitle[0].toLowerCase();
        return makeListFromString(answer).equals(makeListFromString(correctName));
    }

    String getCorrectAnswer(){
        return nameAndTitle[0];
    }

    private void generateRandomNameAndTitle() {
        String randomIsbn = isbnList.remove(new Random().nextInt(isbnList.size()));
        try {
            nameAndTitle = DB.getNameAndTitle(randomIsbn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies a string object into an array list by splitting it into substrings
     * around matches of " ", "." or "," and deletes empty elements.
     * @param string the initial string that has to be splitted.
     * @return sorted list of words from a string object.
     */
    private ArrayList<String> makeListFromString(String string) {
        ArrayList<String> list;
        if (string.contains(",")) {
            String[] strings = string.split(",");
            for(int i = 0; i < strings.length; i++){
                strings[i] = strings[i].trim(); //Deletes all spaces at the begging/end og an element.
            }
            list = new ArrayList<>(Arrays.asList(strings));
            // Splits substrings if containing "."
            if(string.contains(".")) {
                ArrayList<String> tempList = new ArrayList<>(list);
                list.stream().filter(s -> s.contains(".")).forEach(s -> {
                    String[] temp = s.split(" ");
                    tempList.addAll(Arrays.asList(temp));
                    tempList.remove(s);
                });
                list = tempList;
            }
        } else {
            list = new ArrayList<>(Arrays.asList(string.split(" ")));
        }
        Collections.sort(list); // Sorts the list that will be compared in checkAnswer().
        list.removeAll(Collections.singletonList("")); // Removes all empty elements from the list.
        return list;
    }
}
