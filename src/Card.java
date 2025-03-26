public class Card {
    private Suit suit;
    private int value;

    public static final String HORIZONTAL = "\u2550"; // ═
    public static final String VERTICAL = "\u2551";   // ║
    public static final String TOP_LEFT = "\u2554";   // ╔
    public static final String TOP_RIGHT = "\u2557";  // ╗
    public static final String BOTTOM_LEFT = "\u255A"; // ╚
    public static final String BOTTOM_RIGHT = "\u255D"; // ╝


    public static final String SEPARATOR = "\u25A0";
    


    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String ORANGE = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String GREY = "\u001B[37m";
    public static final String RESET = "\u001B[0m";



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
    int width = (value == 10) ? 10 : 9;

    // Build top border with color
    top.append(suitColor).append(TOP_LEFT);
    for (int i = 0; i < width - 2; i++) {
        top.append(HORIZONTAL);
    }
    top.append(TOP_RIGHT).append(RESET);

    // Build middle part: card suit letter and value
    middle.append(suitColor).append(VERTICAL)
          .append(" ").append(suit.toString().charAt(0)).append(" ")
          .append(SEPARATOR)
          .append(" ").append(value).append(" ")
          .append(VERTICAL).append(RESET);

    // Build bottom border
    bottom.append(suitColor).append(BOTTOM_LEFT);
    for (int i = 0; i < width - 2; i++) {
        bottom.append(HORIZONTAL);
    }
    bottom.append(BOTTOM_RIGHT).append(RESET);

    return top.toString() + "\n" + middle.toString() + "\n" + bottom.toString();
    }

    private static String getColorForSuit(Suit suit) {
        switch (suit) {
            case Suit.RED: return RED;
            case Suit.BLUE: return BLUE;
            case Suit.GREEN: return GREEN;
            case Suit.ORANGE: return ORANGE;
            case Suit.PURPLE: return PURPLE;
            case Suit.GREY: return GREY;
            default: return RESET;
        }
    }
}