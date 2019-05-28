package entity;

import java.util.*;

public class Game {
  public Player[] players;
  public final Map<Coalition, Float> coalitionsValue = new HashMap<>();
  public Coalition coalitionWithAllPlayers;
  private float balancedParam;

  public Game(Player[] players) {
    this.players = players;
  }

  public Game copy() {
    Player[] players = new Player[this.players.length];
    for (int i = 0; i < players.length; i++) {
      players[i] = this.players[i].copy();
    }
    Game result = new Game(players);
    Map<Integer, Player> copyPlayersMap = new HashMap<>();
    for (Player player : players) {
      copyPlayersMap.put(player.key, player);
    }
    for (Coalition coalition : coalitionsValue.keySet()) {
      result.putCoalition(coalition.copy(copyPlayersMap), coalitionsValue.get(coalition));
    }
    result.coalitionWithAllPlayers = coalitionWithAllPlayers.copy(copyPlayersMap);
    result.balancedParam = balancedParam;
    return result;
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
    Coalition dualCoalition = computeDualCoalition(coalition);
    if (dualCoalition == null) {
      return coalitionsValue.get(coalition);
    }
    return coalitionsValue.get(coalitionWithAllPlayers) - coalitionsValue.get(dualCoalition);
  }

  private Coalition computeDualCoalition(Coalition coalition) {
    for (Coalition coalition1 : coalitionsValue.keySet()) {
      if (isDual(coalition, coalition1)) {
        return coalition1;
      }
    }
    return null;
  }

