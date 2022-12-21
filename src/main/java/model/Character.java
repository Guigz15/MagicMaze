package model;

import controller.Decision;
import controller.Effector;
import controller.Sensor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents a character
 */
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

    /**
     * Method called when the character changes level
     */
    public void nextLevel() {
        sensor.nextLevel();
        decision.nextLevel();
    }

    /**
     * Method called when the character dies
     */
    public void die() {
        decision.updateEvaluation(-10 * sensor.getBoard().getHeight() * sensor.getBoard().getWidth());
    }
}
