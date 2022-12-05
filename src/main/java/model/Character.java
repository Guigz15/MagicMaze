package model;

import controller.Decision;
import controller.Effector;
import controller.Sensor;
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
    public void initializeLevel(Board board)
    {
        sensor.clear();
        sensor.getDiscoveredTiles().add(sensor.getTile());
        sensor.getUnexploredTiles().addAll((board.getNeighbors(sensor.getTile())));
    }
}
