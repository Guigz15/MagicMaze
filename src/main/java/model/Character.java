package model;

import controller.Effector;
import controller.Sensor;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class Character {

    @Getter @Setter
    private Sensor sensor;
    @Getter @Setter
    private Effector effector;

    public Character(Board board) {
        Tile startTile = board.getRandomEmptyTile();
        startTile.setCharacter(true);
        startTile.draw();
        this.sensor = new Sensor(board, startTile);
        this.effector = new Effector(this.sensor);
    }
}
