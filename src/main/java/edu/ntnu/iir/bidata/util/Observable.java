package edu.ntnu.iir.bidata.util;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable<T extends Observer> {
  private final List<T> observers;

  public Observable() {
    this.observers = new ArrayList<>();
  }

  public abstract void addObserver(T observer);
  public abstract void removeObserver(T observer);
  public abstract void notifyObservers();
  public List<T> getObservers() {
    return observers;
  }
}
