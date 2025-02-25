public class Card {
    //variables
    private String color;
    private int value;
    
    //constructor
    public Card(String color, int value) {
        this.color = color;
        this.value = value;
    }
    
    //methods
    public String getColor() {
        return color;
    }
    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return color + " " + value;
    }
}