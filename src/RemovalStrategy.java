import java.util.ArrayList;
import java.util.List;

public class RemovalStrategy {

    public static class RemovalChoice {
        public List<Card> sameSuitCandidates;
        public List<Card> lowerValueCandidates;

        public RemovalChoice(List<Card> sameSuitCandidates, List<Card> lowerValueCandidates) {
            this.sameSuitCandidates = sameSuitCandidates;
            this.lowerValueCandidates = lowerValueCandidates;
        }
    }

    public RemovalChoice determineRemovalChoice(Card playedCard, List<Card> paradeCards) {
        List<Card> sameSuitCards = new ArrayList<>();
        List<Card> lowerValueCards = new ArrayList<>();

        if (paradeCards.size() <= 1) {
            return new RemovalChoice(sameSuitCards, lowerValueCards);
        }

        List<Card> lineBeforePlayed = paradeCards.subList(0, paradeCards.size() - 1);
        int lineSizeBeforePlayed = lineBeforePlayed.size();

        if (lineSizeBeforePlayed <= playedCard.getValue()) {
            return new RemovalChoice(sameSuitCards, lowerValueCards);
        }

        // Cards in removal mode: those with a position number larger than
        // playedCard.getValue()
        List<Card> removalModeCards = new ArrayList<>();
        for (int i = 0; i < lineSizeBeforePlayed; i++) {
            if (lineSizeBeforePlayed - i > playedCard.getValue()) {
                removalModeCards.add(lineBeforePlayed.get(i));
            }
        }

        // Determine candidates based on suit and value
        for (Card card : removalModeCards) {
            if (card.getSuit() == playedCard.getSuit()) {
                sameSuitCards.add(card);
            }
            if (card.getValue() <= playedCard.getValue()) {
                lowerValueCards.add(card);
            }
        }

        return new RemovalChoice(sameSuitCards, lowerValueCards);
    }
}
