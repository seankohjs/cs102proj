package com.parade.view;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.parade.util.Print;

public class GameMenu {

    public static void display(){
        System.out.print(Print.BOLD);
        System.out.println();
        System.out.println("■■■■■ PARADE GAME MENU ■■■■■");
        System.out.println();
        System.out.println("1 .. PLAY GAME");
        System.out.println("2 .. VIEW GAME RULES");
        System.out.println("3 .. QUIT GAME");
        System.out.println();
    }

    public static boolean readOptions(Scanner sc){
        int choice = 0;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print(Print.BOLD + "PLEASE ENTER YOUR OPTION : ");
                choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= 3) {
                    valid = true;
                } else { 
                    System.out.println(Print.BOLD + "PLEASE ENTER [1], [2] OR [3] .." + Print.DEFAULT);
                }
            } catch (NumberFormatException e) {
                System.out.println(Print.BOLD + "PLEASE ENTER [1], [2] OR [3] .." + Print.DEFAULT);
            }
        }

        GameView.clearScreen();
        switch (choice) {
            case 1:
                return true;
            case 2:
                readRules(sc);
                break;
            case 3:
                System.out.println(Print.BOLD + "EXITING PARADE GAME ..");
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
        return false;
    }

    private static void readRules(Scanner sc){
        try {
            String fullRules = new String(Files.readAllBytes(Paths.get("GameRules.txt")));
            
            // Split the rules by empty lines (consecutive newlines)
            String[] rawSections = fullRules.split("\n\\s*\n");
            
            // Clean up the sections
            List<String> validSections = new ArrayList<>();
            for (String section : rawSections) {
                if (!section.trim().isEmpty()) {
                    validSections.add(section.trim());
                }
            }
            
            String[] sections = validSections.toArray(new String[0]);
            
            // If no sections were found or very few, fall back to arbitrary chunking
            if (sections.length <= 1) {
                // Split into chunks of approximately 20 lines each
                String[] lines = fullRules.split("\n");
                int linesPerSection = 20;
                int numSections = (int) Math.ceil((double) lines.length / linesPerSection);
                
                sections = new String[numSections];
                for (int i = 0; i < numSections; i++) {
                    StringBuilder sectionBuilder = new StringBuilder();
                    int startLine = i * linesPerSection;
                    int endLine = Math.min(startLine + linesPerSection, lines.length);
                    
                    for (int j = startLine; j < endLine; j++) {
                        sectionBuilder.append(lines[j]).append("\n");
                    }
                    sections[i] = sectionBuilder.toString().trim();
                }
            }
    
            int currentSection = 0;
            boolean viewingRules = true;
    
            while (viewingRules && currentSection < sections.length) {
                GameView.clearScreen();
    
                // Display section header
                System.out.println("");
                System.out.println(Print.BOLD + "■■■■■ PARADE GAME RULES ■■■■■" + Print.DEFAULT);
                System.out.println("");
                System.out.println("SECTION " + (currentSection + 1) + " OF " + sections.length);
                System.out.println("");
                System.out.println("■■■■■");
    
                // Display current section content
                System.out.println(sections[currentSection]);
                System.out.println("");
    
                // Navigation options
                System.out.println("■■■■■");
                System.out.println("");
                System.out.println(Print.BOLD + "NAVIGATION: " + Print.RED + "[N]" + Print.DEFAULT + "EXT SECTION   ■   " + 
                                                Print.BOLD + Print.RED + "[P]" + Print.DEFAULT + "REVIOUS SECTION   ■   " + 
                                                Print.BOLD + Print.RED + "[Q]" + Print.DEFAULT + "UIT TO MENU");
                System.out.print("ENTER YOUR CHOICE :: ");
    
                String input = sc.nextLine().trim().toUpperCase();
    
                switch (input) {
                    case "N":
                        if (currentSection < sections.length - 1) {
                            currentSection++;
                        } else {
                            System.out.println("");
                            System.out.println(Print.BOLD + "YOU HAVE REACHED THE END OF THE GAME RULES ..");
                            System.out.print("PRESS" + Print.RED + " [ENTER] " + Print.DEFAULT + Print.BOLD + "TO RETURN TO THE MAIN MENU .. " + Print.DEFAULT);
                            sc.nextLine();
                            viewingRules = false;
                        }
                        break;
                    case "P":
                        if (currentSection > 0) {
                            currentSection--;
                        } else {
                            System.out.println("");
                            System.out.println("YOU HAVE REACHED THE BEGINNING OF THE GAME RULES.");
                            System.out.print("PRESS" + Print.BOLD + Print.RED + " [ENTER] " + Print.DEFAULT + Print.BOLD + "TO CONTINUE .. " + Print.DEFAULT);
                            sc.nextLine();
                        }
                        break;
                    case "Q":
                        viewingRules = false;
                        break;
                    default:
                        System.out.print("INVALID OPTION! " + "PRESS" + Print.BOLD + Print.RED + " [ENTER] " + Print.DEFAULT + Print.BOLD + "TO CONTINUE .. " + Print.DEFAULT);
                        sc.nextLine();
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING GAME RULES: " + e.getMessage());
            System.out.print("PRESS" + Print.BOLD + Print.RED + " [ENTER] " + Print.DEFAULT + "TO RETURN TO THE MAIN MENU .. ");
            sc.nextLine();
        }
    }

    
}
