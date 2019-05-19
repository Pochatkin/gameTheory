import entity.Coalition;
import entity.CoalitionUtils;
import entity.Game;
import entity.Player;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HelloFX extends Application {
  private Game initGame;
  private VBox leftPartContent;
  private VBox centralPartContent;
  private TextArea answerArea;

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
    temp.setSpacing(20);
    temp.getChildren().add(new Label("alpha"));
    TextField alphaField = new TextField();
    alphaField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        initGame.setBalancedParam(Float.parseFloat(newValue));
      } catch (NumberFormatException e) {
        initGame.setBalancedParam(0.5f);
      }
    });
    temp.getChildren().add(alphaField);

    contentBox.getChildren().addAll(leftPartContent, centralPartContent, rightPartContent);

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

    List<Type> types = new ArrayList<>();
    types.add(Type.CONJUNCTIVE);
    types.add(Type.DISJUNCTIVE);
    types.add(Type.BOTH);
    ComboBox<Type> gameTypeDropdown = new ComboBox<>(FXCollections.observableList(types));
    gameTypeDropdown.valueProperty().set(Type.BOTH);

    startButton.setText("Compute");
    startButton.setOnAction(event -> {
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
        case BOTH:
        default:
          stringBuilder.append("Conjunctive: \n");
          stringBuilder.append(initGame.computeConjunctiveGame().toString());
          stringBuilder.append("\n");
          stringBuilder.append("Disjunctive: \n");
          stringBuilder.append(initGame.computeDisjunctiveGame().toString());
          break;
      }
      answerArea.setText(stringBuilder.toString());
    });


    header.getChildren().add(createPlayersCountPart("Players count:", comboBox));
    header.getChildren().add(startButton);
    header.getChildren().add(gameTypeDropdown);
    return header;
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
