package entity;

import java.util.HashSet;
import java.util.Set;

public class Coalition implements Comparable<Coalition> {
  public final Set<Player> players = new HashSet<>();


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

  @Override
  public String toString() {
    return players.toString();
  }



  @Override
  public int compareTo(Coalition coalition) {
    return Integer.compare(players.size(), coalition.players.size());
  }
}
