import entity.Coalition;
import entity.CoalitionUtils;
import entity.Game;
import entity.Player;
import graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    String path = "data.txt";
    Scanner scanner;

    scanner = new Scanner(new File(path));
    int n = scanner.nextInt();
    Player[] players = Player.createPlayers(n);

    List<Coalition> coalitions = CoalitionUtils.createCoalitions(players);
    coalitions.sort(Coalition::compareTo);
    Game game = new Game(players);
    for (Coalition coalition : coalitions) {
      game.putCoalition(coalition, scanner.nextFloat());
    }
    Graph<Player> graph = Graph.createGraph(players, scanner);

    Game conjunctiveGame = CoalitionUtils.computeConjunctiveGame(game);
    System.out.println("disjunctive game: ");
    System.out.println(conjunctiveGame.toString());
    Game disjunctiveGame = CoalitionUtils.computeDisjunctiveGame(game);
    System.out.println("conjunctive game: ");
    System.out.println(disjunctiveGame.toString());
  }

}
