import java.util.*;

public class Player {
    private String playerName;
    private List<Card> hand;
    private List<Card> collectedCards;

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


    public void addCollectedCards(List<Card> cards) {
        collectedCards.addAll(cards);
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


}
