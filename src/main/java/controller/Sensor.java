package controller;

import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;
import java.util.LinkedHashSet;

public class Sensor {

    @Getter @Setter
    private Tile tile;
    @Getter @Setter
    private Board board;
    @Getter @Setter
    private LinkedHashSet<Tile> discoveredTiles;


    public Sensor(Board board, Tile tile) {
        this.board = board;
        this.tile = tile;
        discoveredTiles = new LinkedHashSet<>();
        discoveredTiles.add(tile);
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

    public void clear() {
        discoveredTiles.clear();
    }

    public void update() {
        discoveredTiles.add(tile);
    }
}
