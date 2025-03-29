package refactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;
    private static final int MAX_RANK = 10; // Rank 0 to 10

    public Deck() {
        this.cards = new ArrayList<>();
        initializeDeck(); // Initialize when created
    }

    // Fills the deck with standard Parade cards (6 suits, ranks 0-10)
    private void initializeDeck() {
        cards.clear(); // Ensure deck is empty before initializing
        for (Suit suit : Suit.getAllSuits()) {
            for (int rank = 0; rank <= MAX_RANK; rank++) {
                cards.add(new Card(suit, rank));
            }
        }
        // Total cards = 6 suits * 11 ranks = 66 cards
    }

    public void shuffle() {
        Collections.shuffle(cards, new Random()); // Use default Random or provide seed if needed
    }

    public Card dealCard() {
        if (isEmpty()) {
            return null; // Or throw an exception like NoSuchElementException
        }
        return cards.remove(cards.size() - 1); // Deal from the "top" (end of list)
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    // Optional: Useful for debugging or verification
    @Override
    public String toString() {
        return "Deck [" + cards.size() + " cards remaining]";
    }
}