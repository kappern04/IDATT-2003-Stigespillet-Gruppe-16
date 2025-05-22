module edu.ntnu.iir.bidata {
  requires javafx.controls;
  requires javafx.swing;
  requires java.desktop;
  requires javafx.media;
  requires com.google.gson;
  requires java.logging;

  exports edu.ntnu.iir.bidata;
  exports edu.ntnu.iir.bidata.laddergame.view;
  exports edu.ntnu.iir.bidata.laddergame.controller;
  exports edu.ntnu.iir.bidata.laddergame.util;
  exports edu.ntnu.iir.bidata.laddergame.model;

  opens edu.ntnu.iir.bidata.laddergame.model to com.google.gson;
  opens edu.ntnu.iir.bidata.laddergame.util to com.google.gson;
  exports edu.ntnu.iir.bidata.laddergame.view.board;
  exports edu.ntnu.iir.bidata.laddergame.view.other;
  exports edu.ntnu.iir.bidata.laddergame.view.util;
  exports edu.ntnu.iir.bidata.laddergame.controller.other;
  exports edu.ntnu.iir.bidata.laddergame.controller.board;
  opens edu.ntnu.iir.bidata.laddergame.view.util to com.google.gson;
    exports edu.ntnu.iir.bidata.laddergame.view.menu;
    exports edu.ntnu.iir.bidata.laddergame.controller.menu;
    exports edu.ntnu.iir.bidata.clickgame;
  exports edu.ntnu.iir.bidata.laddergame;
}