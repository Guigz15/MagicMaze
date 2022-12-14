package model;

import controller.Decision;
import controller.Effector;
import controller.Sensor;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;


public class Character {

    @Getter @Setter
    private Sensor sensor;
    @Getter @Setter
    private Decision decision;
    @Getter @Setter
    private Effector effector;

    public Character(Board board) {
        Tile startTile = board.getRandomEmptyTile();
        startTile.setCharacter(true);
        startTile.draw();
        this.sensor = new Sensor(board, startTile);
        this.effector = new Effector(this.sensor);
        this.decision = new Decision(this.sensor);
    }
    public void nextLevel() {
        sensor.nextLevel();
        decision.nextLevel();
    }

    public void die() {
        decision.updateEvaluation(-10 * sensor.getBoard().getHeight() * sensor.getBoard().getWidth());
    }
}
