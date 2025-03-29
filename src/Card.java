public class Card {

    // CARD BOX DRAWING
    public static final String HORIZONTAL = "\u2550"; // ═
    public static final String VERTICAL = "\u2551";   // ║
    public static final String TOP_LEFT = "\u2554";   // ╔
    public static final String TOP_RIGHT = "\u2557";  // ╗
    public static final String BOTTOM_LEFT = "\u255A"; // ╚
    public static final String BOTTOM_RIGHT = "\u255D"; // ╝

    // SEPARATOR BETWEEN SUIT AND VALUE
    public static final String SEPARATOR = "\u25A0";

    private Suit suit;
    private int value;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
    StringBuilder top = new StringBuilder();
    StringBuilder middle = new StringBuilder();
    StringBuilder bottom = new StringBuilder();

    // Determine the color based on the suit
    String suitColor = getColorForSuit(suit);

    // Determine the width based on card value length (use 10 for 10, 9 for others)
    int width = (value == 10) ? 12 : 11;

    // Build top border with color
    top.append(suitColor).append(TOP_LEFT);
    for (int i = 0; i < width - 2; i++) {
        top.append(HORIZONTAL);
    }
    top.append(TOP_RIGHT).append(Print.RESET);

    // Build middle part: card suit letter and value
    if (value == 10) {
        middle.append(suitColor).append(VERTICAL)
          .append(" ").append(suit.toString().substring(0, 2)).append(" ")
          .append(SEPARATOR)
          .append(" ").append(value).append(" ")
          .append(VERTICAL).append(Print.RESET);
    } else {
        middle.append(suitColor).append(VERTICAL)
          .append(" ").append(suit.toString().substring(0, 2)).append(" ")
          .append(SEPARATOR)
          .append(" 0").append(value).append(" ")
          .append(VERTICAL).append(Print.RESET);
    }

    // Build bottom border
    bottom.append(suitColor).append(BOTTOM_LEFT);
    for (int i = 0; i < width - 2; i++) {
        bottom.append(HORIZONTAL);
    }
    bottom.append(BOTTOM_RIGHT).append(Print.RESET);

    return top.toString() + "\n" + middle.toString() + "\n" + bottom.toString();
    }

    private static String getColorForSuit(Suit suit) {
        switch (suit) {
            case Suit.RED: return Print.RED;
            case Suit.BLUE: return Print.BLUE;
            case Suit.GREEN: return Print.GREEN;
            case Suit.ORANGE: return Print.ORANGE;
            case Suit.PURPLE: return Print.PURPLE;
            case Suit.GREY: return Print.GREY;
            default: return Print.RESET;
        }
    }
}