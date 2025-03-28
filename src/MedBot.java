import java.util.List;

import javax.smartcardio.Card;

public class MedBot extends Bot{
    private List<Card> hand;
    private List<Card> collectedCards;

    public MedBot(String botName) {
        super(botName);
    }

    // medium bot throw first card
    public Card discard(){
        return super.getHand().removeFirst();
    }
}
