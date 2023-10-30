package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedSet;

public class EvilHangman {

    public static void main(String[] args) {
        String dictionaryFileName = args[0];
        File file = new File("C:/Users/trevo/Desktop/College/CS/CS240/EvilHangman/",dictionaryFileName);
        int wordLength = Integer.parseInt(args[1]);
        int maxGuess = Integer.parseInt(args[2]);
        int numCorrect = 0;

        EvilHangmanGame hangMan = new EvilHangmanGame();

        try {
            hangMan.startGame(file, wordLength);
        }
        catch (IOException | EmptyDictionaryException ex){
            System.out.println("Sorry, there was an Error with the file");
        }
        Scanner sc= new Scanner(System.in);
        do {
            System.out.printf("You have %d guesses left\n",maxGuess);
            System.out.print("Used letters: [");
            SortedSet<Character> prevGuesses;
            prevGuesses = hangMan.getGuessedLetters();
            for(char c: prevGuesses){
                System.out.print(c + ", ");
            }
            if(prevGuesses.size() > 0){
                System.out.print("\b\b");
            }
            System.out.print(']');
            String pattern = hangMan.getPattern();
            System.out.printf("\nWord: %s\n", pattern);

            boolean invalid;
            char guess;
            do {
                invalid = false;
                System.out.print("Enter guess: ");
                String filter = sc.next();
                if (filter.length() > 1) {
                    System.out.print("Invalid input! ");
                    invalid = true;
                }
                guess = filter.charAt(0);
                if(!Character.isAlphabetic(guess)) {
                    System.out.print("Invalid input! ");
                    invalid = true;
                }
                else{
                    try {
                        hangMan.makeGuess(guess);
                    } catch (GuessAlreadyMadeException ex) {
                        System.out.print("You already made that guess. ");
                        invalid = true;
                    }
                }
            }while(invalid);

            if(hangMan.getPattern().equals(pattern)){
                maxGuess--;
                if(maxGuess > 0){
                    System.out.printf("Sorry, there are no %c's\n\n",guess);
                }
            }
            else{
                numCorrect+= hangMan.getGuessCount(guess);
                if(numCorrect == wordLength){
                    break;
                }
                System.out.printf("Yes there is %d %c\n\n",hangMan.getGuessCount(guess),guess);
            }
        } while(maxGuess > 0);
        if(numCorrect == wordLength){
            System.out.print("Congrats you won, You guessed: ");
        }
        else{
            System.out.print("You lost! The word was: ");
        }
        System.out.print(hangMan.getFinal());
    }
}
