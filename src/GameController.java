import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;

public class GameController {
    private Deck deck;
    private ParadeLine paradeLine;
    private List<Player> players;
    private TurnManager turnManager;
    private ScoreCalculator scoreCalculator;
    private boolean isLastRound;

    private int extraTurnCount = 0;
    private GameView view;

    public GameController(List<String> playerNames, List<Boolean> isBot,
            List<Integer> botDifficulties, Scanner scanner) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();

        // Create the appropriate player types
        for (int i = 0; i < playerNames.size(); i++) {
            if (isBot.get(i)) {
                players.add(new BotPlayer(playerNames.get(i), botDifficulties.get(i)));
            } else {
                players.add(new Player(playerNames.get(i)));
            }
        }

        this.turnManager = new TurnManager(players);
        this.scoreCalculator = new ScoreCalculator();
        this.isLastRound = false;
        this.view = new GameView(scanner);

        // Deal initial parade line (6 cards)
        for (int i = 0; i < 6; i++) {
            Card card = deck.drawCard();
            if (card != null) {
                paradeLine.addCardToLine(card);
            }
        }
        // Deal 5 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addToHand(card);
                }
            }
        }
    }

    public void startGame() {
        while (true) {
            view.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            view.displayTurnHeader(currentPlayer);
            view.displayGameState(deck, paradeLine, isLastRound);
            view.displayPlayerCollections(currentPlayer);
            view.displayOtherPlayersCollections(players, currentPlayer);
    
            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = view.getPlayerCardChoice(currentPlayer, paradeLine, players);
                playTurn(cardToPlay);
            } else {
                view.displayMessage(currentPlayer.getPlayerName() + " has no cards to play! Passing turn.");
            }
    
            view.promptForNextTurn(currentPlayer);
    
            if (!isLastRound) {
                checkGameEndConditions();
                turnManager.nextPlayer();
            } else {
                turnManager.nextPlayer();
                extraTurnCount++;
                if (extraTurnCount >= players.size()) {
                    // All players have had their final turn - end the game
                    break;
                }
            }
        }
        
        // Call endGame() after exiting the main game loop
        endGame();
    }

    private void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        view.displayMessage(currentPlayer.getPlayerName() + " Plays:\n" + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard,
                paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            view.displayMessage(currentPlayer.getPlayerName() + " takes the following card from the parade.");
            System.out.println(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            view.displayMessage(currentPlayer.getPlayerName() + " takes no card from the parade.");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                view.displayMessage(currentPlayer.getPlayerName() + " draws a card.");
            } else {
                view.displayMessage("Deck is empty, no card drawn.");
            }
            view.displayMessage("\n" + currentPlayer.getPlayerName() + "'s Current Hand: "
                    + GameUtils.handToString(currentPlayer.getHand()));
        } else {
            view.displayMessage(currentPlayer.getPlayerName() + " does not draw a card in last round.");
        }
        view.displayPlayerCollections(currentPlayer);
    }

    private void endGame() {
        view.clearScreen();
        view.displayMessage("\nGame Over!");
        view.displayMessage("\n--- Discarding 2 Hand Cards for Scoring ---");

        for (Player player : players) {
            view.clearScreen();
            view.displayMessage("\n" + player.getPlayerName() + ", it's time to discard 2 cards from your hand.");
            view.displayMessage(player.getPlayerName() + "'s Current Collection:");
            view.displayPlayerCollections(player);
            view.interactiveDiscardTwoCards(player);
            view.displayMessage(player.getPlayerName() + " discards hand to: "
                    + GameUtils.handToString(player.getHand()));
            player.addHandToCollection();
            view.displayMessage("\nUpdated Game State for " + player.getPlayerName() + ":");
            view.displayPlayerCollections(player);
            view.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        displayFinalScoreboard();

        // Wait for user acknowledgment before returning to menu
        System.out
                .println("\n" + Print.BOLD + "Game complete! Press [ENTER] to return to the main menu." + Print.RESET);
        Scanner scanner = new Scanner(System.in); 
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void displayFinalScoreboard() {
        view.clearScreen();

        System.out.println("\n" + Print.BOLD + "■■■■■ FINAL RESULTS ■■■■■" + Print.RESET);
        System.out.println("\n--- Final Player Collections ---");

        for (Player player : players) {
            view.displayPlayerCollections(player);
            System.out.println(); // Add spacing between players
        }

        // Calculate and display all scores
        Map<Suit, List<Player>> suitMajorities = scoreCalculator.determineSuitMajorities(players);

        System.out.println(Print.BOLD + "■■■■■ FINAL SCORES ■■■■■" + Print.RESET);
        System.out.println("\nPlayer               Score");
        System.out.println("------------------------------");

        // Store scores for determining winner
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = scoreCalculator.calculatePlayerFinalScore(player, suitMajorities);
            playerScores.put(player, score);

            // Format the score line with proper spacing
            String scoreLine = String.format("%-20s %d", player.getPlayerName(), score);
            System.out.println(scoreLine);
        }

        // Determine and announce the winner
        Player winner = scoreCalculator.determineWinner(players);
        int winnerScore = playerScores.get(winner);

        System.out.println("\n" + Print.BOLD + Print.GREEN + "WINNER: " +
                winner.getPlayerName() + " with " + winnerScore + " points!" + Print.RESET);

        // Display suit majorities
        System.out.println("\n" + Print.BOLD + "■■■■■ SUIT MAJORITIES ■■■■■" + Print.RESET);
        for (Suit suit : Suit.values()) {
            List<Player> majorityPlayers = suitMajorities.get(suit);
            if (majorityPlayers != null && !majorityPlayers.isEmpty()) {
                System.out.print(getSuitColor(suit) + suit + Print.RESET + ": ");
                for (int i = 0; i < majorityPlayers.size(); i++) {
                    System.out.print(majorityPlayers.get(i).getPlayerName());
                    if (i < majorityPlayers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println(getSuitColor(suit) + suit + Print.RESET + ": No majority");
            }
        }
    }

    // Helper method to get color for suit display
    private String getSuitColor(Suit suit) {
        switch (suit) {
            case RED:
                return Print.RED;
            case BLUE:
                return Print.BLUE;
            case GREEN:
                return Print.GREEN;
            case ORANGE:
                return Print.ORANGE;
            case PURPLE:
                return Print.PURPLE;
            case GREY:
                return Print.GREY;
            default:
                return "";
        }
    }

    private void calculateFinalScores() {
        view.displayMessage("\n--- Scores ---");
        Map<Suit, List<Player>> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        for (Player player : players) {
            int score = scoreCalculator.calculatePlayerFinalScore(player, suitMajorities);
            view.displayMessage(player.getPlayerName() + " final score: " + score);
        }
    }

    private void determineWinner() {
        Player winner = scoreCalculator.determineWinner(players);
        Map<Suit, List<Player>> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        int winnerScore = scoreCalculator.calculatePlayerFinalScore(winner, suitMajorities);
        view.displayMessage("\nWinner: " + winner.getPlayerName() + " with score " + winnerScore + "!");
    }

    private void checkGameEndConditions() {
        if (!isLastRound) {
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true);
            } else if (deck.isEmpty()) {
                startLastRound(false);
            }
        }
    }

    private boolean hasAnyoneCollectedSixColors() {
        for (Player player : players) {
            Set<Suit> collectedSuits = new HashSet<>();
            for (Card card : player.getCollectedCards()) {
                collectedSuits.add(card.getSuit());
            }
            if (collectedSuits.size() >= 6) {
                return true;
            }
        }
        return false;
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0;
        view.displayMessage("\n--- Last Round Started! ---");
        if (sixColors) {
            view.displayMessage("Triggered by a player collecting 6 colors.");
        } else {
            view.displayMessage("Triggered by deck exhaustion.");
        }
    }
}
