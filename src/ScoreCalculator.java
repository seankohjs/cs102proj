import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreCalculator {

    public Map<Suit, Player> determineSuitMajorities(List<Player> players) {
        Map<Suit, Player> majorities = new HashMap<>();
        for (Suit suit : Suit.values()) {
            Player majorityPlayer = null;
            int maxCount = -1;
            List<Player> tiedPlayers = new ArrayList<>();

            for (Player player : players) {
                int count = player.getCardCountInSuit(suit);
                if (count > maxCount) {
                    maxCount = count;
                    majorityPlayer = player;
                    tiedPlayers.clear();
                    tiedPlayers.add(player);
                } else if (count == maxCount && count > 0) {
                    tiedPlayers.add(player);
                }
            }
            if (tiedPlayers.size() == 1) {
                majorities.put(suit, majorityPlayer);
            } else {
                majorities.put(suit, null);
            }
        }
        return majorities;
    }

    public int calculatePlayerFinalScore(Player player, Map<Suit, Player> suitMajorities) {
        int baseScore = 0;
        int majorityScore = 0;
        for (Suit suit : Suit.values()) {
            int suitCardCount = player.getCardCountInSuit(suit);
            if (suitMajorities.get(suit) == player) {
                majorityScore += suitCardCount;
            } else {
                for (Card card : player.getCollectedCards()) {
                    if (card.getSuit() == suit) {
                        baseScore += card.getValue();
                    }
                }
            }
        }
        return baseScore + majorityScore;
    }

    public Player determineWinner(List<Player> players) {
        Map<Suit, Player> suitMajorities = determineSuitMajorities(players);
        Player winner = null;
        int minScore = Integer.MAX_VALUE;
        List<Player> tiedPlayers = new ArrayList<>();

        for (Player player : players) {
            int playerScore = calculatePlayerFinalScore(player, suitMajorities);
            if (playerScore < minScore) {
                minScore = playerScore;
                winner = player;
                tiedPlayers.clear();
                tiedPlayers.add(player);
            } else if (playerScore == minScore) {
                tiedPlayers.add(player);
            }
        }
        if (tiedPlayers.size() == 1) {
            return winner;
        } else {
            return determineTiebreakerWinner(tiedPlayers);
        }
    }

    private Player determineTiebreakerWinner(List<Player> tiedPlayers) {
        Player tiebreakerWinner = null;
        int minCollectedCards = Integer.MAX_VALUE;
        for (Player player : tiedPlayers) {
            int collectedCount = player.getCollectedCards().size();
            if (collectedCount < minCollectedCards) {
                minCollectedCards = collectedCount;
                tiebreakerWinner = player;
            }
        }
        return tiebreakerWinner;
    }
}
