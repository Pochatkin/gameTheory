package entity;

import java.util.*;

public class Game {
  public final Player[] players;
  public final Map<Coalition, Float> coalitionValue = new HashMap<>();

  public Game(Player[] players) {
    this.players = players;
  }

  public void putCoalition(Coalition coalition, float value) {
    coalitionValue.put(coalition, value);
  }

  public Float getCoalitionValue(Coalition coalition) {
    return coalitionValue.get(coalition);
  }

  public Set<Coalition> getCoalitions() {
    return coalitionValue.keySet();
  }

  @Override
  public String toString() {
    return "Game{" +
        "players=" + Arrays.toString(players) +
        ", coalitions=" + coalitionValue +
        '}';
  }
}
