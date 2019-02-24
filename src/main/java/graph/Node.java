package graph;

import java.util.HashSet;
import java.util.Set;

public class Node<T> {
  public final T value;
  private final Set<Node<T>> nextNodes = new HashSet<>();


  public Node(T value) {
    this.value = value;
  }

  public void addNext(Node<T> node) {
    nextNodes.add(node);
  }

  public void removeNext(Node<T> node) {
    nextNodes.remove(node);
  }

  @Override
  public String toString() {
    StringBuilder indices = new StringBuilder("nextNodes={");
    for (Node<T> node : nextNodes) {
      indices.append(node.value).append("; ");
    }
    indices.append("}").append("\n");
    return
        indices.toString();
  }
}
