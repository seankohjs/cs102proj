package refactor;

// Represents the 6 suits (colors) in the Parade deck.
public enum Suit {
    RED, // Or Color1, Red, etc.
    BLUE, // Or Color2, Blue, etc.
    GREEN,
    ORANGE,
    PURPLE,
    GREY;

    // Utility to get all suits, useful for deck creation
    public static Suit[] getAllSuits() {
        return Suit.values();
    }
}