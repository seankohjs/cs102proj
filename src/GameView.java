import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

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
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    // Displays a header for the current turn.
    public void displayTurnHeader(Player currentPlayer) {
        System.out.println("\n=======================================");
        System.out.println("It's " + currentPlayer.getPlayerName() + "'s turn!");
    }

    // Displays the current game state.
    public void displayGameState(Deck deck, ParadeLine paradeLine, boolean isLastRound) {
        if (isLastRound) {
            System.out.println("**** LAST ROUND ****");
        }
        System.out.println("\n--- Game State ---");
        System.out.println("Parade Line: " + GameUtils.cardsToString(paradeLine.getParadeLineCards()));
        System.out.println("Cards in Deck: " + deck.getCardCount());
    }

    // Displays a message (a simple wrapper).
    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    // Displays the current player's collection.
    public void displayPlayerCollections(Player player) {
        System.out.println("\n--- " + player.getPlayerName() + "'s Collected Cards ---");
        Map<Suit, List<Card>> collectionsBySuit = new HashMap<>();
        for (Suit suit : Suit.values()) {
            collectionsBySuit.put(suit, new ArrayList<>());
        }
        for (Card card : player.getCollectedCards()) {
            collectionsBySuit.get(card.getSuit()).add(card);
        }
        for (Suit suit : Suit.values()) {
            List<Card> cards = collectionsBySuit.get(suit);
            if (!cards.isEmpty()) {
                System.out.println(suit.toString().charAt(0)
                        + suit.toString().substring(1).toLowerCase() + ": "
                        + GameUtils.cardsToString(cards));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("None collected yet.");
        }
    }

    // Displays other players' collections.
    public void displayOtherPlayersCollections(List<Player> players, Player currentPlayer) {
        System.out.println("\n--- Other Players' Collections ---");
        for (Player player : players) {
            if (player != currentPlayer) {
                System.out.println(player.getPlayerName() + "'s Collected Cards:");
                Map<Suit, List<Card>> collectionsBySuit = new HashMap<>();
                for (Suit suit : Suit.values()) {
                    collectionsBySuit.put(suit, new ArrayList<>());
                }
                for (Card card : player.getCollectedCards()) {
                    collectionsBySuit.get(card.getSuit()).add(card);
                }
                for (Suit suit : Suit.values()) {
                    List<Card> cards = collectionsBySuit.get(suit);
                    if (!cards.isEmpty()) {
                        System.out.println("  " + suit.toString().charAt(0)
                                + suit.toString().substring(1).toLowerCase() + ": "
                                + GameUtils.cardsToString(cards));
                    }
                }
                System.out.println();
            }
        }
    }

    // Prompts the user to press 'y' to continue and shows a countdown.
    public void promptForNextTurn(Player player) {
        System.out.print("\nPress 'y' to continue: ");
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("y")) {
            System.out.print("Please type 'y' to continue: ");
            input = scanner.nextLine();
        }
        try {
            for (int i = 2; i > 0; i--) {
                System.out.println("Next turn in " + i + " second" + (i > 1 ? "s" : "") + "...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Ignore interruption.
        }
    }

    // Interactively prompts the user to discard cards until only 2 remain.
    public void interactiveDiscardTwoCards(Player player) {
        while (player.getHand().size() > 2) {
            System.out.println("\n" + player.getPlayerName() + ", choose a card to discard from your hand: "
                    + GameUtils.handToString(player.getHand()));
            int index = -1;
            while (true) {
                System.out.print("Enter the index of the card you want to discard: ");
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                    scanner.nextLine();
                    if (index >= 0 && index < player.getHand().size()) {
                        break;
                    } else {
                        System.out.println("Invalid index, try again.");
                    }
                } else {
                    System.out.println("Invalid input, please enter a number.");
                    scanner.nextLine();
                }
            }
            Card discarded = player.getHand().remove(index);
            System.out.println("Discarded: " + discarded);
        }
    }

    // Prompts the player for a card choice from their hand.
    public Card getPlayerCardChoice(Player currentPlayer) {
        if(currentPlayer instanceof Bot) {
            Bot e = (Bot)currentPlayer;
            return e.discard();
        }

        while (true) {
            System.out.println("\nYour hand: " + GameUtils.handToString(currentPlayer.getHand()));
            System.out.print("Enter the index of the card you want to play (0 to "
                    + (currentPlayer.getHand().size() - 1) + "): ");
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                if (index >= 0 && index < currentPlayer.getHand().size()) {
                    return currentPlayer.getHand().remove(index);
                } else {
                    System.out.println("Invalid card index. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // Prompts the player to choose a card from a list of candidates.
    public Card getPlayerRemovalChoice(Player currentPlayer, List<Card> candidates, String type) {
        System.out.println("\nEligible cards to take (" + type + "): " + GameUtils.handToString(candidates));
        if (candidates.isEmpty()) {
            return null;
        }
        while (true) {
            System.out.print("Enter the index of the card you want to take (0 to " + (candidates.size() - 1)
                    + "), or -1 to take none: ");
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                if (index == -1) {
                    return null;
                } else if (index >= 0 && index < candidates.size()) {
                    return candidates.get(index);
                } else {
                    System.out.println("Invalid index. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}
