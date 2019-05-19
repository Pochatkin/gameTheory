package entity;

import java.util.*;

public class Game {
  public Player[] players;
  public final Map<Coalition, Float> coalitionsValue = new HashMap<>();
  private Coalition coalitionWithAllPlayers;
  private float balancedParam;

  public Game(Player[] players) {
    this.players = players;
  }

  public void putCoalition(Coalition coalition, float value) {
    coalitionsValue.put(coalition, value);
    if (coalition.players.size() == players.length) {
      coalitionWithAllPlayers = coalition;
    }
  }

  public void setBalancedParam(float balancedParam) {
    this.balancedParam = balancedParam;
  }

  public Game computeBalancedGame() {
    Game dualGame = computeDualGame();
    Game balancedGame = new Game(Arrays.copyOf(players, players.length));
    for (Coalition coalition : coalitionsValue.keySet()) {
      float coalitionValue = balancedParam * coalitionsValue.get(coalition)
              + (1 - balancedParam) * dualGame.coalitionsValue.get(coalition);
      balancedGame.putCoalition(coalition, coalitionValue);
    }
    return balancedGame;
  }

  public Game computeDualGame() {
    Game dualGame = new Game(Arrays.copyOf(players, players.length));
    for (Coalition coalition : coalitionsValue.keySet()) {
      dualGame.putCoalition(coalition, computeDualValue(coalition));
    }
    return dualGame;
  }

  private float computeDualValue(Coalition coalition) {
    for (Coalition coalition1 : coalitionsValue.keySet()) {
      if (isDual(coalition, coalition1)) {
        return coalitionsValue.get(coalitionWithAllPlayers) - coalitionsValue.get(coalition1);
      }
    }
    return 0;
  }

  private boolean isDual(Coalition coalition, Coalition coalition1) {
    Player[] players = coalition.getPlayers();
    Player[] players1 = coalition1.getPlayers();
    if (players.length + players1.length != this.players.length) return false;

    for (Player player : players) {
      for (Player player1 : players1) {
        if (player == player1) {
          return false;
        }
      }
    }
    return true;
  }

  public Game computeConjunctiveGame() {
    List<Coalition> conjunctiveCoalitions = computeConjunctiveCoalitions();
    Map<Coalition, Coalition> coalitionMap = new HashMap<>();
    for (Coalition coalition : coalitionsValue.keySet()) {
      Coalition bestCompareCoalition = findBestCompareCoalition(coalition, conjunctiveCoalitions);
      coalitionMap.put(coalition, bestCompareCoalition);
    }
    Game disjunctiveGame = new Game(Arrays.copyOf(players, players.length));
    for (Coalition coalition : coalitionMap.keySet()) {
      Coalition key = coalitionMap.get(coalition);
      Float value = key.isEmpty() ? 0f : coalitionsValue.get(key);
      disjunctiveGame.putCoalition(coalition, value);
    }
    return disjunctiveGame;
  }

  private List<Coalition> computeConjunctiveCoalitions() {
    List<Coalition> result = new ArrayList<>();
    for (Coalition coalition : coalitionsValue.keySet()) {
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

  private List<Coalition> computeDisjunctiveCoalitions() {
    List<Coalition> result = new ArrayList<>();
    for (Coalition coalition : coalitionsValue.keySet()) {
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

  public Game computeDisjunctiveGame() {
    List<Coalition> disjunctiveCoalitions = computeDisjunctiveCoalitions();
    Map<Coalition, Coalition> coalitionMap = new HashMap<>();
    for (Coalition coalition : coalitionsValue.keySet()) {
      coalitionMap.put(coalition, findBestCompareCoalition(coalition, disjunctiveCoalitions));
    }
    Game disjunctiveGame = new Game(Arrays.copyOf(players, players.length));
    for (Coalition coalition : coalitionMap.keySet()) {
      Coalition key = coalitionMap.get(coalition);
      Float value = key.isEmpty() ? 0f : coalitionsValue.get(key);
      disjunctiveGame.putCoalition(coalition, value);
    }
    return disjunctiveGame;
  }

  private Coalition findBestCompareCoalition(Coalition coalition, List<Coalition> set) {
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

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    List<Coalition> copy = new ArrayList<>(coalitionsValue.keySet());
    Collections.sort(copy);

    for (Coalition coalition : copy) {
      stringBuilder.append(coalition).append(": ").append(coalitionsValue.get(coalition)).append("\n");
    }
    return stringBuilder.toString();
  }
}
