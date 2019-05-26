import entity.Coalition;
import entity.CoalitionUtils;
import entity.Game;
import entity.Player;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main1 extends Application {
  private FileChooser fileChooser;
  private Scanner scanner;
  private Game initGame;
  private TextField alphaField;
  private TextArea answerArea;
  private ComboBox<Type> gameTypeDropdown;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle("Game solver");

    fileChooser = new FileChooser();
    fileChooser.setTitle("Select data file");

    VBox root = new VBox();
    root.setPadding(new Insets(10));
    root.setSpacing(20);

    HBox contentBox = new HBox();
    contentBox.setSpacing(30);

    contentBox.getChildren().add(new Label("Alpha: "));
    alphaField = new TextField();
    alphaField.textProperty().setValue("0.1");
    contentBox.getChildren().add(alphaField);

    Button startButton = new Button();
    startButton.setText("Compute");
    startButton.setOnAction(event -> run());
    contentBox.getChildren().add(startButton);

    answerArea = new TextArea();
    answerArea.setMinHeight(400);
    contentBox.getChildren().add(answerArea);

    Button button = new Button("check");
    contentBox.getChildren().add(button);
    button.setOnAction(actionEvent -> answerArea.textProperty().setValue(String.valueOf(initGame.checkBounds())));


    Node header = createHeader(stage);
    root.getChildren().add(header);
    root.getChildren().add(contentBox);

    stage.setScene(new Scene(root, 1024, 512));
    stage.show();
  }

  private void createGame(Scanner scanner) {
    Player[] players = Player.createPlayers(scanner.nextInt());
    initGame = new Game(players);

    for (int i = 0; i < players.length; i++) {
      int from = scanner.nextInt();
      int to = scanner.nextInt();
      players[to - 1].addPredecessor(players[from - 1]);
    }

    List<Coalition> coalitions = CoalitionUtils.createCoalitions(players);
    coalitions.sort(Coalition::compareTo);
    for (Coalition coalition : coalitions) {
      initGame.putCoalition(coalition, scanner.nextFloat());
    }
  }

  private Node createHeader(Stage stage) {
    HBox header = new HBox();
    header.setSpacing(200);

    Label fileNameLabel = new Label("");

    Button fileChooserButton = new Button("Open file");
    fileChooserButton.setOnAction(actionEvent -> {
      File file = fileChooser.showOpenDialog(stage);
      System.out.println(file);
      if (file != null) {
        try {
          scanner = new Scanner(file);
          createGame(scanner);
          fileNameLabel.textProperty().setValue(file.getName());
        } catch (FileNotFoundException ignored) { }
      }
    });


    ObservableList<Type> types = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(Type.values())));
    gameTypeDropdown = new ComboBox<>(types);
    gameTypeDropdown.valueProperty().set(Type.CONJUNCTIVE);


    HBox box = new HBox();
    box.getChildren().add(fileChooserButton);
    box.getChildren().add(fileNameLabel);
    box.setSpacing(10);

    header.getChildren().add(box);
    header.getChildren().add(gameTypeDropdown);
    return header;
  }

  private void run() {
    if (initGame == null) return;
    try {
      initGame.setBalancedParam(Float.parseFloat(alphaField.textProperty().get()));
    } catch(NumberFormatException e) {
      e.printStackTrace();
      initGame.setBalancedParam(0.5f);
    }
    Type currentType = gameTypeDropdown.valueProperty().get();
    StringBuilder stringBuilder = new StringBuilder();
    switch (currentType) {
      case CONJUNCTIVE:
        stringBuilder.append("Conjunctive: \n");
        stringBuilder.append(initGame.computeConjunctiveGame().toString());
        break;
      case DISJUNCTIVE:
        stringBuilder.append("Disjunctive: \n");
        stringBuilder.append(initGame.computeDisjunctiveGame().toString());
        break;
      case DUAL_GAME:
        stringBuilder.append("Dual: \n");
        stringBuilder.append(initGame.computeDualGame().toString());
        break;
      case BALANCED_GAME:
        stringBuilder.append("Balanced: \n");
        stringBuilder.append(initGame.computeBalancedGame().toString());
        break;
      case CONJUNCTIVE_BALANCED:
        stringBuilder.append("Conjunctive balanced: \n");
        stringBuilder.append(initGame.computeBalancedGame().computeConjunctiveGame().toString());
        break;
      case DISJUNCTIVE_BALANCED:
        stringBuilder.append("Disjunctive balanced: \n");
        stringBuilder.append(initGame.computeBalancedGame().computeDisjunctiveGame().toString());
        break;
      case ALPHA_NUCLEOLUS:
        stringBuilder.append(alphaField.textProperty().get()).append("-nucleolus of (N, r): \n");
        Game balancedGame = initGame;
        List<Coalition> coalitions = balancedGame.computeDisjunctiveCoalitions();


        Map<Player, Float> playerFloatMap = new HashMap<>();
        Game.computeDivision(balancedGame, balancedGame.coalitionWithAllPlayers, coalitions, playerFloatMap);
        float sum = 0;
        for (Player player : playerFloatMap.keySet()) {
          sum += playerFloatMap.get(player);
        }
        playerFloatMap.put(
                balancedGame.players[0],
                balancedGame.coalitionsValue.get(balancedGame.coalitionWithAllPlayers) - sum
        );
        stringBuilder.append(playerFloatMap.values().toString());
    }
    answerArea.setText(stringBuilder.toString());
  }
}
