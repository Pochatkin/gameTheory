package entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Coalition implements Comparable<Coalition> {
  public final List<Player> players = new ArrayList<>();


  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public Player[] getPlayers() {
    return players.toArray(new Player[0]);
  }

  public boolean contains(Player player) {
    return players.contains(player);
  }

  public boolean isEmpty() {
    return players.isEmpty();
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
