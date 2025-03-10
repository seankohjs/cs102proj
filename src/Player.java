import java.util.*;

public class Player {
    private String playerName;
    private List<Card> hand;
    private List<Card> collectedCards;

    public int calculateScore() { // <--- Add this method back into Player class
        int score = 0;
        Map<Suit, Integer> suitCounts = new HashMap<>();
        for (Card card : collectedCards) {
            Suit suit = card.getSuit();
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }
        // For now, just sum up face values in Player class calculateScore()
        // Majority logic is handled in Game class
        for (Card card : collectedCards) {
            score += card.getValue();
        }
        return score;
    }

    public Player(String playerName) {
        this.playerName = playerName;
        this.hand = new ArrayList<>();
        this.collectedCards = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getCollectedCards() {
        return collectedCards;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    public void addCollectedCards(List<Card> cards) {
        collectedCards.addAll(cards);
    }

    public void discardHandToSize(int maxSize) {
        // In PvP, player chooses discard, in simple AI, discard first
        while (hand.size() > maxSize) {
            hand.remove(0); // Simple discard strategy for example
        }
    }

    public int getCardCountInSuit(Suit suit) {
        int count = 0;
        for (Card card : collectedCards) {
            if (card.getSuit() == suit) {
                count++;
            }
        }
        return count;
    }

    public void addHandToCollection() { // For scoring, add remaining hand cards
        collectedCards.addAll(hand);
        hand.clear(); // Hand is now empty after scoring
    }

    public void discardTwoHandCards() { // Discard 2 cards after game end, player chooses ideally
        while (hand.size() > 2) {
            hand.remove(0); // Simple discard strategy for example - improve in UI version
        }
    }

}
