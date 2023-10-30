package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    SortedSet<Character> prevGuesses;
    TreeSet<String> wordSet; //do I really want a set or what data structure?
    String keyWord;


    public EvilHangmanGame(){
        //implement
        wordSet = new TreeSet<>();
        prevGuesses = new TreeSet<>();
        keyWord = "";
    }
    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     * @throws IOException if the dictionary does not exist or an error occurs when reading it.
     * @throws EmptyDictionaryException if the dictionary does not contain any words.
     */
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        wordSet.clear();
        prevGuesses.clear();
        if(!dictionary.canRead() | !dictionary.exists()){
            throw new IOException();
        }
        if(dictionary.length() <= 0){
            throw new EmptyDictionaryException();
        }

        keyWord = "-";
        keyWord = keyWord.repeat(wordLength);

        Scanner importDict = new Scanner(dictionary);
        while(importDict.hasNext()){
            String nextWord = importDict.next();
            if(nextWord.length() == wordLength) {
                wordSet.add(nextWord);
            }
        }
        if(wordSet.size() <= 0){
            throw new EmptyDictionaryException();
        }
    }
    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed, case insensitive
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws GuessAlreadyMadeException if the character <code>guess</code>
     * has already been guessed in this game.
     */
    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);
        if (prevGuesses.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }
        prevGuesses.add(guess);
        //allocate map
        TreeMap<String, TreeSet<String>> wordGroups = new TreeMap<>();
        //fill up the map with the words
        for (String s : wordSet) {
            StringBuilder tempKey = new StringBuilder();
      //      for (int i = 0; i < wordSet.last().length(); i++) {
                for (int j = 0; j < s.length(); j++) {
                    if (s.charAt(j) == guess) {
                        tempKey.append(guess);
                    } else {
                        tempKey.append('-');
                    }
                }
       //     }
            String key = tempKey.toString();
            if (!wordGroups.containsKey(key)) {
                TreeSet<String> newSet = new TreeSet<>();
                newSet.add(s);
                wordGroups.put(key, newSet);
            } else {
                TreeSet<String> newSet = wordGroups.get(key);
                newSet.add(s);
                wordGroups.replace(key, newSet);
            }
        }
        TreeSet<String> maxKey = new TreeSet<>();
        for (String key : wordGroups.keySet()) {
            if (maxKey.size() == 0) {
                maxKey.add(key);
            } else if (wordGroups.get(maxKey.last()).size() == wordGroups.get(key).size()) {
                maxKey.add(key);
            } else if (wordGroups.get(maxKey.last()).size() < wordGroups.get(key).size()) {
                maxKey.clear();
                maxKey.add(key);
            }
        }

        if (maxKey.size() > 1) {
            //tie breaker
            for (String key : maxKey) {
                if (key.indexOf(guess) < 0) {
                    this.updateKey(key);
                    wordSet = wordGroups.get(key);
                    return wordSet;
                }
            }
            TreeSet<String> minLetters = new TreeSet<>();
            int minNum = maxKey.last().length();
            for (String key : maxKey) {
                int numLetters = 0;
                for (int i = 0; i < key.length(); i++) {
                    if (key.charAt(i) == guess) {
                        numLetters++;
                    }
                }
                if (minLetters.size() == 0) {
                    minLetters.add(key);
                    minNum = numLetters;
                } else if (minNum == numLetters) {
                    minLetters.add(key);
                } else if (numLetters < minNum) {
                    minLetters.clear();
                    minLetters.add(key);
                }
            }
            TreeSet<String> rightMost = new TreeSet<>();
            if (minLetters.size() > 1) {
                for (int i = minLetters.last().length() - 1; i >= 0; i--) {
                    rightMost.clear();
                    for (String key : minLetters) {
                        if (key.charAt(i) == guess) {
                            rightMost.add(key);
                        }
                    }
                    if(rightMost.size() == 1){
                        this.updateKey(rightMost.last());
                        wordSet = wordGroups.get(rightMost.last());
                        return wordSet;
                    }
                }
            } else {
                this.updateKey(minLetters.last());
                wordSet = wordGroups.get(minLetters.last());
                return wordSet;
            }
        } else {
            this.updateKey(maxKey.last());
            wordSet = wordGroups.get(maxKey.last());
            return wordSet;
        }
        return wordSet;
    }
    /**
     * Returns the set of previously guessed letters, in alphabetical order.
     *
     * @return the previously guessed letters.
     */
    @Override
    public SortedSet<Character> getGuessedLetters() {
        return prevGuesses;
    }

    public String getFinal(){
        Random rand = new Random();
        int upperbound = wordSet.size();
        int randInt = rand.nextInt(upperbound);
        for(String s: wordSet){
            if(randInt == 0){
                return s;
            }
            randInt--;
        }
        return "something broke";
    }
    public void updateKey(String recentKey) {
        StringBuilder tempWord = new StringBuilder();
        for (int i = 0; i < keyWord.length(); i++) {
            if (keyWord.charAt(i) != '-') {
                tempWord.append(keyWord.charAt(i));
            } else if (recentKey.charAt(i) != '-') {
                tempWord.append(recentKey.charAt(i));
            }
            else{
                tempWord.append('-');
            }
        }
        this.keyWord = tempWord.toString();
    }
    public String getPattern(){
        return keyWord;
    }
    public int getGuessCount(char guess){
        int counter = 0;
        for(int i = 0; i < keyWord.length(); i++){
            if(keyWord.charAt(i) == guess){
                counter++;
            }
        }
        return counter;
    }

}

