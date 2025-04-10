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
        // Create new deck
        deck = new Deck();
        paradeLine = new ParadeLine();
        isLastRound = false;

        System.out.print(Print.YELLOW + "ENTER THE NUMBER OF HUMAN PLAYERS" + Print.GREEN + " (1 TO 5) " + Print.YELLOW + ":: " + Print.DEFAULT);
        
        int numHumans = GameUtils.getValidInput(1, 5, sc);

        int numBots;
        if (numHumans == 1) {
            System.out.print(Print.YELLOW + "ENTER THE NUMBER OF BOT PLAYERS" +
                             Print.GREEN + " (1 TO " + (6 - numHumans) + ") " + Print.YELLOW + ":: " + Print.DEFAULT);
            numBots = GameUtils.getValidInput(1, 6 - numHumans, sc);
        } else {
            System.out.print(Print.YELLOW + "ENTER THE NUMBER OF BOT PLAYERS" + 
                             Print.GREEN + " (0 TO " + (6 - numHumans) + ") " + Print.YELLOW + ":: " + Print.DEFAULT);
            numBots = GameUtils.getValidInput(0, 6 - numHumans, sc);
        }

        List<String> playerNames = new ArrayList<String>();

        // Get human player names and add to list
        for (int i = 0; i < numHumans; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
            String playerName = sc.nextLine().strip();

            while (playerNames.contains(playerName)) {
                if (playerName.isEmpty()) {
                    System.out.print(Print.YELLOW + "PLEASE ENTER A NON-EMPTY NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
                    playerName = sc.nextLine().strip();
                    continue;
                }
                System.out.println(Print.RED + "NAME ALREADY EXISTS!" + Print.DEFAULT);
                System.out.print(Print.YELLOW + "PLEASE ENTER ANOTHER NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: " + Print.DEFAULT);
                playerName = sc.nextLine().strip();
            }

                            
            playerNames.add(playerName.toUpperCase());
            players.add(new Player(playerName.toUpperCase()));
        }

        // Get bot names and difficulties and add to list
        for (int i = 0; i < numBots; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR BOT " + Print.GREEN + (i+1) + Print.CYAN + " (OR PRESS ENTER FOR BOT " + (i+1) + ")" + Print.YELLOW +  " :: " + Print.DEFAULT);
            String botName = sc.nextLine().strip();
            if (botName.isEmpty()) {
                botName = "BOT " + (i + 1);
            }

            while (playerNames.contains(botName)) {
                System.out.print(Print.BOLD + Print.RED + "NAME ALREADY EXISTS! ");
                System.out.print(Print.BOLD + Print.DEFAULT + "PLEASE ENTER ANOTHER NAME FOR BOT " + (i + 1) + " :: ");
                botName = sc.nextLine().strip();
            }
            int difficulty = GameUtils.getValidInput(1, 3, sc);
            playerNames.add(botName.toUpperCase());
            players.add(new BotPlayer(botName.toUpperCase(), difficulty));
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
                System.out.println(Print.CYAN + currentPlayer.getPlayerName() + Print.PURPLE + " HAS NO CARDS TO PLAY! PASSING TURN .." + Print.DEFAULT);
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
        System.out.println(Print.RED + "\n|||   GAME OVER   |||" + Print.DEFAULT);
        System.out.println("\n■■■■■" + Print.ORANGE + "*** SCORING STAGE -" + Print.RED + " DISCARD " + Print.ORANGE + "2 CARDS FROM HAND ***" + Print.DEFAULT + "■■■■■");

        for (Player player : players) {
            GameView.clearScreen();
            System.out.println("\n" + Print.CYAN + player.getPlayerName() + Print.YELLOW + ", IT'S TIME TO" + Print.RED + " DISCARD " + Print.YELLOW + "2 CARDS FROM YOUR HAND .." + Print.DEFAULT);
            System.out.println(Print.CYAN + player.getPlayerName() + "'S " + Print.YELLOW + "CURRENT COLLECTION :: " + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.interactiveDiscardTwoCards(player);
            player.addHandToCollection();
            System.out.println(Print.ORANGE + "\nUPDATED GAME STATE FOR " + Print.CYAN + player.getPlayerName() + Print.ORANGE + " :: " + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        GameView.displayFinalScoreboard(players);

        // Wait for user acknowledgment before returning
        System.out.print(Print.ORANGE + "\nGAME COMPLETE! PRESS" + Print.RED + " [ENTER] " + Print.ORANGE + "TO RETURN TO THE MAIN MENU .." + Print.DEFAULT);
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
        System.out.println("\n■■■■■" + Print.RED + "*** LAST ROUND STARTED ***" + Print.DEFAULT + "■■■■■");
        if (sixColors) {
            System.out.println(Print.YELLOW + "TRIGGERED BY A PLAYER COLLECTING 6 COLORS .." + Print.DEFAULT);
        } else {
            System.out.println(Print.YELLOW + "TRIGGERED BY DECK EXHAUSTION .." + Print.DEFAULT);
        }
    }
}
