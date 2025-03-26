import java.util.ArrayList;
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
    private RemovalStrategy removalStrategy;
    private ScoreCalculator scoreCalculator;
    private boolean isLastRound;

    private int extraTurnCount = 0;
    private GameView view;

    public GameController(List<String> playerNames, Scanner scanner) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        this.turnManager = new TurnManager(players);
        this.removalStrategy = new RemovalStrategy();
        this.scoreCalculator = new ScoreCalculator();
        this.isLastRound = false;
        // Instantiate the view and pass in the scanner
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
                Card cardToPlay = view.getPlayerCardChoice(currentPlayer);
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
                    break;
                }
            }
        }
        endGame();
    }

    private void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        view.displayMessage(currentPlayer.getPlayerName() + " Plays:\n" + playedCard);
        paradeLine.addCardToLine(playedCard);

        RemovalStrategy.RemovalChoice removalChoice = removalStrategy.determineRemovalChoice(playedCard,
                paradeLine.getParadeLineCards());
        List<Card> cardsToRemove = new ArrayList<>();

        // Handle removal for same suit
        if (!removalChoice.sameSuitCandidates.isEmpty()) {
            Card choice = view.getPlayerRemovalChoice(currentPlayer, removalChoice.sameSuitCandidates, "same suit");
            if (choice != null) {
                cardsToRemove.add(choice);
            }
        }

        // Handle removal for lower value (after excluding any already chosen)
        if (!removalChoice.lowerValueCandidates.isEmpty()) {
            List<Card> remainingCandidates = new ArrayList<>(removalChoice.lowerValueCandidates);
            remainingCandidates.removeAll(cardsToRemove);
            if (!remainingCandidates.isEmpty()) {
                Card choice = view.getPlayerRemovalChoice(currentPlayer, remainingCandidates, "lower value");
                if (choice != null) {
                    cardsToRemove.add(choice);
                }
            }
        }

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

        view.displayMessage("\n--- Final Player Collections Before Scoring ---");
        for (Player player : players) {
            view.displayPlayerCollections(player);
        }
        calculateFinalScores();
        determineWinner();
    }

    private void calculateFinalScores() {
        view.displayMessage("\n--- Scores ---");
        Map<Suit, Player> suitMajorities = scoreCalculator.determineSuitMajorities(players);
        for (Player player : players) {
            int score = scoreCalculator.calculatePlayerFinalScore(player, suitMajorities);
            view.displayMessage(player.getPlayerName() + " final score: " + score);
        }
    }

    private void determineWinner() {
        Player winner = scoreCalculator.determineWinner(players);
        Map<Suit, Player> suitMajorities = scoreCalculator.determineSuitMajorities(players);
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
