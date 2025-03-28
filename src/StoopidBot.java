import java.util.List;

import javax.smartcardio.Card;

public class StoopidBot extends Bot{
    private List<Card> hand;
    private List<Card> collectedCards;

    public StoopidBot(String botName) {
        super(botName);
    }

    // stoopid bot throw random card
    public Card discard(){
        int idx = rand.nextInt(5); //random between 0-4
        return super.getHand().remove(idx);
    }
}
