package edu.ntnu.iir.bidata.view.elements;

import java.io.InputStream;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class CSS {
  private Font orbitronFont;
  private final Color SPACE_BLUE = Color.rgb(64, 224, 208);
  private final Color SPACE_PURPLE = Color.rgb(138, 43, 226);

  public final Color PLAYER_1_COLOR = Color.rgb(7, 239, 0);
  public final Color PLAYER_2_COLOR = Color.rgb(112, 0, 239);
  public final Color PLAYER_3_COLOR = Color.rgb(0, 174, 239);
  public final Color PLAYER_4_COLOR = Color.rgb(239, 128, 0);


  public CSS() {
    loadCustomFont();
  }

  public void loadCustomFont() {
    try {
      InputStream is = getClass().getResourceAsStream("/font/Orbitron-VariableFont_wght.ttf");
      orbitronFont = Font.loadFont(is, 12); // Load with a default size
      if (orbitronFont == null) {
        System.err.println("Failed to load Orbitron font. Using system font.");
        orbitronFont = Font.font("Arial"); // Fallback
      } else {
        System.out.println("Orbitron font loaded successfully!");
      }
    } catch (Exception e) {
      System.err.println("Error loading font: " + e.getMessage());
      orbitronFont = Font.font("Arial"); // Fallback
    }
  }

  public Font getOrbitronFont(double size, FontWeight weight) {
    return Font.font(orbitronFont.getFamily(), weight, size);
  }

  public Button createSpaceButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(250);
    button.setPrefHeight(50);
    button.setFont(getOrbitronFont(16, FontWeight.BOLD));
    button.setTextFill(Color.WHITE);
    button.setFocusTraversable(Boolean.FALSE);

    // Apply CSS styling
    button.getStyleClass().add("space-button");

    // Add glow effect
    DropShadow shadow = new DropShadow();
    shadow.setColor(SPACE_BLUE);
    button.setEffect(shadow);

    // Add hover shake animation
    button.setOnMouseEntered(e -> {
      // Create shaking animation
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
      shake.setFromX(-3);
      shake.setToX(3);
      shake.setCycleCount(6);
      shake.setAutoReverse(true);
      shake.play();

      // Increase glow effect
      shadow.setRadius(20);
      shadow.setColor(SPACE_PURPLE);
    });

    button.setOnMouseExited(e -> {
      // Reset effects
      shadow.setRadius(10);
      shadow.setColor(SPACE_BLUE);
    });

    return button;
  }

  public Color getSpaceBlue() {
    return SPACE_BLUE;
  }

  public Color getSpacePurple() {
    return SPACE_PURPLE;
  }

  // Add to CSS.java
  public Background createSpaceBackground(String imagePath) {
    try {
      Image image = new Image(getClass().getResourceAsStream(imagePath));
      BackgroundImage bgImage = new BackgroundImage(
          image,
          BackgroundRepeat.NO_REPEAT,
          BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
      );
      return new Background(bgImage);
    } catch (Exception e) {
      System.err.println("Could not load background image: " + imagePath);
      return null;
    }
  }
}