
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreCalculator {

    public Map<Suit, List<Player>> determineSuitMajorities(List<Player> players) {
        Map<Suit, List<Player>> majorities = new HashMap<>();
        boolean isTwoPlayerGame = (players.size() == 2);
        
        for (Suit suit : Suit.values()) {
            List<Player> majorityPlayers = isTwoPlayerGame ? 
                determineTwoPlayerMajority(players, suit) : 
                determineMultiPlayerMajority(players, suit);
            
            majorities.put(suit, majorityPlayers);
        }
        return majorities;
    }
    
    /**
     * Determines suit majority for 2-player games (need 2+ more cards than opponent)
     */
    private List<Player> determineTwoPlayerMajority(List<Player> players, Suit suit) {
        List<Player> majorityPlayers = new ArrayList<>();
        Player p1 = players.get(0);
        Player p2 = players.get(1);
        int count1 = p1.getCardCountInSuit(suit);
        int count2 = p2.getCardCountInSuit(suit);
        
        // A player must have at least 1 card and 2+ more cards than opponent
        if (count1 > 0 && count1 >= count2 + 2) {
            majorityPlayers.add(p1);
        } else if (count2 > 0 && count2 >= count1 + 2) {
            majorityPlayers.add(p2);
        }
        
        return majorityPlayers.isEmpty() ? null : majorityPlayers;
    }
    
    /**
     * Determines suit majority for games with 3+ players (standard rule)
     */
    private List<Player> determineMultiPlayerMajority(List<Player> players, Suit suit) {
        int maxCount = -1;
        List<Player> majorityPlayers = new ArrayList<>();

        // First pass: find the maximum card count for this suit
        for (Player player : players) {
            int count = player.getCardCountInSuit(suit);
            if (count > maxCount) {
                maxCount = count;
            }
        }

        // Second pass: collect all players with the maximum card count
        for (Player player : players) {
            int count = player.getCardCountInSuit(suit);
            if (count == maxCount && count > 0) {
                majorityPlayers.add(player);
            }
        }

        return majorityPlayers.isEmpty() ? null : majorityPlayers;
    }

    public int calculatePlayerFinalScore(Player player, Map<Suit, List<Player>> suitMajorities) {
        int baseScore = 0;
        int majorityScore = 0;
        for (Suit suit : Suit.values()) {
            int suitCardCount = player.getCardCountInSuit(suit);
            List<Player> majorityPlayers = suitMajorities.get(suit);
            
            if (majorityPlayers != null && majorityPlayers.contains(player)) {
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
        Map<Suit, List<Player>> suitMajorities = determineSuitMajorities(players);
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

