package edu.ntnu.iir.bidata.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LadderView {

  public GridPane createLadder(String imagePath, int startX, int startY, int endX, int endY) {
    Image ladderImage = new Image(getClass().getResource(imagePath).toExternalForm());
    ImageView ladderImageView = new ImageView(ladderImage);
    ladderImageView.setFitWidth(50);
    ladderImageView.setFitHeight(100);


    GridPane ladderPane = new GridPane();
    ladderPane.add(ladderImageView, startX, startY, endX, endY);
    return ladderPane;
  }
}