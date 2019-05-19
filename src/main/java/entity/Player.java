package entity;

import java.util.HashSet;
import java.util.Set;

public class Player {
  public final int key;
  private final Set<Player> predecessors = new HashSet<>();

  public Player(int key) {
    this.key = key;
  }

  public void addPredecessor(Player predecessor) {
    predecessors.add(predecessor);
  }

  public void removePredecessor(Player predecessor) {
    predecessors.remove(predecessor);
  }

  public boolean isTopPlayer() {
    return predecessors.isEmpty();
  }

  public Player[] getPredecessors() {
    return predecessors.toArray(new Player[0]);
  }

  public static Player[] createPlayers(int n) {
    Player[] players = new Player[n];
    for (int i = 0; i < n; i++) {
      players[i] = new Player(i + 1);
    }
    return players;
  }

  @Override
  public String toString() {
    return String.valueOf(key);
  }
}
