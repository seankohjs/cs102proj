import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class GameController {
    private Deck deck;
    private ParadeLine paradeLine;
    private List<Player> players;
    private TurnManager turnManager;
    private RemovalStrategy removalStrategy;
    private ScoreCalculator scoreCalculator;
    private boolean isLastRound;
    private boolean deckExhaustedLastRound;
    private Scanner scanner;
    private int extraTurnCount = 0;

    private void interactiveDiscardTwoCards(Player player) {
        while (player.getHand().size() > 2) {
            System.out.println("\n" + player.getPlayerName() + ", choose a card to discard from hand: "
                    + GameUtils.handToString(player.getHand()));
            int index = -1;
            while (true) {
                System.out.print("Enter the index of the card you want to discard: ");
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                    scanner.nextLine(); // consume newline
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

    public GameController(List<String> playerNames) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        this.turnManager = new TurnManager(players);
        this.removalStrategy = new RemovalStrategy();
        this.scoreCalculator = new ScoreCalculator();
        this.isLastRound = false;
        this.deckExhaustedLastRound = false;
        this.scanner = new Scanner(System.in);

        // Deal initial parade line (6 cards)
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

    public void startGame() {
        // Use an infinite loop and break out when the last round cycle is complete.
        while (true) {
            clearScreen(); // Clear the screen at the beginning of each turn.

            Player currentPlayer = turnManager.getCurrentPlayer();
            System.out.println("\n=======================================");
            System.out.println("It's " + currentPlayer.getPlayerName() + "'s turn!");
            printGameState();

            // Print current player's collection (already nicely formatted).
            printPlayerCollections(currentPlayer);
            // Print a separate section for the other players' collections.
            printOtherPlayersCollections(currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = getPlayerCardChoice(currentPlayer);
                playTurn(cardToPlay);
            } else {
                System.out.println(currentPlayer.getPlayerName() + " has no cards to play! Passing turn.");
            }

            // After turn logic (prompting, advancing turn, etc.) goes here...
            promptForNextTurn(currentPlayer);

            if (!isLastRound) {
                checkGameEndConditions();
                turnManager.nextPlayer();
            } else {
                turnManager.nextPlayer();
                extraTurnCount++;
                if (extraTurnCount >= players.size()) {
                    break; // End game after every player has played one extra turn.
                }
            }
        }
        endGame();
        scanner.close();
    }

    private void checkGameEndConditions() {
        if (!isLastRound) {
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true);
            } else if (deck.isEmpty()) {
                startLastRound(false);
                deckExhaustedLastRound = true;
            }
        }
    }

    private boolean hasAnyoneCollectedSixColors() {
        for (Player player : players) {
            Set<Suit> collectedSuits = new HashSet<>();
            for (Card card : player.getCollectedCards()) {
                collectedSuits.add(card.getSuit());
            }
            if (collectedSuits.size() >= 6) {
                return true;
            }
        }
        return false;
    }

    public void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        System.out.println(currentPlayer.getPlayerName() + " plays " + playedCard);
        paradeLine.addCardToLine(playedCard);

        RemovalStrategy.RemovalChoice removalChoice = removalStrategy.determineRemovalChoice(playedCard,
                paradeLine.getParadeLineCards());
        List<Card> cardsToRemove = new ArrayList<>();

        // Handle removal for same suit
        if (!removalChoice.sameSuitCandidates.isEmpty()) {
            Card choice = getPlayerRemovalChoice(currentPlayer, removalChoice.sameSuitCandidates, "same suit");
            if (choice != null) {
                cardsToRemove.add(choice);
            }
        }

        // Handle removal for lower value (after excluding any already chosen)
        if (!removalChoice.lowerValueCandidates.isEmpty()) {
            List<Card> remainingCandidates = new ArrayList<>(removalChoice.lowerValueCandidates);
            remainingCandidates.removeAll(cardsToRemove);
            if (!remainingCandidates.isEmpty()) {
                Card choice = getPlayerRemovalChoice(currentPlayer, remainingCandidates, "lower value");
                if (choice != null) {
                    cardsToRemove.add(choice);
                }
            }
        }

        if (!cardsToRemove.isEmpty()) {
            System.out.println(
                    currentPlayer.getPlayerName() + " takes " + GameUtils.cardsToString(cardsToRemove)
                            + " from the parade.");
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            System.out.println(currentPlayer.getPlayerName() + " takes no card from the parade.");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                System.out.println(currentPlayer.getPlayerName() + " draws a card.");
            } else {
                System.out.println("Deck is empty, no card drawn.");
            }
            System.out.println(
                    "\n" + currentPlayer.getPlayerName() + "'s Current Hand: "
                            + GameUtils.handToString(currentPlayer.getHand()));
        } else {
            System.out.println(currentPlayer.getPlayerName() + " does not draw a card in last round.");
        }

        printPlayerCollections(currentPlayer);
    }

    private void printOtherPlayersCollections(Player currentPlayer) {
        System.out.println("\n--- Other Players' Collections ---");
        for (Player player : players) {
            // Skip printing for the current player.
            if (player != currentPlayer) {
                System.out.println(player.getPlayerName() + "'s Collected Cards:");
                // Use a local copy of the logic from printPlayerCollections,
                // but without an extra header.
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
                System.out.println(); // extra space between players.
            }
        }
    }

    private void endGame() {
        System.out.println("\nGame Over!");
        System.out.println("\n--- Discarding 2 Hand Cards for Scoring ---");

        for (Player player : players) {
            // Clear the screen before each player's discard phase.
            clearScreen();

            System.out.println("\n" + player.getPlayerName() + ", it's time to discard 2 cards from your hand.");

            // Print the player's current collection for reference.
            System.out.println(player.getPlayerName() + "'s Current Collection:");
            printPlayerCollections(player);

            // Prompt the player to discard until only 2 cards remain in their hand.
            interactiveDiscardTwoCards(player);
            System.out
                    .println(player.getPlayerName() + " discards hand to: " + GameUtils.handToString(player.getHand()));

            // Add remaining hand cards to the collection for scoring.
            player.addHandToCollection();

            // Print the updated game state for the player.
            System.out.println("\nUpdated Game State for " + player.getPlayerName() + ":");
            printPlayerCollections(player);

            // Prompt the player to move on.
            System.out.print("\nPress 'y' to continue: ");
            String input = scanner.nextLine();
            while (!input.equalsIgnoreCase("y")) {
                System.out.print("Please type 'y' to continue: ");
                input = scanner.nextLine();
            }

            // Countdown for 2 seconds before proceeding.
            try {
                for (int i = 2; i > 0; i--) {
                    System.out.println("Next player in " + i + " second" + (i > 1 ? "s" : "") + "...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // If sleep is interrupted, ignore and continue.
            }
        }

        // After all players have discarded, show all players' collections before
        // scoring.
        System.out.println("\n--- Final Player Collections Before Scoring ---");
        for (Player player : players) {
            printPlayerCollections(player);
        }

        calculateFinalScores();
        determineWinner();
    }

    private void calculateFinalScores() {
        System.out.println("\n--- Scores ---");
        Map<Suit, Player> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        for (Player player : players) {
            int score = scoreCalculator.calculatePlayerFinalScore(player, suitMajorities);
            System.out.println(player.getPlayerName() + " final score: " + score);
        }
    }

    private void promptForNextTurn(Player player) {
        // Print updated game state for the player

        // Prompt the player to continue
        System.out.print("\nPress 'y' to continue: ");
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("y")) {
            System.out.print("Please type 'y' to continue: ");
            input = scanner.nextLine();
        }

        // Countdown of 2 seconds before next turn
        try {
            for (int i = 2; i > 0; i--) {
                System.out.println("Next turn in " + i + " second" + (i > 1 ? "s" : "") + "...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Interrupted? Just continue.
        }
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Unix/Linux/Mac OS
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback: print several new lines if the above doesn't work.
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private void determineWinner() {
        Player winner = scoreCalculator.determineWinner(players);
        Map<Suit, Player> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        int winnerScore = scoreCalculator.calculatePlayerFinalScore(winner, suitMajorities);
        System.out.println("\nWinner: " + winner.getPlayerName() + " with score " + winnerScore + "!");
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0; // Reset the counter for extra turns.
        System.out.println("\n--- Last Round Started! ---");
        if (sixColors) {
            System.out.println("Triggered by a player collecting 6 colors.");
        } else {
            System.out.println("Triggered by deck exhaustion.");
        }
    }

    private Card getPlayerCardChoice(Player currentPlayer) {
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

    private Card getPlayerRemovalChoice(Player currentPlayer, List<Card> candidates, String type) {
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

    private void printGameState() {
        if (isLastRound) {
            System.out.println("**** LAST ROUND ****");
        }
        System.out.println("\n--- Game State ---");
        System.out.println("Parade Line: " + GameUtils.cardsToString(paradeLine.getParadeLineCards()));
        System.out.println("Cards in Deck: " + deck.getCardCount());
    }

    private void printPlayerCollections(Player player) {
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
                System.out.println(suit.toString().charAt(0) + suit.toString().substring(1).toLowerCase() + ": "
                        + GameUtils.cardsToString(cards));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("None collected yet.");
        }
    }
}
