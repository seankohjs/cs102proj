import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    //variables
    private ArrayList<Card> deck;
    private static final String[] COLORS = {"Red", "Blue", "Purple", "Green", "Grey", "Orange"};
    private static final int[] VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    //constructor
    public Deck() {
        this.deck = new ArrayList<Card>();
        for (String color:COLORS) {
            for (int value:VALUES) {
                deck.add(new Card(color, value));
            }
        }
        Collections.shuffle(deck);
    }
    //methods
    public Card getTopCard() {
        return deck.remove(0);
    }
}
