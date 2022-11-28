package controller;

import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;

import java.util.List;

public class Sensor {

    @Getter @Setter
    private Tile tile;
    @Getter @Setter
    private Board board;

    public Sensor(Board board, Tile tile) {
        this.board = board;
        this.tile = tile;
    }

    /**
     * Get x position of the tile
     * @return tile x position
     */
    public int getXPosition() {
        return tile.getXPosition();
    }

    /**
     * Get y position of the tile
     * @return tile y position
     */
    public int getYPosition() {
        return tile.getYPosition();
    }


}
