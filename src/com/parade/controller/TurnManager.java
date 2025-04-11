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

        // If player is a bot, automatically continue after a short delay
        if (player.isBot()) {
            System.out.println();
            System.out.print(Print.PURPLE + "BOT PLAYER " + Print.CYAN + player.getPlayerName() + Print.PURPLE + " IS READY TO CONTINUE .. " + Print.DEFAULT);
            System.out.println();
            try {
                Thread.sleep(3000); // 3 second delay so human players can read what happened
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            return;
        }

        // Original human player logic
        System.out.println();
        System.out.print(Print.YELLOW + "PRESS ENTER TO CONTINUE :: " + Print.DEFAULT);
        scanner.nextLine();
        // while (!input.equalsIgnoreCase("Y")) {
        //     System.out.print(Print.YELLOW + "ENTER" + Print.RED + " [Y] " + Print.YELLOW + "TO CONTINUE :: " + Print.DEFAULT);
        //     input = scanner.nextLine();
        // }
        try {
            for (int i = 3; i > 0; i--) {
                System.out.print(Print.PURPLE + "\nNEXT TURN IN " + i + " SECOND" + (i > 1 ? "S" : "") + " .. " + Print.DEFAULT);
                System.out.println();
                Thread.sleep(1000);
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
                System.out.println("\n" + Print.CYAN + player.getPlayerName() + Print.PURPLE + " (BOT) IS CHOOSING A CARD TO DISCARD .." + Print.DEFAULT);

                try {
                    Thread.sleep(3000); // Simulate thinking
                } catch (InterruptedException e) {
                    // Ignore interruption
                }

                // Use the bot's discard logic
                Card discarded = bot.selectCardToDiscard();
                player.getHand().remove(discarded);
                System.out.println();
                System.out.println("■■■■■ " + Print.CYAN + player.getPlayerName() + Print.RED + " DISCARDED\n\n" + Print.DEFAULT + discarded);
            }
            return;
        }

        while (player.getHand().size() > 2) {
            System.out.println();
            System.out.println(Print.CYAN + player.getPlayerName() + Print.YELLOW + ", CHOOSE A CARD TO" + Print.RED + " DISCARD " + Print.YELLOW + "FROM YOUR HAND :: " + Print.DEFAULT);
            System.out.println();
            System.out.println(GameUtils.handToString(player.getHand()));
            int index = 0;
            boolean discarded = false;

            while (!discarded) {
                System.out.print(Print.YELLOW + "\nENTER THE INDEX OF THE CARD YOU WANT TO" + Print.RED + " DISCARD" + Print.YELLOW + " :: " + Print.DEFAULT);
                try {
                    index = Integer.parseInt(scanner.nextLine());
                    if (index < 1 || index > player.getHand().size()) {
                        System.out.println(Print.RED + "INVALID CARD INDEX! PLEASE ENTER A NUMBER BETWEEN 1 AND " + player.getHand().size() + " .. " + Print.DEFAULT);
                        continue;
                    }

                    System.out.println(Print.YELLOW + "\nYOU HAVE CHOSEN TO DISCARD ::" + Print.DEFAULT);
                    System.out.println("\n" + player.getHand().get(index - 1));

                    while (true) {
                        System.out.print(Print.YELLOW + "\nPLEASE ENTER" + Print.RED + " [Y] " + Print.YELLOW + "TO CONFIRM OR" +
                                                                           Print.RED + " [N] " + Print.YELLOW + "TO CHOOSE AGAIN :: " + Print.DEFAULT);
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("Y")) {
                            discarded = true;
                            Card discardedCard = player.getHand().remove(index - 1);
                            System.out.println();
                            System.out.println("■■■■■ " + Print.CYAN + player.getPlayerName() + " DISCARDED\n\n" + Print.DEFAULT + discardedCard);
                            break;
                        } else if (confirm.equalsIgnoreCase("N")) {
                            break;
                        } else {
                            System.out.println(Print.RED + "INVALID INPUT!" + Print.DEFAULT);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println(Print.RED + "INVALID INPUT, PLEASE ENTER A NUMBER .." + Print.DEFAULT);
                    continue;
                }
            }
            
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
            System.out.println(Print.CYAN + currentPlayer.getPlayerName() + Print.PURPLE + " IS THINKING .. " + Print.DEFAULT);
            try {
                Thread.sleep(3000); // Add delay to simulate "thinking"
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.PURPLE + " PLAYS CARD ::\n\n" + selectedCard + Print.DEFAULT);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // Ignore Exception
            }

            return selectedCard;
        }

        // Original human player logic
        System.out.println();
        System.out.println("■■■■■" + Print.YELLOW + " YOUR HAND ::\n" + Print.DEFAULT);
        System.out.println(GameUtils.handToString(currentPlayer.getHand()));
        System.out.println();

        while (true) {
            System.out.print(Print.YELLOW + "ENTER THE INDEX OF THE CARD YOU WANT TO PLAY" + Print.GREEN + " (1 TO " + (currentPlayer.getHand().size()) + ")" + Print.YELLOW + " :: " + Print.DEFAULT);
            try {
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 1 || index > currentPlayer.getHand().size()) {
                    System.out.println(Print.RED + "INVALID CARD INDEX! PLEASE ENTER A NUMBER BETWEEN 1 AND " + currentPlayer.getHand().size() + " .. " + Print.DEFAULT);
                    continue;
                }

                System.out.println(Print.YELLOW + "\nYOU HAVE CHOSEN TO PLAY ::" + Print.DEFAULT);
                System.out.println("\n" + currentPlayer.getHand().get(index - 1));

                while (true) {
                    System.out.print(Print.YELLOW + "\nPLEASE ENTER" + Print.RED + " [Y] " + Print.YELLOW + "TO CONFIRM OR" +
                                                                       Print.RED + " [N] " + Print.YELLOW + "TO CHOOSE AGAIN :: " + Print.DEFAULT);
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("Y")) {
                        return currentPlayer.getHand().remove(index - 1);
                    } else if (confirm.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println(Print.RED + "INVALID INPUT!" + Print.DEFAULT);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(Print.RED + "INVALID INPUT, PLEASE ENTER A NUMBER .." + Print.DEFAULT);
                continue;
            }
        }

    }

    public void playTurn(Card playedCard, ParadeLine paradeLine, Deck deck, boolean isLastRound) {
        Player currentPlayer = getCurrentPlayer();
        GameView.clearScreen();
        System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.YELLOW + " PLAYS CARD ::\n\n" + Print.DEFAULT + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard, paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.YELLOW + " TAKES THE FOLLOWING CARDS FROM THE PARADE ..\n" + Print.DEFAULT);
            System.out.println(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.DEFAULT + " TAKES NO CARD FROM THE PARADE ..");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.DEFAULT + " DRAWS A CARD ..\n");
            } else {
                System.out.println(Print.RED + "DECK IS EMPTY, NO CARD DRAWN ..\n" + Print.DEFAULT);
            }
            if (!currentPlayer.isBot()) {
                System.out.println(Print.CYAN + currentPlayer.getPlayerName() + "'S" + Print.YELLOW + " CURRENT HAND :: \n" + Print.DEFAULT);
                System.out.println(GameUtils.handToString(currentPlayer.getHand()));
                System.out.println();
            }
        } else {
            System.out.println("\n" + Print.CYAN + currentPlayer.getPlayerName() + Print.DEFAULT + " DOES NOT DRAW A CARD IN THE LAST ROUND ..\n");
        }
        GameView.displayPlayerCollections(currentPlayer);
    }
}