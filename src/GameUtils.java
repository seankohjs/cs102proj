import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class GameUtils {

    public enum Color {
        RED, BLUE, GREEN, ORANGE, PURPLE, GREY
    }

    public static final String BOLD = "\u001B[1m";
    public static final String RESET = "\u001B[0m";


    // Converts a list of cards (hand) into a formatted string with indexes.
    public static String handToString(List<Card> hand) {
        if (hand.isEmpty()) return "[]";

        // Ensure terminal is "primed" for ANSI sequences
        StringBuilder sb = new StringBuilder("\u001B[0m\r");
    
        // Determine max lines in the card representation
        int maxLines = hand.get(0).toString().split("\n").length;
    
        // Create a multi-line string representation
        StringBuilder[] lines = new StringBuilder[maxLines];
        for (int i = 0; i < maxLines; i++) {
            lines[i] = new StringBuilder();
        }
    
        // Populate lines with card strings
        for (int i = 0; i < hand.size(); i++) {
            String[] cardLines = hand.get(i).toString().split("\n");
    
            for (int j = 0; j < maxLines; j++) {
                lines[j].append(cardLines[j]).append(Card.RESET); // Ensure reset after each card
    
                if (i < hand.size() - 1) {
                    lines[j].append("  "); // Space between cards
                }
            }
        }
    
        // Combine lines into final output
        for (StringBuilder line : lines) {
            sb.append(line).append("\n");
        }
    
        return sb.toString().trim();
    }
    


    // Converts a list of cards into a formatted string without indexes.
    public static String cardsToString(List<Card> cards) {
        if (cards.isEmpty()) return "";
    
        // Ensure terminal is "primed" for ANSI sequences
        StringBuilder sb = new StringBuilder("\u001B[0m\r");
    
        // Determine max lines in the card representation
        int maxLines = cards.get(0).toString().split("\n").length;
        
        // Create a multi-line string representation
        StringBuilder[] lines = new StringBuilder[maxLines];
        for (int i = 0; i < maxLines; i++) {
            lines[i] = new StringBuilder();
        }
    
        // Populate lines
        for (int i = 0; i < cards.size(); i++) {
            String[] cardLines = cards.get(i).toString().split("\n");
            for (int j = 0; j < maxLines; j++) {
                lines[j].append(cardLines[j]);
                if (i < cards.size() - 1) {
                    lines[j].append("  "); // Space between cards
                }
            }
        }
    
        // Combine lines
        for (StringBuilder line : lines) {
            sb.append(line).append("\n");
        }
    
        return sb.toString().trim();
    }
    


    // Prints a player's collection in a formatted manner.
    public static String formatPlayerCollection(Map<Suit, List<Card>> collectionsBySuit) {
        StringBuilder sb = new StringBuilder("\u001B[0m\r");
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