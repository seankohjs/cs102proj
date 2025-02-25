public class Player {
    // Instance variable
    public String name;
    public int points;
    public ArrayList<String> hand;
    public ArrayList<String> pile;
    
    // Static variable
    private static final int START_POINTS = 0;

    // Constructor
    public Player(String name){
        this.name = name;
        this.points = START_POINTS;
    }
    
    // Instance method
    public int getPoints(){
        return points;
    }

    public setPoints(int points){
        this.points = points;
    }

    public addPoints(int toGive){
        this.points += toGive;
    }

    public addToHand(){
        hand.add();
    }

    public throwFromHand(){
        hand.remove();
    }

    public addToPile(){
        pile.add();
    }

    // Static method

}
