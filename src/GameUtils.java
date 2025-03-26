import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class GameUtils {

    // Converts a list of cards (hand) into a formatted string with indexes.
    public static String handToString(List<Card> hand) {
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

    // Converts a list of cards into a formatted string without indexes.
    public static String cardsToString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i).toString());
            if (i < cards.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Prints a player's collection in a formatted manner.
    public static String formatPlayerCollection(Map<Suit, List<Card>> collectionsBySuit) {
        StringBuilder sb = new StringBuilder();
        for (Suit suit : Suit.values()) {
            List<Card> cards = collectionsBySuit.get(suit);
            if (cards != null && !cards.isEmpty()) {
                sb.append(suit.toString().charAt(0))
                        .append(suit.toString().substring(1).toLowerCase())
                        .append(": ")
                        .append(cardsToString(cards))
                        .append("\n");
            }
        }
        return sb.toString();
    }

    // A convenience method to build a player's collection as a map from a list of
    // cards.
    public static Map<Suit, List<Card>> buildCollectionMap(List<Card> collectedCards) {
        Map<Suit, List<Card>> collectionsBySuit = new HashMap<>();
        for (Suit suit : Suit.values()) {
            collectionsBySuit.put(suit, new ArrayList<>());
        }
        for (Card card : collectedCards) {
            collectionsBySuit.get(card.getSuit()).add(card);
        }
        return collectionsBySuit;
    }

    // Returns a formatted string representing a player's collection.
    public static String formatPlayerCollection(List<Card> collectedCards) {
        Map<Suit, List<Card>> collectionMap = buildCollectionMap(collectedCards);
        return formatPlayerCollection(collectionMap);
    }
}
