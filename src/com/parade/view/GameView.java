package com.parade.view;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import com.parade.model.*;
import com.parade.util.*;
import com.parade.ai.BotPlayer;

public class GameView {
    private Scanner scanner;

    public GameView(Scanner scanner) {
        this.scanner = scanner;
    }

    // Clears the console screen.
    public void clearScreen() {
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

    // Displays a header for the current turn.
    public void displayTurnHeader(Player currentPlayer) {
        System.out.println();
        System.out.println("■■■■■");
        System.out
                .println(Print.BOLD + "IT'S " + Print.ORANGE + currentPlayer.getPlayerName() + Print.GREY + "'S TURN!");
    }

    // Displays the current game state.
    public void displayGameState(Deck deck, ParadeLine paradeLine, boolean isLastRound) {
        if (isLastRound) {
            System.out.println(Print.BOLD + Print.ORANGE + "■■■■■ ***** LAST ROUND ***** ■■■■■" + Print.RESET);
        }
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■ GAME STATE ■■■■■" + Print.RESET);
        System.out.println(Print.BOLD + "PARADE LINE:" + Print.RESET);
        System.out.println(GameUtils.cardsToString(paradeLine.getParadeLineCards()));
        System.out.println(Print.BOLD + "CARDS IN DECK: " + deck.getCardCount() + Print.RESET);
    }

    // Displays a message (a simple wrapper).
    public void displayMessage(String msg) {
        System.out.println();
        System.out.println(msg);
    }

    // Displays the current player's collection.
    public void displayPlayerCollections(Player player) {
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■ " + player.getPlayerName() + "'S COLLECTED CARDS ■■■■■" + Print.RESET);
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
            System.out.println(Print.BOLD + "NO CARDS COLLECTED YET .." + Print.RESET);
        }
    }

    // Displays other players' collections.
    public void displayOtherPlayersCollections(List<Player> players, Player currentPlayer) {
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■ OTHER PLAYERS' COLLECTIONS ■■■■■" + Print.RESET);
        for (Player player : players) {
            if (player != currentPlayer) {
                System.out.println(Print.BOLD + player.getPlayerName() + "'S COLLECTED CARDS ::" + Print.RESET);
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
                System.out.println();
            }
        }
    }

    // Prompts the user to press 'y' to continue and shows a countdown.
    public void promptForNextTurn(Player player) {
        System.out.println();

        // If player is a bot, automatically continue after a short delay
        if (player.isBot()) {
            System.out.println(
                    Print.BOLD + "BOT PLAYER " + player.getPlayerName() + " IS READY TO CONTINUE .." + Print.RESET);
            try {
                Thread.sleep(1000); // 2 second delay so human players can read what happened
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            return;
        }

        // Original human player logic
        System.out.print(Print.BOLD + "ENTER [Y] TO CONTINUE :: " + Print.RESET);
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("y")) {
            System.out.print(Print.BOLD + "PLEASE ENTER [Y] TO CONTINUE :: " + Print.RESET);
            input = scanner.nextLine();
        }
        try {
            for (int i = 2; i > 0; i--) {
                System.out.println(
                        Print.BOLD + "NEXT TURN IN " + i + " SECOND" + (i > 1 ? "S" : "") + " .." + Print.RESET);
                Thread.sleep(5);
            }
        } catch (InterruptedException e) {
            // Ignore interruption.
        }
    }

