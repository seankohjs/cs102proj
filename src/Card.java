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

    private Color color;
    private int value;

    public Card(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
    StringBuilder top = new StringBuilder();
    StringBuilder middle = new StringBuilder();
    StringBuilder bottom = new StringBuilder();

    // Determine the color based on the color
    String displayColor = getDisplayColor(color);

    int width = 13;

    // Build top border
    top.append(displayColor).append(TOP_LEFT);
    for (int i = 0; i < width - 4; i++) {
        top.append(HORIZONTAL);
    }
    top.append(TOP_RIGHT).append(Print.RESET);

    // Build middle border with card color and value
    String valueStr = (value < 10) ? " 0" + value : " " + value;
    middle.append(displayColor).append(VERTICAL)
          .append(" ").append(color.toString().substring(0, 2)).append(" ")
          .append(SEPARATOR).append(valueStr).append(" ")
          .append(VERTICAL).append(Print.RESET);

    // Build bottom border
    bottom.append(displayColor).append(BOTTOM_LEFT);
    for (int i = 0; i < width - 4; i++) {
        bottom.append(HORIZONTAL);
    }
    bottom.append(BOTTOM_RIGHT).append(Print.RESET);
    return top.toString() + "\n" + middle.toString() + "\n" + bottom.toString();
    }

    public static String getDisplayColor(Color color) {
        switch (color) {
            case RED:
                return Print.RED;
            case BLUE:
                return Print.BLUE;
            case GREEN:
                return Print.GREEN;
            case ORANGE:
                return Print.ORANGE;
            case PURPLE:
                return Print.PURPLE;
            case GREY:
                return Print.GREY;
            default:
                return Print.RESET;
        }
    }
}
