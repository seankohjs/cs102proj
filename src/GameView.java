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
        System.out.println();
        System.out.println("■■■■■");
        System.out.println("It's " + currentPlayer.getPlayerName() + "'s Turn!");
    }

    // Displays the current game state.
    public void displayGameState(Deck deck, ParadeLine paradeLine, boolean isLastRound) {
        if (isLastRound) {
            System.out.println("***** LAST ROUND *****");
        }
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■ GAME STATE ■■■■■" + Print.RESET);
        System.out.println(Print.BOLD + "Parade Line:" + Print.RESET);
        System.out.println(GameUtils.cardsToString(paradeLine.getParadeLineCards()));
        System.out.println("Cards in Deck: " + deck.getCardCount());
    }

    // Displays a message (a simple wrapper).
    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    // Displays the current player's collection.
    public void displayPlayerCollections(Player player) {
        System.out.println();
        System.out.println("■■■■■ " + player.getPlayerName() + "'s Collected Cards ■■■■■");
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
                System.out.println(GameUtils.cardsToString(cards));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("No Cards Collected Yet.");
        }
    }

    // Displays other players' collections.
    public void displayOtherPlayersCollections(List<Player> players, Player currentPlayer) {
        System.out.println();
        System.out.println("■■■■■ Other Players' Collections ■■■■■");
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
        System.out.print("Enter [Y] to Continue: ");
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("y")) {
            System.out.print("Please Enter [Y] to Continue: ");
            input = scanner.nextLine();
        }
        try {
            for (int i = 2; i > 0; i--) {
                System.out.println("Next Turn In " + i + " Second" + (i > 1 ? "s" : "") + "...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Ignore interruption.
        }
    }

    // Interactively prompts the user to discard cards until only 2 remain.
    public void interactiveDiscardTwoCards(Player player) {
        while (player.getHand().size() > 2) {
            System.out.println("\n" + player.getPlayerName() + ", Choose a Card to" + Print.RED + " Discard " + Print.RESET + "from your Hand: "
                    + GameUtils.handToString(player.getHand()));
            int index = -1;
            while (true) {
                System.out.print("Enter the INDEX of the Card you Want to" + Print.RED + " Discard " + Print.RESET);
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                    scanner.nextLine();
                    if (index >= 0 && index < player.getHand().size()) {
                        break;
                    } else {
                        System.out.println("INVALID INDEX, Try Again.");
                    }
                } else {
                    System.out.println("INVALID INPUT, Please Enter a Number.");
                    scanner.nextLine();
                }
            }
            Card discarded = player.getHand().remove(index);
            System.out.println("Discarded: " + discarded);
        }
    }

    // Prompts the player for a card choice from their hand.
    public Card getPlayerCardChoice(Player currentPlayer) {
        while (true) {
            System.out.println("\nYour Hand: ");
            System.out.println(GameUtils.handToString(currentPlayer.getHand()));
            System.out.println();
            System.out.print("Enter the Index of the Card You Want to Play (0 to "
                    + (currentPlayer.getHand().size() - 1) + "): ");
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                if (index >= 0 && index < currentPlayer.getHand().size()) {
                    return currentPlayer.getHand().remove(index);
                } else {
                    System.out.println("INVALID CARD INDEX. Please Try Again.");
                }
            } else {
                System.out.println("INVALID INPUT. Please Enter a Number.");
                scanner.nextLine();
            }
        }
    }

    // Prompts the player to choose a card from a list of candidates.
    public Card getPlayerRemovalChoice(Player currentPlayer, List<Card> candidates, String type) {
        System.out.println("\nEligible Cards to Take (" + type + "):");
        System.out.println(GameUtils.handToString(candidates));
        if (candidates.isEmpty()) {
            return null;
        }
        while (true) {
            System.out.print("Enter the Index of the Card You Want to Take (0 to " + (candidates.size() - 1)
                    + "), or -1 to Take NONE: ");
            if (scanner.hasNextInt()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                if (index == -1) {
                    return null;
                } else if (index >= 0 && index < candidates.size()) {
                    return candidates.get(index);
                } else {
                    System.out.println("INVALID INDEX. Try Again.");
                }
            } else {
                System.out.println("INVALID INPUT. Please Enter a Number.");
                scanner.nextLine();
            }
        }
    }
}