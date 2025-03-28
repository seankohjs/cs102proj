import java.util.List;

import javax.smartcardio.Card;

public class HardBot extends Bot{
    private List<Card> hand;
    private List<Card> collectedCards;

    public HardBot(String botName) {
        super(botName);
    }

    // hard bot throw highest card to prevent getting cards into deck
    public Card discard(){
        int highCard = -1;
        int idx = 0;
        int counter = 0;
        for(Card c : hand){
            if(c.getValue() > highCard){
                highCard = c.getValue();
                idx = counter;
            }
            counter++;
        }
        return super.getHand().remove(idx);
    }
}
