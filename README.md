# Portfolio project IDATT2003
This file uses Mark Down syntax. For more information see [here](https://www.markdownguide.org/basic-syntax/).

STUDENT NAME = "Kasper Østerlie Gladsøy"  
STUDENT ID = "123456"

## Project description

This project is a portfolio assignment for the IDATT2003 course. It contains two games: Ladder Game (Stigespillet) and Click Game. The Ladder Game is a digital board game where players move across tiles, encountering ladders and special tiles, while the Click Game is a simple reflex-based game.

## Project structure

- All source files are stored in `src/main/java/edu/ntnu/iir/bidata/`.
- The Ladder Game code is organized into subpackages such as `controller`, `model`, `view`, and `util` for separation of concerns.
- JUnit test classes are stored in `src/test/java/edu/ntnu/iir/bidata/`.
- Resources (images, CSS, etc.) are located in `src/main/resources/`.

## Link to repository

[https://github.com/kappern04/idatt2003-portfolio](https://github.com/kappern04/idatt2003-portfolio)

## How to run the project

- The main class is `edu.ntnu.iir.bidata.Main`.
- Run the project using your IDE or with Maven:  
  `mvn clean javafx:run`
- When started, you will be prompted to choose between Ladder Game and Click Game.
- Input is via the console and the GUI.
- The expected behavior is that the selected game launches and is playable via the GUI.

## How to run the tests

- Tests are written with JUnit and are located in `src/test/java/`.
- Run all tests with Maven:  
  `mvn test`
- You can also run individual test classes from your IDE.

## References

- Course book: "Java Programming" (relevant chapters referenced in code comments)
- [JavaFX documentation](https://openjfx.io/)
- [Markdown Guide](https://www.markdownguide.org/basic-syntax/)