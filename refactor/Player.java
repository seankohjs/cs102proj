package refactor;

import java.util.ArrayList;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Set;

public class Player {
    private final String name;
    private final List<Card> hand;
    private final List<Card> capturedCards;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.capturedCards = new ArrayList<>();
        this.score = 0;
    }

    public void addCardToHand(Card card) {
        if (card != null) {
            hand.add(card);
            // Optional: Sort hand for easier display/selection
            // hand.sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getRank));
        }
    }

    public Card playCard(int index) {
        if (index < 0 || index >= hand.size()) {
            // Consider throwing a custom exception or returning null if handled gracefully elsewhere
            throw new IllegalArgumentException("Invalid card index ("+ index +") for player " + name + "'s hand of size " + hand.size());
        }
        return hand.remove(index);
    }

    public void addCapturedCard(Card card) {
         if (card != null) {
             capturedCards.add(card);
         }
    }

    // Calculates the final score based on captured cards and majority suits
    // Called once by Game at the end.
    public void calculateFinalScore(Set<Suit> majoritySuits) {
        int calculatedScore = 0;
        for (Card card : capturedCards) {
            if (majoritySuits.contains(card.getSuit())) {
                calculatedScore += 1; // Majority suits count as 1 point
            } else {
                calculatedScore += card.getRank(); // Non-majority suits count as their rank
            }
        }
        this.score = calculatedScore;
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    // Return unmodifiable view to prevent external modification
    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public List<Card> getCapturedCards() {
        return Collections.unmodifiableList(capturedCards);
    }

    public int getScore() {
        return score;
    }

    public boolean hasEmptyHand() {
        return hand.isEmpty();
    }


    @Override
    public String toString() {
        return name; // Simple representation
    }
}