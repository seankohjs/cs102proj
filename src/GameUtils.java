import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class GameUtils {

    // Converts a list of cards (hand) into a formatted string with indexes.
    public static String handToString(List<Card> hand) {
    if (hand.isEmpty()) return "[]";

    // "Prime" the terminal for ANSI codes
    StringBuilder sb = new StringBuilder("\u001B[0m\r");
    
    // Determine the number of lines in a card representation (e.g., top, middle, bottom)
    int maxLines = hand.get(0).toString().split("\n").length;
    
    // Create a StringBuilder for the index header
    StringBuilder indexLine = new StringBuilder();
    
    // Create a StringBuilder array for each card line
    StringBuilder[] lines = new StringBuilder[maxLines];
    for (int i = 0; i < maxLines; i++) {
        lines[i] = new StringBuilder();
    }
    
    // Process each card in the hand
    for (int i = 0; i < hand.size(); i++) {
        Card card = hand.get(i);
        // Determine the card width: 10 if value==10, otherwise 9
        int cardWidth = 11;
        // Convert the index (starting at 1) to string
        String indexStr = String.valueOf(i + 1);
        // Compute left/right padding to center the index over the card width
        int leftPadding = (cardWidth - indexStr.length()) / 2;
        int rightPadding = cardWidth - indexStr.length() - leftPadding;
        
        // Append left spaces, index, then right spaces
        indexLine.append(" ".repeat(leftPadding - 1))
                 .append(Print.BOLD).append("[").append(indexStr).append("]").append(Print.RESET)
                 .append(" ".repeat(rightPadding - 1));
        
        // Add inter-card spacing (adjust if not the last card)
        if (i < hand.size() - 1) {
            indexLine.append("  ");
        }
        
        // Get the card's string representation split into lines
        String[] cardLines = card.toString().split("\n");
        for (int j = 0; j < maxLines; j++) {
            lines[j].append(cardLines[j]).append(Print.RESET);
            if (i < hand.size() - 1) {
                lines[j].append("  "); // space between cards
            }
        }
    }
    
    // Append the index line first, then the card lines
    sb.append(indexLine).append("\n");
    for (StringBuilder line : lines) {
        sb.append(line).append("\n");
    }
    
    return sb.toString().trim();
    }

    public static String cardsToString(List<Card> cards) {
        if (cards.isEmpty()) return "";
        final int CARDS_PER_ROW = 10;
        int numCards = cards.size();
        // Calculate the number of rows needed
        int numRows = (numCards + CARDS_PER_ROW - 1) / CARDS_PER_ROW;
        
        // Determine how many lines each card has (assuming they are uniform)
        int maxLines = cards.get(0).toString().split("\n").length;
        
        // Start building the final string (include ANSI reset sequence if needed)
        StringBuilder sb = new StringBuilder("\u001B[0m\r");
    
        // Iterate over each row of cards
        for (int row = 0; row < numRows; row++) {
            // For each line in the card representation
            for (int lineIndex = 0; lineIndex < maxLines; lineIndex++) {
                // Iterate over each card in the current row
                for (int col = 0; col < CARDS_PER_ROW; col++) {
                    int index = row * CARDS_PER_ROW + col;
                    if (index >= numCards) break; // No more cards
                    String[] cardLines = cards.get(index).toString().split("\n");
                    sb.append(cardLines[lineIndex]);
                    // Add space between cards, if not the last card in the row
                    if (col < CARDS_PER_ROW - 1 && index < numCards - 1) {
                        sb.append("  ");
                    }
                }
                sb.append("\n");
            }
            sb.append("\n"); // Blank line to separate rows
        }
        
        return sb.toString().trim();
    }    

    // A convenience method to build a player's collection as a map from a list of
    // cards.
    public static Map<Color, List<Card>> buildCollectionMap(List<Card> collectedCards) {
        Map<Color, List<Card>> collectionsByColor = new HashMap<>();
        for (Color color : Color.values()) {
            collectionsByColor.put(color, new ArrayList<>());
        }
        for (Card card : collectedCards) {
            collectionsByColor.get(card.getColor()).add(card);
        }
        return collectionsByColor;
    }

 
}