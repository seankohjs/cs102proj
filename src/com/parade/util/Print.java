package com.parade.util;

public class Print {
    
    public static final String BOLD = "\u001B[1m";
    public static final String DEFAULT = "\u001B[39m";
    // public static final String RESET = "\u001B[0m";
    
    // SEPARATOR
    public static final String SEPARATOR = "\u25A0"; // ■

    // COLORS
    public static final String RED = "\u001B[1;31m";
    public static final String BLUE = "\u001B[1;34m";
    public static final String GREEN = "\u001B[1;32m";
    public static final String ORANGE = "\033[1;38;5;214m";
    public static final String PURPLE = "\u001B[1;35m";
    public static final String GREY = "\u001B[1;37m";
    public static final String YELLOW = "\033[1;33m";

    // CARD BOX DRAWING
    public static final String HORIZONTAL = "\u2550"; // ═
    public static final String VERTICAL = "\u2551";   // ║
    public static final String TOP_LEFT = "\u2554";   // ╔
    public static final String TOP_RIGHT = "\u2557";  // ╗
    public static final String BOTTOM_LEFT = "\u255A"; // ╚
    public static final String BOTTOM_RIGHT = "\u255D"; // ╝
}
