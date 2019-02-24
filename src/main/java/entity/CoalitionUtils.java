package entity;

import java.util.*;

public class CoalitionUtils {
  public static List<Coalition> createCoalitions(Player[] players) {
    List<Coalition> coalitions = new ArrayList<>();
    for (int i = 0; i < Math.pow(2, players.length); i++) {
      createCoalition(i, players, coalitions);
    }
    return coalitions;
  }

  private static void createCoalition(int n, Player[] players, List<Coalition> result) {
    Coalition coalition = new Coalition();
    for (int i = 0; i < players.length; i++) {
      if (((n>>i)&1) == 1) {
        coalition.addPlayer(players[i]);
      }
    }
    result.add(coalition);
  }

  private static List<Coalition> computeDisjunctiveCoalitions(Game game) {
    List<Coalition> result = new ArrayList<>();
    for (Coalition coalition : game.getCoalitions()) {
      boolean isDis = true;
      for (Player player : coalition.players) {
        Player[] predecessors = player.getPredecessors();
        if (!containsOne(coalition, predecessors)) {
          isDis = false;
        }
      }
      if (isDis) {
        result.add(coalition);
      }
    }
    return result;
  }

  private static boolean containsOne(Coalition coalition, Player[] players) {
    if (players.length == 0) return true;
    for (Player player : players) {
      if (coalition.contains(player)) return true;
    }
    return false;
  }

  private static boolean containsAll(Coalition coalition, Player[] players) {
    if (players.length == 0) return true;
    for (Player player : players) {
      if (!coalition.contains(player)) return false;
    }
    return true;
  }

  public static List<Coalition> computeConjunctiveCoalitions(Game game) {
    List<Coalition> result = new ArrayList<>();
    for (Coalition coalition : game.getCoalitions()) {
      boolean isCon = true;
      for (Player player : coalition.players) {
        Player[] predecessors = player.getPredecessors();
        if (!containsAll(coalition, predecessors)) {
          isCon = false;
        }
      }
      if (isCon) {
        result.add(coalition);
      }
    }
    return result;
  }

  public static Game computeDisjunctiveGame(Game game) {
    List<Coalition> disjunctiveCoalitions = computeDisjunctiveCoalitions(game);
    Map<Coalition, Coalition> coalitionMap = new HashMap<>();
    for (Coalition coalition : game.coalitionValue.keySet()) {
      coalitionMap.put(coalition, findBestCompareCoalition(coalition, disjunctiveCoalitions));
    }
    Game disjunctiveGame = new Game(Arrays.copyOf(game.players, game.players.length));
    for (Coalition coalition :  coalitionMap.keySet()) {
      Float value = game.getCoalitionValue(coalitionMap.get(coalition));
      disjunctiveGame.putCoalition(coalition, value);
    }
    return disjunctiveGame;
  }

  public static Game computeConjunctiveGame(Game game) {
    List<Coalition> conjunctiveCoalitions = computeConjunctiveCoalitions(game);
    Map<Coalition, Coalition> coalitionMap = new HashMap<>();
    for (Coalition coalition : game.coalitionValue.keySet()) {
      coalitionMap.put(coalition, findBestCompareCoalition(coalition, conjunctiveCoalitions));
    }
    Game disjunctiveGame = new Game(Arrays.copyOf(game.players, game.players.length));
    for (Coalition coalition :  coalitionMap.keySet()) {
      Float value = game.getCoalitionValue(coalitionMap.get(coalition));
      disjunctiveGame.putCoalition(coalition, value);
    }
    return disjunctiveGame;
  }

  private static Coalition findBestCompareCoalition(Coalition coalition, List<Coalition> set) {
    Coalition best = new Coalition();
    int bestScore = -1;
    for (Coalition element : set) {
      int score = score(coalition, element);
      if (score > bestScore) {
        best = element;
        bestScore = score;
      }
    }
    return best;
  }

  private static int score(Coalition rootCoalition, Coalition mappedCoalition) {
    Player[] mappedPlayers = mappedCoalition.getPlayers();
    Player[] rootPlayers = rootCoalition.getPlayers();
    if (mappedPlayers.length > rootPlayers.length) {
      return -1;
    } else {
      if (containsAll(rootCoalition, mappedPlayers)) {
        return mappedPlayers.length;
      }
      return -1;
    }
  }

}
