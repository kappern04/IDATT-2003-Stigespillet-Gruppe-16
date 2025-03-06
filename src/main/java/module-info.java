module edu.ntnu.iir.bidata {
  requires javafx.controls;
  requires jdk.compiler;
  requires java.desktop;
  requires javafx.media;

  exports edu.ntnu.iir.bidata;
  exports edu.ntnu.iir.bidata.view;
  exports edu.ntnu.iir.bidata.object;
  exports edu.ntnu.iir.bidata.controller;
}