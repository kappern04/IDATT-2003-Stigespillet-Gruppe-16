module edu.ntnu.iir.bidata {
  requires javafx.controls;
  requires javafx.swing;
  requires java.desktop;
  requires javafx.media;
  requires com.google.gson;
  requires java.logging;

  exports edu.ntnu.iir.bidata;
  exports edu.ntnu.iir.bidata.laddergame;
  exports edu.ntnu.iir.bidata.laddergame.controller;
  exports edu.ntnu.iir.bidata.laddergame.controller.board;
  exports edu.ntnu.iir.bidata.laddergame.controller.menu;
  exports edu.ntnu.iir.bidata.laddergame.controller.other;
  exports edu.ntnu.iir.bidata.laddergame.model;
  exports edu.ntnu.iir.bidata.laddergame.util;
  exports edu.ntnu.iir.bidata.laddergame.view.board;
  exports edu.ntnu.iir.bidata.laddergame.view.component;
  exports edu.ntnu.iir.bidata.laddergame.view.dialog;
  exports edu.ntnu.iir.bidata.laddergame.view.screen;
  exports edu.ntnu.iir.bidata.laddergame.view.util;

  opens edu.ntnu.iir.bidata.laddergame.model to com.google.gson;
  opens edu.ntnu.iir.bidata.laddergame.util to com.google.gson;
}