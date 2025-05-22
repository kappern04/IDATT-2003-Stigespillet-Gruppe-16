package edu.ntnu.iir.bidata.laddergame.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic observable base class for the observer pattern.
 * @param <T> the type of data being observed
 */
public abstract class Observable<T> {
  private final List<Observer<T>> observers = new ArrayList<>();

  public void addObserver(Observer<T> observer) {
    if (observer == null) throw new IllegalArgumentException("Observer cannot be null");
    if (!observers.contains(observer)) observers.add(observer);
  }

  public void removeObserver(Observer<T> observer) {
    observers.remove(observer);
  }

  public void notifyObservers() {
    notifyObservers("UPDATE");
  }

  public void notifyObservers(String eventType) {
    for (Observer<T> observer : observers) {
      observer.update(this, eventType);
    }
  }

  public List<Observer<T>> getObservers() {
    return observers;
  }
}