    // Interactively prompts the user to discard cards until only 2 remain.
    public void interactiveDiscardTwoCards(Player player) {
        // Check if the player is a bot
        if (player.isBot()) {
            BotPlayer bot = (BotPlayer) player;
            // Bot logic for discarding cards
            while (player.getHand().size() > 2) {
                System.out.println(Print.BOLD + "\n" + player.getPlayerName()
                        + " (BOT) IS CHOOSING A CARD TO DISCARD .." + Print.RESET);

                try {
                    Thread.sleep(1000); // Simulate thinking
                } catch (InterruptedException e) {
                    // Ignore interruption
                }

                // Use the bot's discard logic
                Card discarded = bot.selectCardToDiscard();
                player.getHand().remove(discarded);
                System.out.println();
                System.out.println(Print.BOLD + "■■■■■ " + player.getPlayerName() + " DISCARDED ■■■■■\n\n" + Print.RESET
                        + discarded);
            }
            return;
        }
        while (player.getHand().size() > 2) {
            System.out.println();
            System.out.println(Print.BOLD + player.getPlayerName() + ", CHOOSE A CARD TO" + Print.RED + " DISCARD "
                    + Print.GREY + "FROM YOUR HAND :: " + Print.RESET);
            System.out.println();
            System.out.println(GameUtils.handToString(player.getHand()));
            System.out.println();
            int index = 0;
            while (true) {
                System.out.print(Print.BOLD + "ENTER THE INDEX OF THE CARD YOU WANT TO" + Print.RED + Print.BOLD
                        + " DISCARD :: " + Print.RESET);
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                    scanner.nextLine();
                    if (index >= 1 && index <= player.getHand().size()) {
                        break;
                    } else {
                        System.out.println(Print.BOLD + Print.RED + "INVALID INDEX, PLEASE TRY AGAIN .." + Print.RESET);
                    }
                } else {
                    System.out
                            .println(Print.BOLD + Print.RED + "INVALID INPUT, PLEASE ENTER A NUMBER .." + Print.RESET);
                    scanner.nextLine();
                }
            }
            Card discarded = player.getHand().remove(index - 1);
            System.out.println();
            System.out.println(
                    Print.BOLD + "■■■■■ " + player.getPlayerName() + " DISCARDED ■■■■■\n\n" + Print.RESET + discarded);
        }
    }

    // Prompts the player for a card choice from their hand.
    public Card getPlayerCardChoice(Player currentPlayer, ParadeLine paradeLine, List<Player> allPlayers) {
        // If player is a bot, use the bot's card selection logic
        if (currentPlayer.isBot()) {
            // Cast is safe because we checked isBot()
            BotPlayer bot = (BotPlayer) currentPlayer;
            Card selectedCard = bot.selectCard(paradeLine, allPlayers);

            // Remove and return the selected card
            currentPlayer.getHand().remove(selectedCard);

            // Show which card the bot selected
            System.out.println();
            System.out.println(Print.BOLD + currentPlayer.getPlayerName() + " IS THINKING .." + Print.RESET);
            System.out.println();
            try {
                Thread.sleep(1000); // Add delay to simulate "thinking"
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            System.out.println();
            System.out.println(
                    Print.BOLD + currentPlayer.getPlayerName() + " PLAYS CARD ::\n" + selectedCard + Print.RESET);
            System.out.println();

            return selectedCard;
        }

        // Original human player logic
        while (true) {
            System.out.println(Print.BOLD + "\nYOUR HAND :: " + Print.RESET);
            System.out.println(GameUtils.handToString(currentPlayer.getHand()));
            System.out.println();
            System.out.print(Print.BOLD + "ENTER THE INDEX OF THE CARD YOU WANT TO PLAY (1 TO " +
                    (currentPlayer.getHand().size()) + ") :: " + Print.RESET);
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                // Fix: Check that index is between 1 and hand size (inclusive)
                if (index >= 1 && index <= currentPlayer.getHand().size()) {
                    return currentPlayer.getHand().remove(index - 1);
                } else {
                    // Improved error message that explicitly states the valid range
                    System.out.println(
                            Print.BOLD + Print.RED + "INVALID CARD INDEX! PLEASE ENTER A NUMBER BETWEEN 1 AND " +
                                    currentPlayer.getHand().size() + " .." + Print.RESET);
                }
            } else {
                System.out.println(Print.BOLD + "INVALID INPUT, PLEASE ENTER A NUMBER .." + Print.RESET);
                scanner.nextLine();
            }
        }
    }

}