module edu.ntnu.iir.bidata {
  requires javafx.controls;
  requires jdk.compiler;
  requires java.desktop;
  requires javafx.media;
  requires com.google.gson;
  requires java.logging;

  exports edu.ntnu.iir.bidata;
  exports edu.ntnu.iir.bidata.view;
  exports edu.ntnu.iir.bidata.controller;
  exports edu.ntnu.iir.bidata.util;
  exports edu.ntnu.iir.bidata.model;

  opens edu.ntnu.iir.bidata.model to com.google.gson;
  opens edu.ntnu.iir.bidata.util to com.google.gson;
  exports edu.ntnu.iir.bidata.view.board;
  exports edu.ntnu.iir.bidata.view.other;
  exports edu.ntnu.iir.bidata.view.util;
  exports edu.ntnu.iir.bidata.controller.other;
  exports edu.ntnu.iir.bidata.controller.board;
}