  private boolean isDual(Coalition coalition, Coalition coalition1) {
    List<Player> players = coalition.getPlayers();
    List<Player> players1 = coalition1.getPlayers();
    if (players.size() + players1.size() != this.players.length) return false;

    for (Player player : players) {
      for (Player player1 : players1) {
        if (player == player1) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean checkMonotonic() {
//    System.out.println("values: " + coalitionsValue);
    for (Coalition coalition : coalitionsValue.keySet()) {
      if (coalition == coalitionWithAllPlayers) continue;
      if (coalitionsValue.get(coalitionWithAllPlayers) < coalitionsValue.get(coalition)) {
        System.out.println("all players: " + coalitionWithAllPlayers);
        System.out.println("value: " + coalitionsValue.get(coalitionWithAllPlayers));
        System.out.println("coalition: " + coalition);
        System.out.println("value: " + coalitionsValue.get(coalition));
        return false;
      }
    }
    return true;
  }

  public boolean checkConcave() {
    for (Coalition coalition : coalitionsValue.keySet()) {
      for (Coalition coalition1 : coalitionsValue.keySet()) {
        if (coalition == coalition1) continue;

        if (!isFullPlayersSet(coalition1.getPlayers(), coalition.getPlayers())) continue;

        Coalition intersection = computeIntersection(coalition1, coalition);
        float intersectionValue = intersection == null ? 0 : coalitionsValue.get(intersection);


        boolean b = coalitionsValue.get(coalition) + coalitionsValue.get(coalition1)
                >= coalitionsValue.get(coalitionWithAllPlayers) + intersectionValue;
        if (!b) {
          return false;
        }
      }
    }
    return true;
  }

  private Coalition computeIntersection(Coalition coalition1, Coalition coalition2) {
    List<Player> players1 = coalition1.getPlayers();
    List<Player> players2 = coalition2.getPlayers();
    Coalition intersection = new Coalition();
    for (Player player1 : players1) {
      for (Player player2 : players2) {
        if (player1 == player2) {
          intersection.addPlayer(player1);
        }
      }
    }
    for (Coalition coalition : coalitionsValue.keySet()) {
      if (coalition.equals(intersection)) {
        return coalition;
      }
    }
    return null;
  }

  private boolean isFullPlayersSet(List<Player> players1, List<Player> players2) {
    forI:
    for (Player player : players) {
      for (Player player1 : players1) {
        if (player == player1) {
          continue forI;
        }
      }
      for (Player player2 : players2) {
        if (player == player2) {
          continue forI;
        }
      }
      return false;
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
        List<Player> predecessors = player.getPredecessors();
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

  public List<Coalition> computeDisjunctiveCoalitions() {
    List<Coalition> result = new ArrayList<>();
    for (Coalition coalition : coalitionsValue.keySet()) {
      boolean isDis = true;
      for (Player player : coalition.players) {
        List<Player> predecessors = player.getPredecessors();
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

  public static Map<Player, Float> computeDivision(Game game, Coalition bestCoalition, List<Coalition> set, Map<Player, Float> result) {
    Pair<Coalition, Float> newBestCoalition = findBestCoalition(game, set, bestCoalition);
    List<Player> outPlayers = bestCoalition.computeDiff(newBestCoalition.first);
    for (Player player : outPlayers) {
      result.put(player, newBestCoalition.second);
    }
    if (newBestCoalition.first.players.size() == 1 && newBestCoalition.first.players.get(0).key == 1) {
      return result;
    }
    Player[] newPlayers = new Player[game.players.length - outPlayers.size()];
    int i = 0;
    loop:
    for (Player player : game.players) {
      for (Player outPlayer : outPlayers) {
        if (player == outPlayer) {
          continue loop;
        }
      }
      newPlayers[i] = player;
      i++;
    }
    Player outPlayersRoot = outPlayers.get(0);
    for (Player outPlayer : outPlayers) {
      boolean find = true;
      for (Player outPlayer1 : outPlayers) {
        if (outPlayer.containsInPredecessors(outPlayer1)) {
          find = false;
        }
      }
      if (find) {
        outPlayersRoot = outPlayer;
        break;
      }
    }
    List<Player> outRootPredecessors = outPlayersRoot.getPredecessors();
    Set<Player> allChildren = new HashSet<>();
    for (Player outPlayer : outPlayers) {
      List<Player> children = outPlayer.getChildren();
      for (Player child : children) {
        if (!outPlayers.contains(child)) {
          allChildren.addAll(children);
        }
      }
    }
    for (Player player : newPlayers) {
      for (Player outPlayer : outPlayers) {
        player.removePredecessor(outPlayer);
        player.removeChild(outPlayer);
      }
      if (outRootPredecessors.contains(player)) {
        for (Player child : allChildren) {
          child.addPredecessor(player);
        }
      }
    }

    Game newGame = new Game(newPlayers);
    for (Coalition coalition : game.coalitionsValue.keySet()) {
      if (coalition.contains(outPlayers)) continue;

      if (coalition.contains(outRootPredecessors) || coalition.containsIn(outRootPredecessors)) {
        Coalition coalition1 = findCoalition(game, coalition, bestCoalition, newBestCoalition.first);
        Float ul = game.coalitionsValue.get(coalition1);
        Float ur = newBestCoalition.second;
        int down = bestCoalition.players.size() - newBestCoalition.first.players.size();
        float value = ul - ur * down;
        newGame.coalitionsValue.put(coalition, value);
      } else {
        Float value = game.coalitionsValue.get(coalition);
        newGame.coalitionsValue.put(coalition, value);
      }
    }

    return computeDivision(newGame.computeDisjunctiveGame(), newBestCoalition.first, newGame.computeDisjunctiveCoalitions(), result);
  }

  private static Coalition findCoalition(Game game, Coalition initCoalition, Coalition bestCoalition, Coalition newBestCoalition) {
    List<Player> players = bestCoalition.computeDiff(newBestCoalition);
    players.addAll(initCoalition.players);
    for (Coalition coalition : game.coalitionsValue.keySet()) {
      if (coalition.strongContains(players)) {
        return coalition;
      }
    }
    return null;
  }

  private static Pair<Coalition, Float> findBestCoalition(Game game, List<Coalition> set, Coalition bestCoalition) {
    Coalition newBestCoalition = null;
    float bestValue = Float.MAX_VALUE;
    for (Coalition coalition : set) {
      if (coalition.players.size() == game.players.length) continue;




      float tau = tau(game, bestCoalition, coalition);
      if (tau < bestValue) {
        newBestCoalition = coalition;
        bestValue = tau;
      } else if (tau == bestValue) {
        if (newBestCoalition == null) {
          newBestCoalition = coalition;
          bestValue = tau;
        } else if (coalition.players.size() > bestCoalition.players.size()) {
          newBestCoalition = coalition;
          bestValue = tau;
        }
      }
    }
    return new Pair<>(newBestCoalition, bestValue);
  }


  private static float tau(Game game, Coalition bestCoalition, Coalition testCoalition) {
    Float best = game.coalitionsValue.get(bestCoalition);
    Float test = game.coalitionsValue.get(testCoalition);

    return (best - test)
            / (bestCoalition.players.size() - testCoalition.players.size() + 1);
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
    List<Player> mappedPlayers = mappedCoalition.getPlayers();
    List<Player> rootPlayers = rootCoalition.getPlayers();
    if (mappedPlayers.size() > rootPlayers.size()) {
      return -1;
    } else {
      if (containsAll(rootCoalition, mappedPlayers)) {
        return mappedPlayers.size();
      }
      return -1;
    }
  }

  private static boolean containsOne(Coalition coalition, List<Player> players) {
    if (players.size() == 0) return true;
    for (Player player : players) {
      if (coalition.contains(player)) return true;
    }
    return false;
  }

  private static boolean containsAll(Coalition coalition, List<Player> players) {
    if (players.size() == 0) return true;
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
