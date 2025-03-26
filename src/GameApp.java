import java.util.Scanner;

public class GameApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameMenu menu = new GameMenu(scanner);
        menu.readOption();
        scanner.close();
    }
}
