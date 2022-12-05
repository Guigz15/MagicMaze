package model;

import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.ImagePattern;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
     * Get all neighbors of a tile
     * @param tile to get neighbors
     * @return list of neighbors of the tile
     */
    public List<Tile> getNeighbors(Tile tile) {
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

    public void generateItems() {
        int monsterPlaced = 0;
        int crevassePlaced = 0;
        boolean portalPlaced = false;
        while (monsterPlaced < height-2 || crevassePlaced < height-2 || !portalPlaced)
        {
            Tile tile = getRandomEmptyTile();
            if (!tile.hasItem()) {
                if (monsterPlaced < height - 2) {
                    tile.setMonster(true);
                    List<Tile> neighbors = getNeighbors(tile);
                    neighbors.forEach(neighbor ->
                    {
                        neighbor.setBadSmelling(true);
                        neighbor.draw();
                    });
                    monsterPlaced++;
                } else if (crevassePlaced < height - 2) {
                    tile.setCrevasse(true);
                    List<Tile> neighbors = getNeighbors(tile);
                    neighbors.forEach(neighbor ->
                    {
                        neighbor.setWindy(true);
                        neighbor.draw();
                    });
                    crevassePlaced++;
                } else {
                    tile.setPortal(true);
                    portalPlaced = true;
                }
                /*
                System.out.println("nombre de monstre placé : " + monsterPlaced);
                System.out.println("nombre de crevasse placé : " + crevassePlaced);
                System.out.println("nombre de portail placé : " + portalPlaced);

                 */
            }
            tile.draw();
        }
    }

    public void nextLevel(GridPane gridPane, Character character) {
        gridPane.getChildren().clear();
        setHeight(getHeight() + 1);
        setWidth(getWidth() + 1);

        tiles = new ArrayList<>();
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
