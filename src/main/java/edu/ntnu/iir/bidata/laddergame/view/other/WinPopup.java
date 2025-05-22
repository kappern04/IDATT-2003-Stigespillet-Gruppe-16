package edu.ntnu.iir.bidata.laddergame.view.other;

import edu.ntnu.iir.bidata.laddergame.model.Player;
import edu.ntnu.iir.bidata.laddergame.util.CSS;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class WinPopup extends VBox {
  private Stage popupStage;
  private CSS css;
  private Player winner;

  public WinPopup(Player winner) {
    this.winner = winner;
    this.css = new CSS();
    setupPopup();
  }

  private void setupPopup() {
    initializeStage();
    configureLayout();

    Text title = createAnimatedTitle();
    Text winnerText = createWinnerText();
    Button exitButton = createExitButton();

    getChildren().addAll(title, winnerText, exitButton);

    setBackground(css.createSpaceBackground("/image/background/mainmenu.png"));
    setupScene();
  }

  private void initializeStage() {
    popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initStyle(StageStyle.UNDECORATED);
  }

  private void configureLayout() {
    setSpacing(30);
    setAlignment(Pos.CENTER);
    setPadding(new javafx.geometry.Insets(40));
  }

  private Text createAnimatedTitle() {
    Text title = new Text("MISSION ACCOMPLISHED");
    title.setFont(css.getOrbitronFont(24, FontWeight.BOLD));
    title.setFill(css.getSpaceBlue());

    Glow glow = new Glow(0.8);
    title.setEffect(glow);

    Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(glow.levelProperty(), 0.8))
    );
    pulse.setCycleCount(Animation.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    return title;
  }

  private Text createWinnerText() {
    Text winnerText = new Text("Commander " + winner.getName() + " Wins!");
    winnerText.setFont(css.getOrbitronFont(18, FontWeight.BOLD));
    winnerText.setFill(css.getSpaceBlue());
    return winnerText;
  }

  private Button createExitButton() {
    Button button = css.createSpaceButton("Return Home");
    button.setOnAction(e -> System.exit(0));
    return button;
  }

  private void setupScene() {
    Scene scene = new Scene(this, 400, 400);
    scene.getStylesheets().add(getClass().getResource("/css/space-theme.css").toExternalForm());
    popupStage.setScene(scene);
  }

  public void show() {
    popupStage.show();
  }
}