package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import model.Action;
import model.Board;
import model.Tile;
import java.util.List;
import java.util.ResourceBundle;
import model.Character;

/**
 * Class that represents the controller of the main window
 */
public class MainWindowController implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private Button moveButton;
    @Getter @Setter
    private Board board;
    private Character character;

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        this.board = new Board(3, 3);

        for(int i = 0; i < board.getWidth(); i++) {
            for(int j = 0; j < board.getHeight(); j++) {
                Tile tile = board.getTile(i, j);
                tile.setHeight(200);
                tile.setWidth(200);
                gridPane.add(board.getTile(i, j), i, j);
            }
        }

        board.generateItems();

        character = new Character(board);
        moveButton.setOnAction(event -> {
            if (character.getSensor().getTile().isPortal()) {
                board.updateBoardSize(gridPane, character, board.getHeight() + 1, board.getWidth() + 1);
                character.nextLevel();
            } else if (character.getSensor().getTile().isCrevasse() || character.getSensor().getTile().isMonster()) {
                character.die();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous rejouer ?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Game Over");
                alert.setHeaderText("Vous êtes mort !");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    board.updateBoardSize(gridPane, character, 3, 3);
                    character.getSensor().nextLevel();
                } else {
                    // Kill GUI Thread
                    Platform.exit();
                    // Kill the JVM
                    System.exit(0);
                }
            } else {
                List<Action> actions = character.getDecision().makeRule();
                character.getEffector().doActions(character, actions);
                character.getSensor().update();
            }
        });
    }
}

