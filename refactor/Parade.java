import java.util.ArrayList;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Objects; // Import Objects for removeAll null check

public class Parade {
    private final List<Card> cards;

    public Parade() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
         if (card != null) {
            this.cards.add(card);
         }
    }

    public Card getCardAtIndex(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " for parade of size " + cards.size());
        }
        return cards.get(index);
    }

    // Removes the specified cards from the parade
    // Assumes Card has correctly implemented equals() and hashCode()
    public void removeCards(List<Card> cardsToRemove) {
        Objects.requireNonNull(cardsToRemove, "List of cards to remove cannot be null");
        this.cards.removeAll(cardsToRemove);
    }

    // Return unmodifiable view for safety
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int getSize() {
        return cards.size();
    }

    @Override
    public String toString() {
        // Provide a slightly more informative default string
        StringBuilder sb = new StringBuilder("Parade [");
        if (!cards.isEmpty()) {
            for (int i = 0; i < cards.size(); i++) {
                sb.append(cards.get(i));
                if (i < cards.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}