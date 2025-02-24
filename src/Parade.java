import java.util.ArrayList;

/*
 * NOTE: This code is a demo
 * Need to wait until actual card class is done
 */


public class Parade {
    // Store current cards in parade
    private ArrayList<Card> paradeCards;

    // Store removed cards from parade
    private ArrayList<Card> removedCards;

    // Constructor methods
    // Construct inital parade

    public Parade(Deck deck) {
        paradeCards = new ArrayList<Card>();
        removedCards = new ArrayList<Card>();

        for(int i = 0; i < 6; i++) {
            paradeCards.add(deck.drawCard());
        }
    }

    // Instance methods
    // Put a card in the end of parade and collect removedCards
    public ArrayList<Card> playCard(Card n) {
        removedCards.clear();
        paradeCards.add(n);
        collectCards();
        return removedCards;
    }
    
    // Remove cards from parade based on card played
    public void collectCards() {
        Card end = paradeCards.getLast();
        for(int i = 0; i < paradeCards.size()-1-end.getValue(); i++) {
            Card currentCard = paradeCards.get(i);
            if(currentCard.getValue() <= end.getValue() || currentCard.getSuit().equals(end.getSuit())) {
                removedCards.add(currentCard);
                paradeCards.remove(i--);
            }
        }
    }

    // Prints current parade
    public String toString() {
        String stringParade = "";
        for(Card card : paradeCards) {
            stringParade += card + " ";
        }
        stringParade = stringParade.strip();

        return "Current Parade[" + stringParade + "]";
    }
}