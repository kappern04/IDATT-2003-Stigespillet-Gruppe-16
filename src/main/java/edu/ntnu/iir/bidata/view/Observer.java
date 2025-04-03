package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.object.Observable;

public interface Observer {
  <T extends Observer> void update(Observable<T> observable, String prompt);
}
