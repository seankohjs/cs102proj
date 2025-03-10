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

    private boolean isGameOver() {
        if (isLastRound) {
            return turnManager.getCurrentPlayerIndex() == 0;
        }
        return false;
    }

    public void startGame() {
        while (!isGameOver()) {
            Player currentPlayer = turnManager.getCurrentPlayer();
            System.out.println("\n=======================================");
            System.out.println("It's " + currentPlayer.getPlayerName() + "'s turn!");
            printGameState();
            printPlayerCollections(currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = getPlayerCardChoice(currentPlayer);
                playTurn(cardToPlay);
            } else {
                System.out.println(currentPlayer.getPlayerName() + " has no cards to play! Passing turn.");
            }

            if (!isLastRound) {
                checkGameEndConditions();
            }

            if (isLastRound) {
                turnManager.nextPlayer();
                if (turnManager.getCurrentPlayerIndex() == 0) {
                    break; // End game after a full cycle in the last round
                }
            } else {
                turnManager.nextPlayer();
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
                    currentPlayer.getPlayerName() + " takes " + cardsToString(cardsToRemove) + " from the parade.");
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
            currentPlayer.discardHandToSize(7);
        } else {
            System.out.println(currentPlayer.getPlayerName() + " does not draw a card in last round.");
        }

        printPlayerCollections(currentPlayer);
    }

    private void endGame() {
        System.out.println("\nGame Over!");
        System.out.println("\n--- Player Collections Before Scoring ---");
        for (Player player : players) {
            printPlayerCollections(player);
        }
        if (!deckExhaustedLastRound) {
            System.out.println("\n--- Discarding 2 Hand Cards for Scoring ---");
            for (Player player : players) {
                System.out.println("\n" + player.getPlayerName() + ", choose 2 cards to discard from hand: "
                        + handToString(player.getHand()));
                player.discardTwoHandCards();
                System.out.println(player.getPlayerName() + " discards hand to: " + handToString(player.getHand()));
                player.addHandToCollection();
            }
        } else {
            System.out.println("\n--- Hand cards are NOT discarded because game ended due to deck exhaustion ---");
            for (Player player : players) {
                player.addHandToCollection();
            }
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

    private void determineWinner() {
        Player winner = scoreCalculator.determineWinner(players);
        Map<Suit, Player> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        int winnerScore = scoreCalculator.calculatePlayerFinalScore(winner, suitMajorities);
        System.out.println("\nWinner: " + winner.getPlayerName() + " with score " + winnerScore + "!");
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        System.out.println("\n--- Last Round Started! ---");
        if (sixColors) {
            System.out.println("Triggered by a player collecting 6 colors.");
        } else {
            System.out.println("Triggered by deck exhaustion.");
        }
        // Optionally, adjust turn order here if needed.
    }

    private Card getPlayerCardChoice(Player currentPlayer) {
        while (true) {
            System.out.println("\nYour hand: " + handToString(currentPlayer.getHand()));
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
        System.out.println("\nEligible cards to take (" + type + "): " + handToString(candidates));
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

    private String handToString(List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < hand.size(); i++) {
            sb.append(i).append(":").append(hand.get(i).toString());
            if (i < hand.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String cardsToString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Card card : cards) {
            sb.append(card.toString()).append(", ");
        }
        if (cards.size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }

    private void printGameState() {
        System.out.println("\n--- Game State ---");
        System.out.println("Parade Line: " + cardsToString(paradeLine.getParadeLineCards()));
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
                        + cardsToString(cards));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("None collected yet.");
        }
    }
}
