package edu.ntnu.iir.bidata.view.util;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

/**
 * Utility class for applying consistent CSS and style elements across the application.
 */
public class CSS {
  private static final Logger LOGGER = Logger.getLogger(CSS.class.getName());
  private static final String DEFAULT_CSS_PATH = "/css/space-theme.css";
  private static final String ORBITRON_FONT_PATH = "/font/orbitron-bold.otf";
  private static final Color SPACE_BLUE = Color.rgb(64, 224, 208);
  private static final Color SPACE_PURPLE = Color.rgb(138, 43, 226);

  private static Font orbitronFont;

  public CSS() {
    loadCustomFont();
  }

  /**
   * Loads the Orbitron font from resources, if not already loaded.
   */
  public final void loadCustomFont() {
    if (orbitronFont != null) return;
    try (InputStream is = getClass().getResourceAsStream(ORBITRON_FONT_PATH)) {
      if (is == null) {
        LOGGER.warning("Orbitron font resource not found. Using system font.");
        orbitronFont = Font.font("Arial");
        return;
      }
      orbitronFont = Font.loadFont(is, 12);
      if (orbitronFont == null) {
        LOGGER.warning("Failed to load Orbitron font. Using system font.");
        orbitronFont = Font.font("Arial");
      } else {
        LOGGER.info("Orbitron font loaded successfully!");
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading font: " + e.getMessage(), e);
      orbitronFont = Font.font("Arial");
    }
  }

  /**
   * Returns the Orbitron font with the given size and weight.
   */
  public Font getOrbitronFont(double size, FontWeight weight) {
    loadCustomFont();
    return orbitronFont != null
            ? Font.font(orbitronFont.getFamily(), weight, size)
            : Font.font("Arial", weight, size);
  }

  public Color getSpaceBlue() {
    return SPACE_BLUE;
  }

  public Color getSpacePurple() {
    return SPACE_PURPLE;
  }

  /**
   * Creates a styled label with the Orbitron font and specified color.
   */
  public Label createStyledLabel(String text, FontWeight weight, double size, Color color) {
    Label label = new Label(text);
    label.setFont(getOrbitronFont(size, weight));
    label.setTextFill(color);
    label.setStyle("-fx-padding: 5; -fx-background-radius: 10px;");
    return label;
  }

  /**
   * Creates a label for the side panel with background color based on contrast.
   */
  public Label sidePanelLabel(String text, double size, Color color) {
    Label label = new Label(text);
    label.setFont(getOrbitronFont(size, FontWeight.NORMAL));
    label.setTextFill(color);
    label.getStyleClass().add("side-panel-label");
    String bgColor = isHighContrastBlack(color)
            ? "rgba(0, 0, 0, 0.7);"
            : "rgba(255, 255, 255, 0.7);";
    label.setStyle("-fx-background-color: " + bgColor);
    return label;
  }

  /**
   * Determines if the color is high-contrast (light).
   */
  public static boolean isHighContrastBlack(Color color) {
    double luminance = 0.299 * color.getRed() +
            0.587 * color.getGreen() +
            0.114 * color.getBlue();
    return luminance > 0.5;
  }

  /**
   * Creates a styled button with a shake animation on hover.
   */
  public Button createSpaceButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("space-button");
    button.setFocusTraversable(false);

    button.setOnMouseEntered(e -> {
      TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
      shake.setFromX(-3);
      shake.setToX(3);
      shake.setCycleCount(6);
      shake.setAutoReverse(true);
      shake.play();
    });

    return button;
  }

  /**
   * Creates a background from the given image path.
   */
  public Background createSpaceBackground(String imagePath) {
    try (InputStream is = getClass().getResourceAsStream(imagePath)) {
      if (is == null) {
        LOGGER.warning("Background image not found: " + imagePath);
        return Background.EMPTY;
      }
      Image image = new Image(is);
      BackgroundImage bgImage = new BackgroundImage(
              image,
              BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT,
              BackgroundPosition.CENTER,
              new BackgroundSize(100, 100, true, true, true, true)
      );
      return new Background(bgImage);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to load background image: " + e.getMessage(), e);
      return Background.EMPTY;
    }
  }

  /**
   * Creates a hover animation for a button.
   */
  public static TranslateTransition createHoverAnimation(Button button) {
    TranslateTransition hover = new TranslateTransition(Duration.millis(200), button);
    hover.setByY(-5);
    return hover;
  }

  /**
   * Applies the default stylesheet to the given scene.
   */
  public void applyDefaultStylesheet(Scene scene) {
    String css = getClass().getResource(DEFAULT_CSS_PATH).toExternalForm();
    if (!scene.getStylesheets().contains(css)) {
      scene.getStylesheets().add(css);
    }
  }

  /**
   * Applies a custom stylesheet to the given scene.
   */
  public void applyStylesheet(Scene scene, String cssPath) {
    String css = getClass().getResource(cssPath).toExternalForm();
    if (!scene.getStylesheets().contains(css)) {
      scene.getStylesheets().add(css);
    }
  }
}