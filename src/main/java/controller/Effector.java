package controller;

import model.Action;
import model.Tile;

public class Effector {

    private Sensor sensor;

    public Effector(Sensor sensor) {
        this.sensor = sensor;
    }

    public void move(Action action) {
        switch (action) {
            case MOVE_UP:
                moveUp();
                break;
            case MOVE_DOWN:
                moveDown();
                break;
            case MOVE_LEFT:
                moveLeft();
                break;
            case MOVE_RIGHT:
                moveRight();
                break;
            case THROW_UP:
                throwUp();
                break;
            case THROW_DOWN:
                throwDown();
                break;
            case THROW_LEFT:
                throwLeft();
                break;
            case THROW_RIGHT:
                throwRight();
                break;
        }
    }

    public void moveUp() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1));
    }

    public void moveDown() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() + 1));
    }

    public void moveLeft() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition() - 1, sensor.getYPosition()));
    }

    public void moveRight() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition() + 1, sensor.getYPosition()));
    }

    public void throwUp() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
        } else {

        }
    }

    public void throwDown() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
        } else {

        }
    }

    public void throwLeft() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
        } else {

        }
    }

    public void throwRight() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
        } else {

        }
    }
}


