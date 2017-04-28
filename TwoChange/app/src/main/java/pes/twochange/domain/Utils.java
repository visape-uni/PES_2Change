package pes.twochange.domain;

import java.util.Date;
import java.util.Random;

public class Utils {

    public static String generateRandomWord(int wordLength) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(wordLength);
        for(int i = 0; i < wordLength; i++) {
            char tmp = (char) ('a' + r.nextInt('z' - 'a'));
            sb.append(tmp);
        }
        return sb.toString();
    }

    public static String randomID() {
        Long longId = new Date().getTime();
        String id = String.valueOf(longId) + generateRandomWord(4);
        return id;
    }

}
