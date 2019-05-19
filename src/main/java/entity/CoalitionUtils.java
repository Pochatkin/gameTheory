package entity;

import java.util.*;

public class CoalitionUtils {
  public static List<Coalition> createCoalitions(Player[] players) {
    List<Coalition> coalitions = new ArrayList<>();
    for (int i = 1; i < Math.pow(2, players.length); i++) {
      createCoalition(i, players, coalitions);
    }
    return coalitions;
  }

  private static void createCoalition(int n, Player[] players, List<Coalition> result) {
    Coalition coalition = new Coalition();
    for (int i = 0; i < players.length; i++) {
      if (((n >> i) & 1) == 1) {
        coalition.addPlayer(players[i]);
      }
    }
    result.add(coalition);
  }
}
