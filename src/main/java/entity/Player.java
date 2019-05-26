package entity;

import java.util.*;

public class Player {
  public final int key;
  private final Set<Player> predecessors = new HashSet<>();
  private final Set<Player> children = new HashSet<>();

  public Player(int key) {
    this.key = key;
  }

  public void addPredecessor(Player predecessor) {
    predecessors.add(predecessor);
    predecessor.children.add(this);
  }

  public void removePredecessor(Player predecessor) {
    predecessors.remove(predecessor);
    predecessor.children.remove(this);
  }

  public void removeChild(Player child) {
    children.remove(child);
    child.predecessors.remove(this);
  }

  public boolean isTopPlayer() {
    return predecessors.isEmpty();
  }

  public List<Player> getPredecessors() {
    return new ArrayList<>(predecessors);
  }

  public List<Player> getChildren() {
    return new ArrayList<>(children);
  }

  public boolean containsInPredecessors(Player player) {
    return predecessors.contains(player);
  }

  public static Player[] createPlayers(int n) {
    Player[] players = new Player[n];
    for (int i = 0; i < n; i++) {
      players[i] = new Player(i + 1);
    }
    return players;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return key == player.key;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return String.valueOf(key);
  }
}
