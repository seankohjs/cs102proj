import java.util.*;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        Suit[] suits = Suit.values();
        for (Suit suit : suits) {
            for (int value = 0; value <= 10; value++) {
                cards.add(new Card(suit, value));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int getCardCount() {
        return cards.size();
    }
}