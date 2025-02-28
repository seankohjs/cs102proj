import java.util.*;

public class ClearScreen {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String input = null;
        do{
            System.out.print("Enter something> ");
            input = sc.nextLine();

            if(input.equals("c")){
                clearScreen();
            }
        }while(!input.equals("q"));
        
        sc.close();
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  
}
