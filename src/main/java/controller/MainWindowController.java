package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import model.Action;
import model.Board;
import model.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import model.Character;

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
        moveButton.setOnAction(event ->
        {
            if (character.getSensor().getTile().isPortal())
            {
                board.nextLevel(gridPane, character);
                character.initializeLevel(board);
            }
            else
            {
                List<Action> actions = character.getDecision().makeRule();
                character.getEffector().doAction(character, actions);
                character.getSensor().update();
            }
        });
    }
}

