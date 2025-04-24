package edu.ntnu.iir.bidata.util;

public interface Observer {
  <T extends Observer> void update();
}
