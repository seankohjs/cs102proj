package com.parade.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.parade.model.*;

public class ScoreCalculator {

    // Determine which player has the majority for each color
    public static Map<Color, List<Player>> determineColorMajorities(List<Player> players) {
        Map<Color, List<Player>> majorities = new HashMap<>();
        boolean isTwoPlayerGame = (players.size() == 2);
        for (Color color : Color.values()) {
            List<Player> majorityPlayers = null;
            if (isTwoPlayerGame) {
                majorityPlayers = determineTwoPlayerMajority(players, color);
            } else {
                majorityPlayers = determineMultiPlayerMajority(players, color);
            }
            majorities.put(color, majorityPlayers);
        }
        return majorities;
    }
    
    // Determines color majority for 2-player games (need 2+ more cards than opponent)
    public static List<Player> determineTwoPlayerMajority(List<Player> players, Color color) {
        List<Player> majorityPlayers = new ArrayList<>();
        Player p1 = players.get(0);
        Player p2 = players.get(1);
        int count1 = p1.getColorCardCount(color);
        int count2 = p2.getColorCardCount(color);
        
        // A player must have at least 1 card and 2+ more cards than opponent
        if (count1 > 0 && count1 >= count2 + 2) {
            majorityPlayers.add(p1);
        } else if (count2 > 0 && count2 >= count1 + 2) {
            majorityPlayers.add(p2);
        }
        
        return majorityPlayers.isEmpty() ? null : majorityPlayers;
    }
    
    // Determines color majority for games with 3+ players (Standard Rule)
    public static List<Player> determineMultiPlayerMajority(List<Player> players, Color color) {
        int maxCount = 0;
        List<Player> majorityPlayers = new ArrayList<>();

        // First pass: find the maximum card count for this color
        for (Player player : players) {
            int count = player.getColorCardCount(color);
            if (count > maxCount) {
                maxCount = count;
            }
        }

        // Second pass: collect all players with the maximum card count
        for (Player player : players) {
            int count = player.getColorCardCount(color);
            if (count == maxCount && count > 0) {
                majorityPlayers.add(player);
            }
        }

        return majorityPlayers.isEmpty() ? null : majorityPlayers;
    }

    public static int calculatePlayerFinalScore(Player player, Map<Color, List<Player>> colorMajorities) {
        int baseScore = 0;
        int majorityScore = 0;
        for (Color color : Color.values()) {
            int colorCardCount = player.getColorCardCount(color);
            List<Player> majorityPlayers = colorMajorities.get(color);
            
            if (majorityPlayers != null && majorityPlayers.contains(player)) {
                majorityScore += colorCardCount;
            } else {
                for (Card card : player.getCollectedCards()) {
                    if (card.getColor() == color) {
                        baseScore += card.getValue();
                    }
                }
            }
        }
        return baseScore + majorityScore;
    }

    public static List<Player> determineWinner(List<Player> players) {
        Map<Color, List<Player>> suitMajorities = determineColorMajorities(players);
        int minScore = Integer.MAX_VALUE;
        List<Player> tiedPlayers = new ArrayList<>();

        for (Player player : players) {
            int playerScore = calculatePlayerFinalScore(player, suitMajorities);
            if (playerScore < minScore) {
                minScore = playerScore;
                tiedPlayers.clear();
                tiedPlayers.add(player);
            } else if (playerScore == minScore) {
                tiedPlayers.add(player);
            }
        }
        if (tiedPlayers.size() == 1) {
            return tiedPlayers;
        } else {
            return determineTiebreakerWinner(tiedPlayers);
        }
    }

    private static List<Player> determineTiebreakerWinner(List<Player> tiedPlayers) {
        int minCollectedCards = Integer.MAX_VALUE;
        List<Player> tiedWinners = new ArrayList<>();
        
        for (Player player : tiedPlayers) {
            int collectedCount = player.getCollectedCards().size();
            if (collectedCount < minCollectedCards) {
                minCollectedCards = collectedCount;
                tiedWinners.clear();
                tiedWinners.add(player);
            } else if (collectedCount == minCollectedCards) {
                tiedWinners.add(player);
            }
        }
        return tiedWinners;
    }
}

