package controller;

import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Sensor {

    @Getter @Setter
    private Tile tile;
    @Getter @Setter
    private Board board;
    @Getter @Setter
    private LinkedHashSet<Tile> discoveredTiles;
    @Getter @Setter
    private HashMap<Tile, Double> boundaryTiles;


    public Sensor(Board board, Tile tile) {
        this.board = board;
        this.tile = tile;
        discoveredTiles = new LinkedHashSet<>();
        discoveredTiles.add(tile);
        boundaryTiles = new HashMap<>();
        board.getNeighbours(tile).forEach(neighbour -> {
            if (tile.isWindy() || tile.isBadSmelling())
                boundaryTiles.put(neighbour, 0.2);
            else
                boundaryTiles.put(neighbour, 0.0);
        });
        entriesSortedByValues(boundaryTiles);
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

    public void nextLevel() {
        discoveredTiles.clear();
        discoveredTiles.add(tile);
        boundaryTiles.clear();
        board.getNeighbours(tile).forEach(neighbour -> {
            if (tile.isWindy() || tile.isBadSmelling())
                boundaryTiles.put(neighbour, 0.2);
            else
                boundaryTiles.put(neighbour, 0.0);
        });
        entriesSortedByValues(boundaryTiles);
    }

    public void update() {
        discoveredTiles.add(tile);
        board.getNeighbours(tile).forEach(neighbour -> {
            if (tile.isWindy() || tile.isBadSmelling())
                boundaryTiles.put(neighbour, 0.2);
            else
                boundaryTiles.put(neighbour, 0.0);
        });
        entriesSortedByValues(boundaryTiles);
    }

    private void entriesSortedByValues(HashMap<Tile, Double> map) {
        HashMap<Tile, Double> result = new HashMap<>();
        map.entrySet().stream()
                .sorted(HashMap.Entry.comparingByValue())
                .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
    }
}
