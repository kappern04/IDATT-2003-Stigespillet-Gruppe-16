package edu.ntnu.iir.bidata.laddergame.util;

public interface Observer<T> {
  void update(Observable<T> observable, String eventType);
}