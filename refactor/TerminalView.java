import java.util.List;
import java.util.Scanner;
// import java.util.InputMismatchException;
// import java.util.Collections; // For Collections.emptyList()

public class TerminalView {
    private final Scanner scanner;

    public TerminalView() {
        // Use try-with-resources or ensure scanner is closed elsewhere (e.g., in Game)
        this.scanner = new Scanner(System.in);
    }

    public void displayWelcome() {
        System.out.println("\n=============================");
        System.out.println("   Welcome to Parade!");
        System.out.println("=============================");
    }

    public int getNumberOfPlayers() {
        int numPlayers = 0;
        final int MIN_PLAYERS = 2;
        final int MAX_PLAYERS = 6; // Adjust if needed

        while (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
            System.out.printf("Enter the number of players (%d-%d): ", MIN_PLAYERS, MAX_PLAYERS);
            try {
                String line = scanner.nextLine(); // Read whole line
                numPlayers = Integer.parseInt(line.trim()); // Try parsing
                if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
                    System.out.printf("Invalid number. Please enter between %d and %d.\n", MIN_PLAYERS, MAX_PLAYERS);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                // No need to consume extra input since we read the whole line
                numPlayers = 0; // Reset to loop again
            }
        }
        return numPlayers;
    }

    public String getPlayerName(int playerNumber) {
        String name = "";
        while (name == null || name.trim().isEmpty()) {
            System.out.printf("Enter name for Player %d: ", playerNumber);
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Player name cannot be empty.");
            }
        }
        return name;
    }

    public void displayGameState(Parade paradeLine, List<Player> players, Player currentPlayer) {
        System.out.println("\n-----------------------------");
        System.out.println("Current Parade (" + paradeLine.getSize() + " cards):");
        System.out.println(paradeLine.getCards()); // Uses Parade's getCards() -> unmodifiable list

        System.out.println("\nPlayers' Captured Cards:");
        for (Player p : players) {
            // Use the getter that returns an unmodifiable list
            List<Card> captured = p.getCapturedCards();
            System.out.printf("  %s (%d cards): %s\n",
                              p.getName(),
                              captured.size(),
                              captured.isEmpty() ? "None" : captured.toString());
        }

        System.out.println("\n--- " + currentPlayer.getName() + "'s Turn ---");
        System.out.println("Your Hand:");
        displayHand(currentPlayer.getHand()); // Uses Player's getHand() -> unmodifiable list
    }

    // Displays hand with indices
    public void displayHand(List<Card> hand) {
        if (hand.isEmpty()) {
            System.out.println("  Hand is empty!");
            return;
        }
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("  %d: %s\n", i, hand.get(i));
        }
    }

    // Gets the player's card choice index from their hand
    public int getCardChoice(Player player) {
        List<Card> hand = player.getHand(); // Get unmodifiable hand
        if (hand.isEmpty()) {
             System.out.println(player.getName() + "'s hand is empty! Cannot choose a card.");
             // This case should ideally be handled by game logic before calling this
             return -1; // Indicate error or inability to choose
        }

        int choice = -1;
        int handSize = hand.size();
        while (choice < 0 || choice >= handSize) {
            System.out.printf("Choose a card to play (0-%d): ", handSize - 1);
            try {
                String line = scanner.nextLine();
                choice = Integer.parseInt(line.trim());
                if (choice < 0 || choice >= handSize) {
                    System.out.println("Invalid choice. Index out of range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1; // Reset choice to loop again
            }
        }
        return choice;
    }

    // Gets the player's choice for the final card to keep
    public int getFinalCardChoice(Player player) {
        List<Card> hand = player.getHand(); // Get unmodifiable hand
        if (hand.isEmpty()) {
             // This shouldn't happen if called correctly in Game, but good to check
             System.out.println(player.getName() + " has no cards left to choose from.");
             return -1;
        }
         if (hand.size() == 1) {
             System.out.println(player.getName() + " has only 1 card left, it will be kept automatically: " + hand.get(0));
             return 0; // Only one choice
         }


        int choice = -1;
        int handSize = hand.size();
         System.out.println("\n" + player.getName() + ", choose one card from your hand to keep for scoring:");
         displayHand(hand);

        while (choice < 0 || choice >= handSize) {
            System.out.printf("Enter the index of the card to keep (0-%d): ", handSize - 1);
            try {
                String line = scanner.nextLine();
                choice = Integer.parseInt(line.trim());
                if (choice < 0 || choice >= handSize) {
                    System.out.println("Invalid choice. Index out of range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1; // Reset choice to loop again
            }
        }
        System.out.println(player.getName() + " chose to keep: " + hand.get(choice));
        return choice;
    }


    public void displayPlayedCard(Player player, Card card) {
        System.out.printf("%s played: %s\n", player.getName(), card);
    }

     public void displayCapturedCardsInfo(Player player, List<Card> captured) {
         // Check if captured is null or empty defensively
         if (captured == null || captured.isEmpty()) {
             System.out.printf("%s captured no cards.\n", player.getName());
         } else {
            System.out.printf("%s captured: %s\n", player.getName(), captured);
        }
    }

    public void displayDrawCard(Player player, Card card) {
         if (card != null) {
            // Optionally show the drawn card? Rules usually don't require this.
            // System.out.printf("%s drew: %s\n", player.getName(), card);
            System.out.printf("%s drew a card.\n", player.getName());
         } else {
             System.out.printf("%s could not draw a card (deck empty).\n", player.getName());
         }
    }

    public void displayTurnEnd(Player player) {
         // System.out.println("--- End of " + player.getName() + "'s Turn ---"); // Optional separator
         System.out.println(); // Add a blank line for spacing
    }


    public void displayFinalScores(List<Player> players) {
        System.out.println("\n=============================");
        System.out.println("        FINAL SCORES");
        System.out.println("=============================");
        // Assumes players list is sorted by score (lowest first) by the Game logic
        for (Player p : players) {
            System.out.printf("%-15s: %d points\n", p.getName(), p.getScore());
            List<Card> captured = p.getCapturedCards();
            System.out.printf("  Captured (%d cards): %s\n", captured.size(), captured.isEmpty() ? "None" : captured.toString());
        }
        System.out.println("=============================");
    }

     public void displayWinner(Player winner) {
        System.out.printf("\n%s wins the game with the lowest score!\n\n", winner.getName());
     }

     public void displayDraw(List<Player> winners) {
         System.out.print("\nIt's a draw between: ");
         for(int i=0; i<winners.size(); i++) {
             System.out.print(winners.get(i).getName());
             if (i < winners.size() - 1) System.out.print(", ");
         }
         System.out.println("! (Lowest scores and same number of captured cards)\n");
     }

     public void displayMessage(String message) {
         System.out.println(message);
     }

    // Method to be called when the game ends to release the scanner resource
    public void close() {
        System.out.println("Closing input scanner.");
        scanner.close();
    }
}