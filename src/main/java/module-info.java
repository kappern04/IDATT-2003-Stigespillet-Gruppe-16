module edu.ntnu.iir.bidata {
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.base;
  requires jdk.compiler;
  requires java.desktop;

  exports edu.ntnu.iir.bidata;
  exports edu.ntnu.iir.bidata.view;
  exports edu.ntnu.iir.bidata.object;
  exports edu.ntnu.iir.bidata.controller;
}