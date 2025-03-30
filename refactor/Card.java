import java.util.Objects;

public final class Card {
    private final Suit suit;
    private final int rank; // 0 through 10

    public Card(Suit suit, int rank) {
        if (rank < 0 || rank > 10) {
            throw new IllegalArgumentException("Rank must be between 0 and 10.");
        }
        if (suit == null) {
            throw new IllegalArgumentException("Suit cannot be null.");
        }
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        // Simple representation, e.g., "SUIT_0 5"
        return suit + " " + rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}