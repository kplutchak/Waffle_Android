package com.pipit.waffle.Speech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eric on 4/25/2015.
 */
public class SpeechToText {

    //Version 1:
    //Tokenize string.
    //Only use first sentence
    //Separate around the first 'OR'
    //Eliminate the question prefix (i.e. "Should I", "Should we" etc.)
    public static String[] stringToTwoChoices(String query){
        List<String> pairOfChoices = new ArrayList<>();
        String[] separateSentences = query.split(".");
        String[] seperateChoices = query.split("or");

        return seperateChoices;
    }


}
