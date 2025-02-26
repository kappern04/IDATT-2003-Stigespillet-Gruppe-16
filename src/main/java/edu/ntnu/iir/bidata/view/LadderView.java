package edu.ntnu.iir.bidata.view;

import static javafx.geometry.Pos.TOP_LEFT;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LadderView {

  public StackPane createLadder(String imagePath) {
    Image ladderImage = new Image(getClass().getResource(imagePath).toExternalForm());
    ImageView ladderImageView = new ImageView(ladderImage);
    ladderImageView.setFitWidth(50);
    ladderImageView.setFitHeight(200);


    StackPane ladderPane = new StackPane();
    ladderPane.getChildren().add(ladderImageView);
    ladderPane.setAlignment(ladderImageView, TOP_LEFT);
    return ladderPane;
  }
}