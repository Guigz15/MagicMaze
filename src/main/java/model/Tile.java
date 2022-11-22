package model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a tile of the board
 */
public class Tile extends Rectangle {
    @Getter @Setter
    private boolean character;
    @Getter @Setter
    private boolean monster;
    @Getter @Setter
    private boolean badSmelling;
    @Getter @Setter
    private boolean crevasse;
    @Getter @Setter
    private boolean windy;
    @Getter @Setter
    private boolean portal;

    /**
     * Tile constructor
     */
    public Tile() {
        this.character = false;
        this.monster = false;
        this.badSmelling = false;
        this.crevasse = false;
        this.windy = false;
        this.portal = false;
        this.setFill(null);
        this.setStroke(Paint.valueOf("black"));
    }

    /**
     * This method is used to compare two tiles
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (getX() != tile.getX()) return false;
        return getY() == tile.getY();
    }

    /**
     * Get x position of the tile
     * @return x position
     */
    public int getXPosition()
    {
        return (int)getX();
    }

    /**
     * Get y position of the tile
     * @return y position
     */
    public int getYPosition()
    {
        return (int)getY();
    }

    /**
     * Draw objects in tile if an object is present
     */
    public void draw() {
        if (isCharacter()) {
            setFill(new ImagePattern(new Image("images/adventurer.png")));
        } else if (isMonster()) {
            setFill(new ImagePattern(new Image("images/monster.png")));
        } else if (isBadSmelling()) {
            setFill(new ImagePattern(new Image("images/bin.png")));
        } else if (isCrevasse()) {
            setFill(new ImagePattern(new Image("images/sink.png")));
        } else if (isWindy()) {
            setFill(new ImagePattern(new Image("images/wind.png")));
        } else if (isPortal()) {
            setFill(new ImagePattern(new Image("images/portal.png")));
        } else if (!isCharacter() && !isMonster() && !isBadSmelling() && !isCrevasse() && !isWindy() && !isPortal()) {
            setFill(null);
        }
    }
}