package controller;

import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Sensor {

    @Getter @Setter
    private Tile tile;
    @Getter @Setter
    private Board board;
    @Getter @Setter
    private LinkedHashSet<Tile> discoveredTiles;
    @Getter @Setter
    private LinkedHashSet<Tile> unexploredTiles;


    public Sensor(Board board, Tile tile) {
        this.board = board;
        this.tile = tile;
        discoveredTiles = new LinkedHashSet<>();
        discoveredTiles.add(tile);
        unexploredTiles = new LinkedHashSet<>();
        unexploredTiles.addAll(board.getNeighbors(tile));
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
        unexploredTiles.clear();
    }

    public void update()
    {
        discoveredTiles.add(tile);
        unexploredTiles.remove(tile);
        board.getNeighbors(tile).forEach(neighbor ->
        {
            if (!discoveredTiles.contains(neighbor))
            {
                unexploredTiles.add(neighbor);
            }
        })
        ;
    }
}
