package edu.ntnu.iir.bidata.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LadderView {

  public StackPane createLadder(String imagePath, double startX, double startY, double endX, double endY) {
    Image ladderImage = new Image(getClass().getResource(imagePath).toExternalForm());
    ImageView ladderImageView = new ImageView(ladderImage);
    ladderImageView.setFitWidth(Math.abs(startX - endX));
    ladderImageView.setFitHeight(Math.abs(-startY + endY));


    StackPane ladderPane = new StackPane();
    ladderPane.getChildren().add(ladderImageView);
    ladderPane.setLayoutX(startX);
    ladderPane.setLayoutY(startY);
    return ladderPane;
  }
}
