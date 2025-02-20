package edu.ntnu.iir.bidata.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LadderView {

  public StackPane createLadder(String imagePath) {
    Image ladderImage = new Image(imagePath);
    ImageView ladderImageView = new ImageView(ladderImage);
    StackPane ladderPane = new StackPane();
    ladderPane.getChildren().add(ladderImageView);
    return ladderPane;
  }
}