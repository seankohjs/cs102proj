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

    public static void initialize() {
        GameMenu.displayMenu();
        boolean start = GameMenu.readOptions(sc);
        while (start) {
            start = startGame();
        }
    }

    private static void gameInit() {
        // Create new deck and parade line
        deck = new Deck();
        paradeLine = new ParadeLine();
        isLastRound = false;

        // Get number of human and bot players
        System.out.println();
        System.out.print(Print.YELLOW + "ENTER THE NUMBER OF HUMAN PLAYERS" + Print.GREEN + " (1 TO 6) " + Print.YELLOW + ":: " + Print.DEFAULT);
        int numHumans = GameUtils.getValidInput(1, 6, sc);
        int numBots = 0;

        if (numHumans < 6) {
            // Only ask for bots if there's room for them
            int maxBots = 6 - numHumans;
            int minBots = (numHumans == 1) ? 1 : 0; // At least 1 bot required for single human player
            System.out.print(Print.YELLOW + "ENTER THE NUMBER OF BOT PLAYERS" + Print.GREEN + " (" + minBots + " TO " + maxBots + ") " + Print.YELLOW + ":: " + Print.DEFAULT);
            numBots = GameUtils.getValidInput(minBots, maxBots, sc);
        } else {
            // If 6 human players, no room for bots
            System.out.println(Print.ORANGE + "MAXIMUM PLAYERS REACHED (6). NO BOTS WILL BE ADDED." + Print.DEFAULT);
        }

        List<String> playerNames = new ArrayList<String>();

        // Get human player names and add them to the list of player names
        for (int i = 0; i < numHumans; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR PLAYER " + Print.GREEN + (i + 1) + Print.YELLOW + " :: "
                    + Print.DEFAULT);
            String playerName = sc.nextLine().strip().toUpperCase();

            while (playerNames.contains(playerName)) {
                if (playerName.isEmpty()) {
                    System.out.print(Print.YELLOW + "PLEASE ENTER A NON-EMPTY NAME FOR PLAYER " + Print.GREEN + (i + 1)
                            + Print.YELLOW + " :: " + Print.DEFAULT);
                    playerName = sc.nextLine().strip().toUpperCase();
                    continue;
                }
                System.out.println(Print.RED + "NAME ALREADY EXISTS!" + Print.DEFAULT);
                System.out.print(Print.YELLOW + "PLEASE ENTER ANOTHER NAME FOR PLAYER " + Print.GREEN + (i + 1)
                        + Print.YELLOW + " :: " + Print.DEFAULT);
                playerName = sc.nextLine().strip().toUpperCase();
            }

            playerNames.add(playerName);
            players.add(new Player(playerName));
        }

        // Get bot names and difficulties and add them to the list of player names
        for (int i = 0; i < numBots; i++) {
            System.out.print(Print.YELLOW + "ENTER NAME FOR BOT " + Print.GREEN + (i + 1) + Print.CYAN
                    + " (OR PRESS ENTER FOR BOT " + (i + 1) + ")" + Print.YELLOW + " :: " + Print.DEFAULT);
            String botName = sc.nextLine().strip().toUpperCase();
            if (botName.isEmpty()) {
                botName = "BOT " + (i + 1);
            }

            while (playerNames.contains(botName)) {
                System.out.println(Print.RED + "NAME ALREADY EXISTS!" + Print.DEFAULT);
                System.out
                        .print(Print.YELLOW + "PLEASE ENTER ANOTHER NAME FOR BOT " + (i + 1) + " :: " + Print.DEFAULT);
                botName = sc.nextLine().strip().toUpperCase();
            }

            // Display difficulty options for each bot
            System.out.println(Print.YELLOW + "SELECT DIFFICULTY FOR " + Print.CYAN + botName.toUpperCase()
                    + Print.YELLOW + " :: ");
            System.out.println(Print.GREEN + "\n1   ■   EASY   (RANDOM DECISIONS)" + Print.DEFAULT);
            System.out.println(Print.RED + "\n2   ■   HARD   (STRATEGIC DECISIONS)" + Print.DEFAULT);
            System.out.print(Print.YELLOW + "\nENTER DIFFICULTY " + Print.GREEN + "(1 OR 2)" + Print.YELLOW + " :: "
                    + Print.DEFAULT);

            int difficulty = GameUtils.getValidInput(1, 2, sc);
            playerNames.add(botName);
            players.add(new BotPlayer(botName, difficulty));
        }

        // Deal initial parade line
        paradeLine.addCardToLine(new Card(Color.PURPLE, 0));
        paradeLine.addCardToLine(new Card(Color.BLUE, 1));
        paradeLine.addCardToLine(new Card(Color.GREEN, 2));
        paradeLine.addCardToLine(new Card(Color.GREY, 3));
        paradeLine.addCardToLine(new Card(Color.ORANGE, 4));
        paradeLine.addCardToLine(new Card(Color.RED, 5));
        paradeLine.addCardToLine(new Card(Color.RED, 6));
        paradeLine.addCardToLine(new Card(Color.RED, 7));
        paradeLine.addCardToLine(new Card(Color.RED, 8));
        paradeLine.addCardToLine(new Card(Color.RED, 9));
        paradeLine.addCardToLine(new Card(Color.RED, 10));
        paradeLine.addCardToLine(new Card(Color.BLUE, 10));
        
        // Deal 5 cards to each player
        for (int i = 0; i < players.size(); i++) {
            if (i == 0) {
                players.get(i).addToHand(new Card(Color.GREY, 0));
                players.get(i).addToHand(new Card(Color.GREY, 1));
                players.get(i).addToHand(new Card(Color.GREY, 6));
                players.get(i).addToHand(new Card(Color.GREY, 10));
                players.get(i).addToHand(new Card(Color.GREY, 7));
            } else if (i == 1) {
                players.get(i).addToHand(new Card(Color.GREEN, 0));
                players.get(i).addToHand(new Card(Color.GREEN, 1));
                players.get(i).addToHand(new Card(Color.GREEN, 6));
                players.get(i).addToHand(new Card(Color.GREEN, 10));
                players.get(i).addToHand(new Card(Color.GREEN, 7));
            } else if (i == 2) {
                players.get(i).addToHand(new Card(Color.BLUE, 5));
                players.get(i).addToHand(new Card(Color.GREEN, 5));
                players.get(i).addToHand(new Card(Color.GREY, 5));
                players.get(i).addToHand(new Card(Color.ORANGE, 5));
                players.get(i).addToHand(new Card(Color.PURPLE, 5));
            } else if (i == 3) {
                players.get(i).addToHand(new Card(Color.PURPLE, 0));
                players.get(i).addToHand(new Card(Color.PURPLE, 1));
                players.get(i).addToHand(new Card(Color.PURPLE, 6));
                players.get(i).addToHand(new Card(Color.PURPLE, 10));
                players.get(i).addToHand(new Card(Color.PURPLE, 7));
            }
        }
    }

    // Main game controller loop
    public static boolean startGame() {
        gameInit();
        while (true) {

            // Display game status information for each round
            GameView.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            GameView.displayTurnHeader(currentPlayer);
            GameView.displayGameState(deck, paradeLine, isLastRound);
            GameView.displayPlayerCollections(currentPlayer);
            GameView.displayOtherPlayersCollections(players, currentPlayer);

            // Get each player's choice of card to play for each round
            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = turnManager.getPlayerCardChoice(currentPlayer, paradeLine, players);
                turnManager.playTurn(cardToPlay, paradeLine, deck, isLastRound);
            } else {
                System.out.println(Print.CYAN + currentPlayer.getPlayerName() + Print.PURPLE
                        + " HAS NO CARDS TO PLAY! PASSING TURN .." + Print.DEFAULT);
            }

            // Get user to continue to next round
            turnManager.promptForNextTurn(currentPlayer);

            // Check if current round is the last round and passes the turn to the next
            // player
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

        // Clear variables for next game
        deck = null;
        players.clear();
        paradeLine = null;

        return false;
    }

    // Final game stage to determine winner
    public static void endGame() {
        GameView.clearScreen();
        System.out.println(Print.RED + "\n|||   GAME OVER   |||" + Print.DEFAULT);
        System.out.println("\n■■■■■" + Print.ORANGE + "*** SCORING STAGE -" + Print.RED + " DISCARD " + Print.ORANGE
                + "2 CARDS FROM HAND ***" + Print.DEFAULT);

        for (Player player : players) {
            GameView.clearScreen();
            System.out.println("\n" + Print.CYAN + player.getPlayerName() + Print.YELLOW + ", IT'S TIME TO" + Print.RED
                    + " DISCARD 2 CARDS " + Print.YELLOW + "FROM YOUR HAND .." + Print.DEFAULT);
            System.out.println("\n" + Print.CYAN + player.getPlayerName() + "'S " + Print.YELLOW
                    + "CURRENT COLLECTION :: \n" + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.interactiveDiscardTwoCards(player);
            player.addHandToCollection();
            System.out.println(Print.ORANGE + "\nUPDATED GAME STATE FOR " + Print.CYAN + player.getPlayerName()
                    + Print.ORANGE + " :: \n" + Print.DEFAULT);
            GameView.displayPlayerCollections(player);
            turnManager.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        GameView.displayFinalScoreboard(players);

        // Wait for user acknowledgment before returning
        System.out.print(Print.ORANGE + "\nGAME COMPLETE! PRESS" + Print.RED + " [ENTER] " + Print.ORANGE
                + "TO RETURN TO THE MAIN MENU .. " + Print.DEFAULT);
        sc.nextLine(); // Wait for user to press Enter
    }

    // Check if current round meets game end conditions, where either a player has
    // collected six colors or the deck is empty
    public static void checkGameEndConditions() {
        if (!isLastRound) {
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true);
            } else if (deck.isEmpty()) {
                startLastRound(false);
            }
        }
    }

    // Check whether any player has collected all six colors
    public static boolean hasAnyoneCollectedSixColors() {
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

    // Start last round of the game
    public static void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0;
        System.out.println();
        System.out.println("\n■■■■■" + Print.RED + " ***   LAST ROUND STARTED   *** \n" + Print.DEFAULT);
        if (sixColors) {
            System.out.println(Print.YELLOW + "TRIGGERED BY A PLAYER COLLECTING 6 COLORS .." + Print.DEFAULT);
        } else {
            System.out.println(Print.YELLOW + "TRIGGERED BY DECK EXHAUSTION .." + Print.DEFAULT);
        }
    }
}
