package graph;

import entity.Player;

import java.util.*;

public class Graph<T> {
  public final Map<T, Node<T>> nodes = new HashMap<>();

  public void addEdge(T from, T to) {
    Node<T> fromNode = getNodeOrCreate(from);
    Node<T> toNode = getNodeOrCreate(to);

    fromNode.addNext(toNode);
  }

  private Node<T> getNodeOrCreate(T elem) {
    if (nodes.containsKey(elem)) return nodes.get(elem);

    Node<T> newNode = new Node<>(elem);
    nodes.put(elem, newNode);
    return newNode;
  }

  public static Graph<Player> createGraph(
      Player[] players,
      Scanner scanner
  ) {
    Graph<Player> result = new Graph<>();
    while (scanner.hasNext()) {
      int from = scanner.nextInt();
      int to = scanner.nextInt();

      Player toPlayer = players[to - 1];
      Player fromPlayer = players[from - 1];
      result.addEdge(fromPlayer, toPlayer);
      toPlayer.addPredecessor(fromPlayer);
    }
    return result;
  }
}
