package model;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class Board {
    @Getter @Setter
    private List<List<Tile>> tiles;
    @Getter @Setter
    private int height;
    @Getter @Setter
    private int width;

    public Board(int height, int width) {
        this.height = height;
        this.width = width;
        this.tiles = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            List<Tile> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                Tile tile = new Tile();
                tile.setX(i);
                tile.setY(j);
                row.add(tile);
            }
            tiles.add(row);
        }
    }

    /**
     * Get a tile from the board
     * @param x of the tile
     * @param y of the tile
     * @return tile at position x, y
     */
    public Tile getTile(int x, int y) {
        return tiles.get(x).get(y);
    }

    /**
     * Get all neighbours of a tile
     * @param tile to get neighbours
     * @return list of neighbours of the tile
     */
    public List<Tile> getNeighbours(Tile tile) {
        int x = (int) tile.getX();
        int y = (int) tile.getY();
        List<Tile> neighbors = new ArrayList<>();
        if (x > 0)
            neighbors.add(getTile(x - 1, y));
        if (x < width - 1)
            neighbors.add(getTile(x + 1, y));
        if (y > 0)
            neighbors.add(getTile(x, y - 1));
        if (y < height - 1)
            neighbors.add(getTile(x, y + 1));
        return neighbors;
    }

    public List<Tile> getDiscoveredNeighbours(Tile tile, LinkedHashSet<Tile> discoveredTiles) {
        List<Tile> neighbours = getNeighbours(tile);
        List<Tile> discoveredNeighbours = new ArrayList<>();
        for (Tile neighbour : neighbours) {
            if (discoveredTiles.contains(neighbour)) {
                discoveredNeighbours.add(neighbour);
            }
        }
        return discoveredNeighbours;
    }

    public int getNumberOfUndiscoveredNeighbours(Tile tile, LinkedHashSet<Tile> discoveredTiles) {
        List<Tile> neighbours = getNeighbours(tile);
        int numberOfUndiscoveredNeighbours = 0;
        for (Tile neighbour : neighbours) {
            if (!discoveredTiles.contains(neighbour)) {
                numberOfUndiscoveredNeighbours++;
            }
        }
        return numberOfUndiscoveredNeighbours;
    }

    public void generateItems() {
        tiles.forEach(row -> row.forEach(tile -> {
            Random random = new Random();
            if (random.nextInt(100) < 20) {
                if (random.nextInt(100) < 50) {
                    tile.setCrevasse(true);
                    List<Tile> neighbors = getNeighbours(tile);
                    neighbors.forEach(neighbor ->
                    {
                        neighbor.setWindy(true);
                        neighbor.draw();
                    });
                } else {
                    tile.setMonster(true);
                    List<Tile> neighbors = getNeighbours(tile);
                    neighbors.forEach(neighbor ->
                    {
                        neighbor.setBadSmelling(true);
                        neighbor.draw();
                    });
                }
            }
            tile.draw();
        }));

        Tile portalTile = getRandomEmptyTile();
        portalTile.setPortal(true);
        portalTile.draw();
    }

    public void updateBoardSize(GridPane gridPane, Character character, int height, int width) {
        gridPane.getChildren().clear();
        setHeight(height);
        setWidth(width);

        tiles = new ArrayList<>();
        for (int i = 0; i < getHeight(); i++) {
            List<Tile> row = new ArrayList<>();
            for (int j = 0; j < getWidth(); j++) {
                Tile tile = new Tile();
                tile.setX(i);
                tile.setY(j);
                row.add(tile);
            }
            tiles.add(row);
        }

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                Tile tile = getTile(i, j);
                tile.setHeight(600.0 / getHeight());
                tile.setWidth(600.0 / getWidth());
                gridPane.add(getTile(i, j), i, j);
            }
        }

        for (RowConstraints row : gridPane.getRowConstraints())
            row.setPercentHeight(100.0 / getHeight());

        for (ColumnConstraints column : gridPane.getColumnConstraints())
            column.setPercentWidth(100.0 / getWidth());

        generateItems();

        Tile characterTile = getRandomEmptyTile();
        characterTile.setCharacter(true);
        character.getSensor().setTile(characterTile);
        characterTile.draw();
    }

    public Tile getRandomEmptyTile() {
        Random random = new Random();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Tile tile = getTile(x, y);
        while (tile.hasItem()) {
            x = random.nextInt(width);
            y = random.nextInt(height);
            tile = getTile(x, y);
        }
        return tile;
    }
}
