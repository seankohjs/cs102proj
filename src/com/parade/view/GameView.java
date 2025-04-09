package com.parade.view;

import java.util.*;
import com.parade.model.*;
import com.parade.util.*;
import com.parade.controller.ScoreCalculator;

public class GameView{
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            for (int i = 0; i < 100; i++) {
                System.out.println();
            }
        }
    }

    public static void displayFinalScoreboard(List<Player> players) {
        clearScreen();
        System.out.println("■■■■■" + Print.RED + " *** |||   FINAL RESULTS   ||| *** "  + Print.DEFAULT + "■■■■■");
        System.out.println("■■■■■" + Print.YELLOW + "FINAL PLAYER COLLECTIONS" + Print.DEFAULT);

        for (Player player : players) {
            displayPlayerCollections(player);
            System.out.println(); // Add spacing between players
        }

        // Calculate and display all scores
        Map<Color, List<Player>> colorMajorities = ScoreCalculator.determineColorMajorities(players);

        System.out.println("■■■■■" + Print.RED + " *** |||   FINAL SCORES   ||| *** "  + Print.DEFAULT + "■■■■■");
        System.out.println();
        System.out.println(Print.PURPLE + "PLAYER               SCORE" + Print.DEFAULT);
        System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■");

        // Store scores for determining winner
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = ScoreCalculator.calculatePlayerFinalScore(player, colorMajorities);
            playerScores.put(player, score);

            // Format the score line with proper spacing
            String scoreLine = String.format("%-20s %d", player.getPlayerName(), score);
            System.out.println(scoreLine);
        }

        // Determine and announce the winner
        List<Player> winner = ScoreCalculator.determineWinner(players);
        for (Player player : winner) {
            int winnerScore = playerScores.get(player);
            System.out.println("\n" + Print.YELLOW + "WINNER: " + Print.CYAN + player.getPlayerName() + Print.YELLOW + " WITH " + winnerScore + " POINTS!" + Print.DEFAULT);
        }

        // Display color majorities
        System.out.println();
        System.out.println("■■■■■" + " |||   SUIT MAJORITIES   ||| " + "■■■■■" + Print.DEFAULT);
        System.out.println();
        for (Color color : Color.values()) {
            List<Player> majorityPlayers = colorMajorities.get(color);
            if (majorityPlayers != null && !majorityPlayers.isEmpty()) {
                System.out.print(Card.getDisplayColor(color) + color + Print.DEFAULT + ": ");
                for (int i = 0; i < majorityPlayers.size(); i++) {
                    System.out.print(majorityPlayers.get(i).getPlayerName());
                    if (i < majorityPlayers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println(Card.getDisplayColor(color) + color + Print.DEFAULT + Print.YELLOW + " :: NO MAJORITY" + Print.DEFAULT);
            }
        }
    }

    // Displays the current player's collection.
    public static void displayPlayerCollections(Player player) {
        System.out.println();
        System.out.println("■■■■■ " + Print.CYAN + player.getPlayerName() + "'S" + Print.YELLOW + " COLLECTED CARDS\n" + Print.DEFAULT);
        Map<Color, List<Card>> collectionsByColor = new HashMap<>();
        for (Color color : Color.values()) {
            collectionsByColor.put(color, new ArrayList<>());
        }
        for (Card card : player.getCollectedCards()) {
            collectionsByColor.get(card.getColor()).add(card);
        }
        for (Color color : Color.values()) {
            List<Card> cards = collectionsByColor.get(color);
            if (!cards.isEmpty()) {
                System.out.println(GameUtils.cardsToString(cards));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println(Print.YELLOW + "NO CARDS COLLECTED YET .." + Print.DEFAULT);
        }
    }

    // Displays other players' collections.
    public static void displayOtherPlayersCollections(List<Player> players, Player currentPlayer) {
        System.out.println();
        System.out.println("■■■■■" + Print.YELLOW + " OTHER PLAYERS' COLLECTIONS\n" + Print.DEFAULT);
        for (Player player : players) {
            if (player != currentPlayer) {
                System.out.println(Print.CYAN + player.getPlayerName() + "'S" + Print.YELLOW + " COLLECTED CARDS ::\n" + Print.DEFAULT);
                Map<Color, List<Card>> collectionsByColor = new HashMap<>();
                for (Color color : Color.values()) {
                    collectionsByColor.put(color, new ArrayList<>());
                }
                for (Card card : player.getCollectedCards()) {
                    collectionsByColor.get(card.getColor()).add(card);
                }
                for (Color color : Color.values()) {
                    List<Card> cards = collectionsByColor.get(color);
                    if (!cards.isEmpty()) {
                        System.out.println(GameUtils.cardsToString(cards));
                    }
                }
            }
        }
    }

    // Displays a header for the current turn.
    public static void displayTurnHeader(Player currentPlayer) {
        System.out.println();
        System.out.println(Print.GREY + "■■■■■" + Print.DEFAULT);
        System.out.println(Print.YELLOW + "IT'S " + Print.CYAN + currentPlayer.getPlayerName() + "'S" + Print.YELLOW + " TURN!" + Print.DEFAULT);
    }

    // Displays the current game state.
    public static void displayGameState(Deck deck, ParadeLine paradeLine, boolean isLastRound) {
        if (isLastRound) {
            System.out.println("■■■■■" + Print.RED + " *** |||   LAST ROUND   ||| *** "  + Print.DEFAULT + "■■■■■");
        }
        System.out.println();
        System.out.println("■■■■■" + Print.RED + " |||   GAME STATE   ||| " + Print.DEFAULT + "■■■■■");
        System.out.println("■■■■■" + Print.GREEN + " >>>   PARADE LINE" + Print.DEFAULT);
        System.out.println(GameUtils.cardsToString(paradeLine.getParadeLineCards()));
        System.out.println("■■■■■" + Print.GREEN + " >>> CARDS IN DECK :: " + Print.ORANGE + "[ " + deck.getCardCount() + " ]" + Print.DEFAULT);
    }
}
