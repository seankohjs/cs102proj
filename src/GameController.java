import java.util.*;

public class GameController {
    private Deck deck;
    private ParadeLine paradeLine;
    private List<Player> players;
    private TurnManager turnManager;
    private boolean isLastRound;
    private Scanner scanner;
    private int extraTurnCount = 0;
    private GameView view;

    public GameController(List<String> playerNames, List<Boolean> isBot,List<Integer> botDifficulties, Scanner scanner) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        this.scanner = scanner;
        this.view = new GameView(scanner);

        // Create the appropriate player types
        for (int i = 0; i < playerNames.size(); i++) {
            if (isBot.get(i)) {
                players.add(new BotPlayer(playerNames.get(i), botDifficulties.get(i)));
            } else {
                players.add(new Player(playerNames.get(i)));
            }
        }

        this.turnManager = new TurnManager(players);
        this.isLastRound = false;

        // Deal initial parade line of 6 cards
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
            //view.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            view.displayTurnHeader(currentPlayer);
            view.displayGameState(deck, paradeLine, isLastRound);
            view.displayPlayerCollections(currentPlayer);
            view.displayOtherPlayersCollections(players, currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = view.getPlayerCardChoice(currentPlayer, paradeLine, players);
                playTurn(cardToPlay);
            } else {
                view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " HAS NO CARDS TO PLAY! Passing Turn.." + Print.RESET);
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

    public void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " PLAYS ::\n\n" + Print.RESET + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard, paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " TAKES THE FOLLOWING CARDS FROM THE PARADE..");
            view.displayMessage(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " TAKES NO CARD FROM THE PARADE..");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " DRAWS A CARD.." + Print.RESET);
            } else {
                view.displayMessage(Print.BOLD + Print.RED + "DECK IS EMPTY, NO CARD DRAWN.." + Print.RESET);
            }
            view.displayMessage("\n" + Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + "'S CURRENT HAND :: " + Print.RESET);
            view.displayMessage(GameUtils.handToString(currentPlayer.getHand()));
        } else {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.GREY + " DOES NOT DRAW A CARD IN THE LAST ROUND.." + Print.RESET);
        }
        view.displayPlayerCollections(currentPlayer);
    }

    public void endGame() {
        //view.clearScreen();
        view.displayMessage(Print.BOLD + Print.RED + "\nGAME OVER" + Print.RESET);
        view.displayMessage(Print.BOLD + "\n■■■■■ *** SCORING STAGE -" + Print.RED + " DISCARD " + Print.GREY + "2 CARDS FROM HAND *** ■■■■■" + Print.RESET);

        for (Player player : players) {
            //view.clearScreen();
            view.displayMessage("\n" + player.getPlayerName() + ", IT'S TIME TO" + Print.RED + Print.BOLD + " DISCARD " + Print.GREY + "2 CARDS FROM YOUR HAND.." + Print.RESET);
            view.displayMessage(Print.BOLD + Print.GREEN + player.getPlayerName() + "'S " + Print.GREY + "CURRENT COLLECTION :: " + Print.RESET);
            view.displayPlayerCollections(player);
            view.interactiveDiscardTwoCards(player);
            player.addHandToCollection();
            view.displayMessage(Print.BOLD + "\nUPDATED GAME STATE FOR " + Print.GREEN + player.getPlayerName() + Print.GREY + " :: " + Print.RESET);
            view.displayPlayerCollections(player);
            view.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        displayFinalScoreboard();

        // Wait for user acknowledgment before returning comenu
        System.out.print(Print.BOLD + "\nGAME COMPLETE! PRESS" + Print.RED + " [ENTER] " + Print.GREY + "TO RETURN TO THE MAIN MENU.." + Print.RESET);
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void displayFinalScoreboard() {
        //view.clearScreen();

        System.out.println(Print.BOLD + "\n■■■■■     FINAL RESULTS     ■■■■■");
        System.out.println("\n■■■■■ *** FINAL PLAYER COLLECTIONS *** ■■■■■" + Print.RESET);

        for (Player player : players) {
            view.displayPlayerCollections(player);
            System.out.println(); // Add spacing between players
        }

        // Calculate and display all scores
        Map<Color, List<Player>> colorMajorities = ScoreCalculator.determineColorMajorities(players);

        System.out.println(Print.BOLD + "■■■■■     FINAL SCORES     ■■■■■" + Print.RESET);
        System.out.println();
        System.out.println(Print.ORANGE + "PLAYER               SCORE" + Print.RESET);
        System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■");

        // Store scores for determining winner
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = ScoreCalculator.calculatePlayerFinalScore(player, colorMajorities);
            playerScores.put(player, score);

            // Format the score line with proper spacing
            String scoreLine = String.format("%-20s %d", player.getPlayerName(), score);
            System.out.println(scoreLine);
        }

        // Determine and announce the winner
        Player winner = ScoreCalculator.determineWinner(players);
        int winnerScore = playerScores.get(winner);

        System.out.println();
        System.out.println(Print.BOLD + Print.YELLOW + "*** WINNER: " + winner.getPlayerName() + " WITH " + winnerScore + " POINTS! ***" + Print.RESET);

        // Display color majorities
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■     SUIT MAJORITIES     ■■■■■" + Print.RESET);
        System.out.println();
        for (Color color : Color.values()) {
            List<Player> majorityPlayers = colorMajorities.get(color);
            if (majorityPlayers != null && !majorityPlayers.isEmpty()) {
                System.out.print(Card.getDisplayColor(color) + color + Print.RESET + ": ");
                for (int i = 0; i < majorityPlayers.size(); i++) {
                    System.out.print(majorityPlayers.get(i).getPlayerName());
                    if (i < majorityPlayers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println(Card.getDisplayColor(color) + color + Print.RESET + Print.BOLD + ":: NO MAJORITY" + Print.RESET);
            }
        }
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
            Set<Color> collectedColors = new HashSet<>();
            for (Card card : player.getCollectedCards()) {
                collectedColors.add(card.getColor());
            }
            if (collectedColors.size() >= 6) {
                return true;
            }
        }
        return false;
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0;
        view.displayMessage(Print.BOLD + Print.YELLOW + "\n■■■■■ *** LAST ROUND STARTED *** ■■■■■" + Print.RESET);
        if (sixColors) {
            view.displayMessage(Print.BOLD + "TRIGGERED BY A PLAYER COLLECTING 6 COLORS." + Print.RESET);
        } else {
            view.displayMessage(Print.BOLD + "TRIGGERED BY DECK EXHAUSTION." + Print.RESET);
        }
    }
}
