package utils;

import java.util.Random;

public class Randomgenerator {
    public StringBuilder RandomString(){
        StringBuilder randomString = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        for(int i=0;i<7;i++) {
            int randomIndex = rand.nextInt(26);
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString;
    }
    public StringBuilder RandomNumberString(){
        StringBuilder randomString = new StringBuilder();
        String characters = "0123456789";
        Random rand = new Random();
        for(int i=0;i<7;i++) {
            int randomIndex = rand.nextInt(10);
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString;
    }

}

