import java.util.List;

public class EasyBot extends Bot {

    public EasyBot(String botName) {
        super(botName);
    }

    // Always discard latest card in deck
    public Card discard() {
        return super.getHand().removeLast();
    }

    public Card takeCard(List<Card> candidates) {
        return candidates.removeLast();
    }
}
