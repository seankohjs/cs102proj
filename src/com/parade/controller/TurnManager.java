package com.parade.controller;


import java.util.*;
import com.parade.util.*;
import com.parade.model.*;
import com.parade.view.GameView;
import com.parade.ai.BotPlayer;

public class TurnManager {
    private List<Player> players;
    private int currentPlayerIndex;
    private Scanner scanner;

    public TurnManager(List<Player> players, Scanner scanner) {
        this.players = players;
        this.scanner = scanner;
        currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Prompts the user to press 'y' to continue and shows a countdown.
    public void promptForNextTurn(Player player) {
        System.out.println();

        // If player is a bot, automatically continue after a short delay
        if (player.isBot()) {
            System.out.println(
                    Print.BOLD + "BOT PLAYER " + player.getPlayerName() + " IS READY TO CONTINUE .." + Print.DEFAULT);
            try {
                Thread.sleep(1000); // 2 second delay so human players can read what happened
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            return;
        }

        // Original human player logic
        System.out.print(Print.BOLD + "ENTER [Y] TO CONTINUE :: " + Print.DEFAULT);
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("y")) {
            System.out.print(Print.BOLD + "PLEASE ENTER [Y] TO CONTINUE :: " + Print.DEFAULT);
            input = scanner.nextLine();
        }
        try {
            for (int i = 2; i > 0; i--) {
                System.out.println(
                        Print.BOLD + "NEXT TURN IN " + i + " SECOND" + (i > 1 ? "S" : "") + " .." + Print.DEFAULT);
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
                        + " (BOT) IS CHOOSING A CARD TO DISCARD .." + Print.DEFAULT);

                try {
                    Thread.sleep(1000); // Simulate thinking
                } catch (InterruptedException e) {
                    // Ignore interruption
                }

                // Use the bot's discard logic
                Card discarded = bot.selectCardToDiscard();
                player.getHand().remove(discarded);
                System.out.println();
                System.out.println(Print.BOLD + "■■■■■ " + player.getPlayerName() + " DISCARDED ■■■■■\n\n" + Print.DEFAULT
                        + discarded);
            }
            return;
        }
        while (player.getHand().size() > 2) {
            System.out.println();
            System.out.println(Print.BOLD + player.getPlayerName() + ", CHOOSE A CARD TO" + Print.RED + " DISCARD "
                    + Print.GREY + "FROM YOUR HAND :: " + Print.DEFAULT);
            System.out.println();
            System.out.println(GameUtils.handToString(player.getHand()));
            System.out.println();
            int index = 0;
            while (true) {
                System.out.print(Print.BOLD + "ENTER THE INDEX OF THE CARD YOU WANT TO" + Print.RED + Print.BOLD
                        + " DISCARD :: " + Print.DEFAULT);
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                    scanner.nextLine();
                    if (index >= 1 && index <= player.getHand().size()) {
                        break;
                    } else {
                        System.out.println(Print.BOLD + Print.RED + "INVALID INDEX, PLEASE TRY AGAIN .." + Print.DEFAULT);
                    }
                } else {
                    System.out
                            .println(Print.BOLD + Print.RED + "INVALID INPUT, PLEASE ENTER A NUMBER .." + Print.DEFAULT);
                    scanner.nextLine();
                }
            }
            Card discarded = player.getHand().remove(index - 1);
            System.out.println();
            System.out.println(
                    Print.BOLD + "■■■■■ " + player.getPlayerName() + " DISCARDED ■■■■■\n\n" + Print.DEFAULT + discarded);
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
            System.out.println(Print.BOLD + currentPlayer.getPlayerName() + " IS THINKING .." + Print.DEFAULT);
            System.out.println();
            try {
                Thread.sleep(1000); // Add delay to simulate "thinking"
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            System.out.println();
            System.out.println(
                    Print.BOLD + currentPlayer.getPlayerName() + " PLAYS CARD ::\n" + selectedCard + Print.DEFAULT);
            System.out.println();

            return selectedCard;
        }

        // Original human player logic
        System.out.println("\nYOUR HAND :: " + Print.DEFAULT);
        System.out.println(GameUtils.handToString(currentPlayer.getHand()));
        System.out.println("");

        while (true) {
            System.out.print("ENTER THE INDEX OF THE CARD YOU WANT TO PLAY (1 TO " +
                    (currentPlayer.getHand().size()) + ") :: " + Print.DEFAULT);
            try {
                int index = Integer.parseInt(scanner.nextLine());

                if (index < 1 || index > currentPlayer.getHand().size()) {
                    System.out.println(Print.RED + "INVALID CARD INDEX! PLEASE ENTER A NUMBER BETWEEN 1 AND " +
                            currentPlayer.getHand().size() + " .. " + Print.DEFAULT);
                    continue;
                }

                System.out.println(Print.YELLOW + "YOU HAVE CHOSEN TO PLAY");
                System.out.println("\n" + currentPlayer.getHand().get(index - 1) + "\n");

                while (true) {
                    System.out.println(
                            Print.YELLOW + "PLEASE ENTER" + Print.RED + " [Y] " + Print.YELLOW + "TO CONFIRM OR" +
                                    Print.RED + " [N] " + Print.YELLOW + "TO CHOOSE AGAIN :: ");
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("Y")) {
                        return currentPlayer.getHand().remove(index - 1);
                    } else if (confirm.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println(Print.RED + "INVALID INPUT! \n");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(Print.RED + "INVALID INPUT, PLEASE ENTER A NUMBER .. \n" + Print.DEFAULT);
                continue;
            }
        }

    }

    public void playTurn(Card playedCard, ParadeLine paradeLine, Deck deck, boolean isLastRound) {
        Player currentPlayer = getCurrentPlayer();
        System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " PLAYS ::\n\n"
                + Print.DEFAULT + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard, paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY
                    + " TAKES THE FOLLOWING CARDS FROM THE PARADE ..");
            System.out.println(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY
                    + " TAKES NO CARD FROM THE PARADE ..");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY
                        + " DRAWS A CARD .." + Print.DEFAULT);
            } else {
                System.out.println(Print.BOLD + Print.RED + "DECK IS EMPTY, NO CARD DRAWN .." + Print.DEFAULT);
            }
            System.out.println("\n" + Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY
                    + "'S CURRENT HAND :: " + Print.DEFAULT);
            System.out.println(GameUtils.handToString(currentPlayer.getHand()));
        } else {
            System.out.println(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY
                    + " DOES NOT DRAW A CARD IN THE LAST ROUND .." + Print.DEFAULT);
        }
        GameView.displayPlayerCollections(currentPlayer);
    }
}