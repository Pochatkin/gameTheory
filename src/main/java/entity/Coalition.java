package entity;

import java.util.*;

public class Coalition implements Comparable<Coalition> {
  public final List<Player> players = new ArrayList<>();


  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  public boolean contains(Player player) {
    return players.contains(player);
  }

  public boolean contains(List<Player> players) {
    for (Player player : players) {
      if (!contains(player)) {
        return false;
      }
    }
    return true;
  }

  public boolean containsIn(List<Player> players) {
    for (Player player : this.players) {
      if (!players.contains(player)) {
        return false;
      }
    }
    return true;
  }

  public boolean strongContains(List<Player> players) {
    if (this.players.size() != players.size()) return false;

    return contains(players);
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public List<Player> computeDiff(Coalition coalition) {
    List<Player> result = new ArrayList<>();
    for (Player player : this.players) {
      if (!coalition.players.contains(player)) {
        result.add(player);
      }
    }
    return result;
  }

  public String getTitle() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
//    Collections.sort(players);
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      builder.append(player.key);
      if (i != players.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append("}");
    return builder.toString();
  }

  @Override
  public String toString() {
    return players.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coalition coalition = (Coalition) o;
    return Objects.equals(players, coalition.players);
  }

  @Override
  public int hashCode() {
    return Objects.hash(players);
  }

  @Override
  public int compareTo(Coalition coalition) {
    int compare = Integer.compare(players.size(), coalition.players.size());
    if (compare == 0) {
     for (int i = 0; i < players.size(); i++) {
       int firstKey = players.get(i).key;
       int secondKey = coalition.players.get(i).key;
       if (firstKey != secondKey) {
         return Integer.compare(firstKey, secondKey);
       }
     }
    }
    return compare;
  }
}
