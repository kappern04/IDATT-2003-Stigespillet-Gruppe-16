package edu.ntnu.iir.bidata.view.util;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
    label.getStyleClass().add("styled-label");
    label.setFont(getOrbitronFont(size, weight));
    label.setTextFill(color);
    label.setStyle(
            "-fx-border-color: " + toRgbaString(color, 0.5) + ";"
    );
    return label;
  }

  /**
   * Creates a label for the side panel with background color based on contrast.
   */
  public Label sidePanelLabel(String text, Color color) {
    Label label = new Label(text);
    label.getStyleClass().add("side-panel-label");
    label.setTextFill(color);
    String bgColor = isHighContrastBlack(color)
            ? "rgba(0, 0, 0, 0.7);"
            : "rgba(255, 255, 255, 0.7);";
    label.setStyle(
            "-fx-background-color: " + bgColor + ";" +
                    "-fx-border-color: " + toRgbaString(color, 0.3) + ";"
    );
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
   * Creates a styled button with a scale animation on hover.
   */
  public Button createSpaceButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("space-button");
    button.setFont(getOrbitronFont(16, FontWeight.BOLD));

    // Add scale grow effect on hover
    ScaleTransition growEffect = new ScaleTransition(Duration.millis(150), button);
    growEffect.setToX(1.08);
    growEffect.setToY(1.08);

    button.setOnMouseEntered(e -> growEffect.playFromStart());
    button.setOnMouseExited(e -> {
      growEffect.stop();
      button.setScaleX(1.0);
      button.setScaleY(1.0);
    });

    return button;
  }

  /**
   * Adds a tooltip to any JavaFX node with custom styling.
   */
  public Tooltip createTooltip(String text) {
    Tooltip tooltip = new Tooltip(text);
    tooltip.setShowDelay(Duration.millis(500));
    tooltip.getStyleClass().add("space-tooltip");
    return tooltip;
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
   * Applies the default stylesheet to the given scene.
   */
  public void applyDefaultStylesheet(Scene scene) {
    String css = getClass().getResource(DEFAULT_CSS_PATH).toExternalForm();
    if (!scene.getStylesheets().contains(css)) {
      scene.getStylesheets().add(css);
    }
  }


  /**
   * Helper to convert a Color to an rgba() string with alpha.
   */
  private String toRgbaString(Color color, double alpha) {
    int r = (int) Math.round(color.getRed() * 255);
    int g = (int) Math.round(color.getGreen() * 255);
    int b = (int) Math.round(color.getBlue() * 255);
    return "rgba(" + r + "," + g + "," + b + "," + alpha + ")";
  }
}