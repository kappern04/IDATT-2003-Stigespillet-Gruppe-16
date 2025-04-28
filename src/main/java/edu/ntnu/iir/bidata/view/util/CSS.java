package edu.ntnu.iir.bidata.view.util;

import java.io.InputStream;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.Scene;
import javafx.util.Duration;

public class CSS {
  private Font orbitronFont;
  private static final String DEFAULT_CSS_PATH = "/css/space-theme.css";
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

  /**
   * Get Orbitron font with specified size and weight.
   *
   * @param size font size
   * @param weight font weight
   * @return the configured font
   */
  public Font getOrbitronFont(double size, FontWeight weight) {
    return Font.font("Orbitron", weight, size);
  }

  public Color getSpaceBlue() {
    return SPACE_BLUE;
  }

  public Color getSpacePurple() {
    return SPACE_PURPLE;
  }

  /**
   * Creates a styled label with custom font, weight, size and color.
   *
   * @param text the text to display
   * @param weight the font weight (bold, normal, etc.)
   * @param size the font size
   * @param color the text color
   * @return styled Label
   */
  public Label createStyledLabel(String text, FontWeight weight, double size, Color color) {
    Label label = new Label(text);
    label.setFont(getOrbitronFont(size, weight));
    label.setTextFill(color);
    return label;
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

  public Background createSpaceBackground(String imagePath) {
    try {
      Image image = new Image(getClass().getResourceAsStream(imagePath));
      BackgroundImage bgImage = new BackgroundImage(
              image,
              BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT,
              BackgroundPosition.CENTER,
              new BackgroundSize(100, 100, true, true, true, true)
      );
      return new Background(bgImage);
    } catch (Exception e) {
      System.err.println("Failed to load background image: " + e.getMessage());
      return Background.EMPTY;
    }
  }

  public TranslateTransition createHoverAnimation(Button button) {
    TranslateTransition hover = new TranslateTransition(Duration.millis(200), button);
    hover.setByY(-5);
    return hover;
  }

  /**
   * Applies the default space theme CSS to a scene.
   *
   * @param scene The scene to apply CSS to
   */
  public void applyDefaultStylesheet(Scene scene) {
    scene.getStylesheets().add(getClass().getResource(DEFAULT_CSS_PATH).toExternalForm());
  }

  /**
   * Applies a custom CSS stylesheet to a scene.
   *
   * @param scene The scene to apply CSS to
   * @param cssPath The path to the CSS resource
   */
  public void applyStylesheet(Scene scene, String cssPath) {
    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
  }
}