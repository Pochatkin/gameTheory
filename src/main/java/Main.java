import entity.Coalition;
import entity.CoalitionUtils;
import entity.Game;
import entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    String path = "src/data.txt";
    Scanner scanner;

    scanner = new Scanner(new File(path));
    int n = scanner.nextInt();
    Player[] players = Player.createPlayers(n);

    for (int i = 0; i < 4; i++) {
      int from = scanner.nextInt();
      int to = scanner.nextInt();
      players[to - 1].addPredecessor(players[from - 1]);
    }

    List<Coalition> coalitions = CoalitionUtils.createCoalitions(players);
    coalitions.sort(Coalition::compareTo);
    Game game = new Game(players);
    for (Coalition coalition : coalitions) {
      game.putCoalition(coalition, scanner.nextFloat());
    }

    Game conjunctiveGame = game.computeConjunctiveGame();
    System.out.println("conjunctive game: ");
    System.out.println(conjunctiveGame.toString());
    Game disjunctiveGame = game.computeDisjunctiveGame();
    System.out.println("disjunctive game: ");
    System.out.println(disjunctiveGame.toString());
  }

}
