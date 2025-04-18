# Parade Card Game

A Java implementation of the strategic card game **Parade**, where players aim to score the lowest points by carefully managing their card collections and color majorities.

---

## Overview

Parade is a card game for 2 to 6 players where each participant takes turns playing cards into a parade line. When cards are played, they may cause other cards to be collected based on their position, color, and value. The goal is to finish the game with the lowest score.

---

## Setup and Installation

### Requirements
- **Java Runtime Environment (JRE) 21 or higher**

### Running the Game

#### Windows:
1. **Clone or download** the repository.
2. Open a **command prompt** in the project directory.
3. Run the start.bat file by typing "./start.bat" and pressing Enter.

#### Mac/Linux:
1. **Clone or download** the repository.
2. Open a **terminal** in the project directory.
3. Run the start.bat file by typing "./start.bat" and pressing Enter.

---

## How to Play

### Main Menu
- **1:** Start a New Game
- **2:** View Game Rules
- **3:** Quit

### Game Setup
- Enter number of **Human Players** (1 - 6)
- Enter number of **AI Players** (0 - 5; at least 1 if playing solo; skip if 6 Human Players)
- **Name** your players and select difficulty for AI players

### Gameplay
- **Turn-Based Play:** On your turn, select a card from your hand to add to the end of the parade (rightmost end)
- **Card Mechanics:** Cards are removed from the parade based on the played card's value and color.
- **Scoring:** Collected cards go into your collection and count toward your score.
- **Victory Condition:** The player with the **lowest score** at the end wins.

### Game End Conditions
- A player collects cards of **all 6 colors**.
- The draw pile is **exhausted**.

---

## Game Features

- **Player Support:** 2 - 6 players (combination of human and AI).
- **AI Difficulty:** Two difficulty levels:
  - **Easy/Random**
  - **Hard/Strategic**
- **User Interface:** Color-coded terminal interface using ANSI color codes.
- **Scoring System:** Detailed scoring with color majorities and tiebreakers.
- **Interactive Rules:** Full game rules are available in-game through the main menu.

---

## Project Structure

- **com.parade.model:** Game data classes such as `Card`, `Player`, `Deck`, etc.
- **com.parade.view:** User interface classes.
- **com.parade.controller:** Game logic controllers.
- **com.parade.util:** Utility classes for display and formatting.
- **com.parade.ai:** Implementation of AI players.

---

## Additional Notes

- The game uses **ANSI color codes** for terminal display.
- AI players implement different strategies depending on the selected difficulty.
- Detailed rules and gameplay instructions are available **in-game** via the main menu.
