package controller;

import model.Action;
import model.Character;
import model.Tile;

import java.util.List;

public class Effector {

    private Sensor sensor;


    public Effector(Sensor sensor) {
        this.sensor = sensor;
    }

    public void doAction(Character character, List<Action> actions)
    {
        actions.forEach(action->doAction(character, action));
    }
    public void doAction(Character character, Action action) {
        Tile oldTile = character.getSensor().getTile();
        int oldX = oldTile.getXPosition();
        int oldY = oldTile.getYPosition();
        switch (action) {
            case MOVE_UP:
                if(oldY > 0)
                {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveUp();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_DOWN:
                if(oldTile.getY() < sensor.getBoard().getHeight() - 1)
                {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveDown();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_LEFT:
                if (oldX > 0)
                {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveLeft();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_RIGHT:
                if (oldX < sensor.getBoard().getWidth()-1)
                {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveRight();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case THROW_UP:
                throwUp();
                character.getDecision().updateEvaluation(-10);
                break;
            case THROW_DOWN:
                throwDown();
                character.getDecision().updateEvaluation(-10);
                break;
            case THROW_LEFT:
                throwLeft();
                character.getDecision().updateEvaluation(-10);
                break;
            case THROW_RIGHT:
                throwRight();
                character.getDecision().updateEvaluation(-10);
                break;
        }
        character.getSensor().getTile().setCharacter(true);
        character.getSensor().getTile().draw();
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


