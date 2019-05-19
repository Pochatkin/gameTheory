import entity.Coalition;
import entity.CoalitionUtils;
import entity.Game;
import entity.Player;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloFX extends Application {
  private Game initGame;
  private VBox leftPartContent;
  private VBox centralPartContent;
  private TextArea answerArea;
  private TextField alphaField;
  private ComboBox<Type> gameTypeDropdown;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("The big Alfons's program");

    VBox root = new VBox();
    root.setPadding(new Insets(10));
    root.setSpacing(20);

    HBox contentBox = new HBox();
    contentBox.setSpacing(30);

    leftPartContent = new VBox();
    leftPartContent.setSpacing(50);
    leftPartContent.setPadding(new Insets(10, 0, 0, 0));
    centralPartContent = new VBox();
    VBox rightPartContent = new VBox();

    VBox temp = new VBox();
    HBox box = new HBox();
    temp.setSpacing(20);
    box.setSpacing(10);
    box.getChildren().add(new Label("Alpha: "));
    alphaField = new TextField();
    box.getChildren().add(alphaField);
    temp.getChildren().add(box);
    temp.getChildren().add(leftPartContent);

    contentBox.getChildren().addAll(temp, centralPartContent, rightPartContent);

    answerArea = new TextArea();
    rightPartContent.getChildren().add(answerArea);

    Node header = createHeader();

    root.getChildren().add(header);
    root.getChildren().add(contentBox);

    primaryStage.setScene(new Scene(root, 1024, 768));
    primaryStage.show();
  }

  private void updateEdgesPart() {
    leftPartContent.getChildren().clear();
    for (Player player : initGame.players) {
      HBox box = new HBox();
      box.setSpacing(20);
      box.getChildren().add(new Label(player.key + ": "));
      for (Player player1 : initGame.players) {
        if (player == player1) continue;

        HBox innerBox = new HBox();
        innerBox.getChildren().add(new Label(player1.key + ""));
        CheckBox e = new CheckBox();
        e.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
          if (newValue) {
            player1.addPredecessor(player);
          } else {
            player1.removePredecessor(player);
          }
        });
        innerBox.getChildren().add(e);
        box.getChildren().add(innerBox);
      }
      leftPartContent.getChildren().add(box);
    }
  }


  private Node createHeader() {
    HBox header = new HBox();
    header.setSpacing(200);
    Button startButton = new Button();

    ComboBox<Integer> comboBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
    comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      updateGame(newValue);
      updateEdgesPart();
    });
    comboBox.valueProperty().setValue(4);

    ObservableList<Type> types = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(Type.values())));
    gameTypeDropdown = new ComboBox<>(types);
    gameTypeDropdown.valueProperty().set(Type.CONJUNCTIVE);

    startButton.setText("Compute");
    startButton.setOnAction(event -> run());


    header.getChildren().add(createPlayersCountPart("Players count:", comboBox));
    header.getChildren().add(startButton);
    header.getChildren().add(gameTypeDropdown);
    return header;
  }

  private void run() {
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
        stringBuilder.append("Conjunctive: \n");
        stringBuilder.append(initGame.computeBalancedGame().computeConjunctiveGame().toString());
        break;
    }
    answerArea.setText(stringBuilder.toString());
  }

  private Node createPlayersCountPart(String textLabel, Node field) {
    Label playersCountLabel = new Label(textLabel);

    HBox box = new HBox();
    box.setSpacing(30);
    box.getChildren().addAll(playersCountLabel, field);
    return box;
  }


  private void updateGame(int count) {
    centralPartContent.getChildren().clear();
    Player[] players = Player.createPlayers(count);
    initGame = new Game(players);
    List<Coalition> coalitions = CoalitionUtils.createCoalitions(players);
    coalitions.sort(Coalition::compareTo);
    for (Coalition coalition : coalitions) {
      TextField field = new TextField();
      ChangeListener<String> listener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
          field.setText(oldValue);
        } else {
          float value = 0;
          try {
            value = Float.parseFloat(newValue);
          } catch (NumberFormatException ignored) {
          }
          initGame.putCoalition(coalition, value);
        }
      };
      field.textProperty().addListener(listener);
      centralPartContent.getChildren().add(createPlayersCountPart(coalition.getTitle(), field));
    }
  }
}
