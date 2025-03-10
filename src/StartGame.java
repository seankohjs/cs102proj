import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of players (2-6): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // consume newline

        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            playerNames.add(scanner.nextLine());
        }

        GameController game = new GameController(playerNames, scanner);
        game.startGame();
        scanner.close();
    }

}
