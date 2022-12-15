package controller;

import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;

import java.util.*;

public class Sensor {

    @Getter @Setter
    private Tile tile;
    @Getter @Setter
    private Board board;
    @Getter @Setter
    private LinkedHashSet<Tile> discoveredTiles;
    @Getter @Setter
    private TreeMap<Double, List<Tile>> boundaryTiles;


    public Sensor(Board board, Tile tile) {
        this.board = board;
        this.tile = tile;
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(2);
        discoveredTiles = new LinkedHashSet<>();
        discoveredTiles.add(tile);
        boundaryTiles = new TreeMap<>();
        initBoundaryTiles();
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
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(2);
        boundaryTiles.clear();
        initBoundaryTiles();
    }

    public void update() {
        discoveredTiles.add(tile);
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(2);
        boundaryTiles.firstEntry().getValue().remove(tile);
        if (boundaryTiles.firstEntry().getValue().isEmpty()) {
            boundaryTiles.remove(boundaryTiles.firstKey());
        }
        initBoundaryTiles();
    }

    private void initBoundaryTiles() {
        board.getNeighbours(tile).forEach(neighbour -> {
            if (!discoveredTiles.contains(neighbour)) {
                if (tile.isWindy() || tile.isBadSmelling()) {
                    if (!boundaryTiles.containsKey(0.2))
                        boundaryTiles.put(0.2, new ArrayList<>());

                    // Add only if the neighbour is not already in the boundary tiles with a lower probability
                    if (boundaryTiles.values().stream().noneMatch(tiles -> tiles.contains(neighbour)))
                        boundaryTiles.get(0.2).add(neighbour);
                } else {
                    if (!boundaryTiles.containsKey(0.0))
                        boundaryTiles.put(0.0, new ArrayList<>());

                    // Remove the neighbour from the boundary tiles if it is already into with higher probability
                    if (boundaryTiles.values().stream().anyMatch(tiles -> tiles.contains(neighbour)))
                        boundaryTiles.values().forEach(tiles -> tiles.remove(neighbour));
                    boundaryTiles.get(0.0).add(neighbour);
                }
            }
        });
    }
}
