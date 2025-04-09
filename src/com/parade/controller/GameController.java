package com.parade.controller;

import java.util.*;
import com.parade.model.*;
import com.parade.view.*;
import com.parade.util.*;
import com.parade.ai.BotPlayer;


public class GameController {
    private static int extraTurnCount = 0;
    private static boolean isLastRound = false;


    public static Scanner sc = new Scanner(System.in);
    private static Deck deck = null;
    private static List<Player> players = new ArrayList<Player>();
    private static ParadeLine paradeLine;
    private static TurnManager turnManager = new TurnManager(players, sc);

    public static void initialize(){
        GameMenu.display();
        boolean start = GameMenu.readOptions(sc);
        while(start){
            start = startGame();
        }
    }

    private static void gameInit(){
        // create new deck
        deck = new Deck();
        paradeLine = new ParadeLine();
        isLastRound = false;

        System.out.print(Print.BOLD + Print.YELLOW + "ENTER THE NUMBER OF HUMAN PLAYERS (1 TO 5) :: " + Print.DEFAULT);
        int numHumans = GameUtils.getValidInput(1, 5, sc);

        int numBots;
        if (numHumans == 1) {
            System.out.print(Print.BOLD + "ENTER THE NUMBER OF BOT PLAYERS (1 TO " + (6 - numHumans) + ") :: " + Print.DEFAULT);
            numBots = GameUtils.getValidInput(1, 6 - numHumans, sc);
        } else {
            System.out.print(Print.BOLD + "ENTER THE NUMBER OF BOT PLAYERS (0 TO " + (6 - numHumans) + ") :: " + Print.DEFAULT);
            numBots = GameUtils.getValidInput(0, 6 - numHumans, sc);
        }

        List<String> playerNames = new ArrayList<String>();

        // Get human player names and add to list
        for (int i = 0; i < numHumans; i++) {
            System.out.print(Print.BOLD + "ENTER NAME FOR PLAYER " + (i + 1) + " :: ");
            String playerName = sc.nextLine();

            while (playerNames.contains(playerName)) {
                System.out.print(Print.BOLD + Print.RED + "NAME ALREADY EXISTS! ");
                System.out.print(Print.BOLD + Print.DEFAULT + "PLEASE ENTER ANOTHER NAME FOR PLAYER " + (i + 1) + " :: ");
                playerName = sc.nextLine();
            }
            playerNames.add(playerName);
            players.add(new Player(playerName));
        }

        // Get bot names and difficulties and add to list
        for (int i = 0; i < numBots; i++) {
            System.out.print(Print.BOLD + "ENTER NAME FOR BOT " + (i+1) + " (OR PRESS ENTER FOR BOT " + (i+1) + ") :: " + Print.DEFAULT);
            String botName = sc.nextLine();
            if (botName.isEmpty()) {
                botName = "BOT " + (i + 1);
            }

            while (playerNames.contains(botName)) {
                System.out.print(Print.BOLD + Print.RED + "NAME ALREADY EXISTS! ");
                System.out.print(Print.BOLD + Print.DEFAULT + "PLEASE ENTER ANOTHER NAME FOR BOT " + (i + 1) + " :: ");
                botName = sc.nextLine();
            }
            
            System.out.print(Print.BOLD + "SELECT DIFFICULTY FOR " + Print.PURPLE + botName + Print.GREY + " [1] (EASY)  [2] (MEDIUM)  [3] (HARD) :: " + Print.DEFAULT);
            int difficulty = GameUtils.getValidInput(1, 3, sc);

            playerNames.add(botName);
            players.add(new BotPlayer(botName, difficulty));
        }

        // Deal initial parade line of 6 cards
        for (int i = 0; i < 6; i++) {
            Card card = deck.drawCard();
            if (card != null) {
                paradeLine.addCardToLine(card);
            }
        }
        // Deal 5 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addToHand(card);
                }
            }
        }
    }

    public static boolean startGame() {
        gameInit();
        while (true) {
            GameView.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            GameView.displayTurnHeader(currentPlayer);
            GameView.displayGameState(deck, paradeLine, isLastRound);
            GameView.displayPlayerCollections(currentPlayer);
            GameView.displayOtherPlayersCollections(players, currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = turnManager.getPlayerCardChoice(currentPlayer, paradeLine, players);
                turnManager.playTurn(cardToPlay, paradeLine, deck, isLastRound);
            } else {
                System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " HAS NO CARDS TO PLAY! PASSING TURN .." + Print.DEFAULT);
            }

            turnManager.promptForNextTurn(currentPlayer);

            if (!isLastRound) {
                checkGameEndConditions();
                turnManager.nextPlayer();
            } else {
                turnManager.nextPlayer();
                extraTurnCount++;
                if (extraTurnCount >= players.size()) {
                    // All players have had their final turn - end the game
                    break;
                }
            }
        }

        // Call endGame() after exiting the main game loop
        endGame();

        deck = null;
        players.clear();
        paradeLine = null;
        return false;
    }

    private static void endGame() {
        GameView.clearScreen();
        System.out.println(Print.BOLD + Print.RED + "\nGAME OVER" + Print.DEFAULT);
        System.out.println(Print.BOLD + "\n■■■■■ *** SCORING STAGE -" + Print.RED + " DISCARD " + Print.GREY + "2 CARDS FROM HAND *** ■■■■■" + Print.DEFAULT);

        for (Player player : players) {
            GameView.clearScreen();
            System.out.println("\n" + player.getPlayerName() + ", IT'S TIME TO" + Print.RED + Print.BOLD + " DISCARD " + Print.GREY + "2 CARDS FROM YOUR HAND .." + Print.DEFAULT);
            System.out.println(Print.BOLD + Print.GREEN + player.getPlayerName() + "'S " + Print.GREY + "CURRENT COLLECTION :: " + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.interactiveDiscardTwoCards(player);
            player.addHandToCollection();
            System.out.println(Print.BOLD + "\nUPDATED GAME STATE FOR " + Print.GREEN + player.getPlayerName() + Print.GREY + " :: " + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        GameView.displayFinalScoreboard(players);

        // Wait for user acknowledgment before returning
        System.out.print(Print.BOLD + "\nGAME COMPLETE! PRESS" + Print.RED + " [ENTER] " + Print.GREY + "TO RETURN TO THE MAIN MENU .." + Print.DEFAULT);
        sc.nextLine(); // Wait for user to press Enter
    }

    private static void checkGameEndConditions() {
        if (!isLastRound) {
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true);
            } else if (deck.isEmpty()) {
                startLastRound(false);
            }
        }
    }

    private static boolean hasAnyoneCollectedSixColors() {
        for (Player player : players) {
            Set<Color> collectedColors = new HashSet<>();
            for (Card card : player.getCollectedCards()) {
                collectedColors.add(card.getColor());
            }
            if (collectedColors.size() >= 6) {
                return true;
            }
        }
        return false;
    }

    private static void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0;
        System.out.println(Print.BOLD + Print.YELLOW + "\n■■■■■ *** LAST ROUND STARTED *** ■■■■■" + Print.DEFAULT);
        if (sixColors) {
            System.out.println(Print.BOLD + "TRIGGERED BY A PLAYER COLLECTING 6 COLORS .." + Print.DEFAULT);
        } else {
            System.out.println(Print.BOLD + "TRIGGERED BY DECK EXHAUSTION .." + Print.DEFAULT);
        }
    }
}
