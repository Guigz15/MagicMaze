package controller;

import model.Action;
import model.Character;
import model.Tile;
import java.util.List;

/**
 * Class that represents the effector of the character
 */
public class Effector {

    private Sensor sensor;

    public Effector(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Method that moves the character through the list of actions
     * @param character to move
     * @param actions to perform
     */
    public void doActions(Character character, List<Action> actions) {
        actions.forEach(action -> doAction(character, action));
    }

    /**
     * Method that moves the character with an action
     * @param character to move
     * @param action to perform
     */
    private void doAction(Character character, Action action) {
        Tile oldTile = character.getSensor().getTile();
        int oldX = oldTile.getXPosition();
        int oldY = oldTile.getYPosition();
        switch (action) {
            case MOVE_UP:
                if(oldY > 0) {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveUp();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_DOWN:
                if(oldTile.getY() < sensor.getBoard().getHeight() - 1) {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveDown();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_LEFT:
                if (oldX > 0) {
                    oldTile.setCharacter(false);
                    oldTile.draw();
                    moveLeft();
                    character.getDecision().updateEvaluation(-1);
                }
                break;
            case MOVE_RIGHT:
                if (oldX < sensor.getBoard().getWidth()-1) {
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

    /**
     * Method that moves the character up
     */
    public void moveUp() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1));
    }

    /**
     * Method that moves the character down
     */
    public void moveDown() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() + 1));
    }

    /**
     * Method that moves the character left
     */
    public void moveLeft() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition() - 1, sensor.getYPosition()));
    }

    /**
     * Method that moves the character right
     */
    public void moveRight() {
        sensor.setTile(sensor.getBoard().getTile(sensor.getXPosition() + 1, sensor.getYPosition()));
    }

    /**
     * Method that throws a rock up
     */
    public void throwUp() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() - 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
            removeBadSmelling(tile);
        }
    }

    /**
     * Method that throws a rock down
     */
    public void throwDown() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition(), sensor.getYPosition() + 1);
        if(tile.isMonster()) {
            tile.setMonster(false);
            removeBadSmelling(tile);
        }
    }

    /**
     * Method that throws a rock left
     */
    public void throwLeft() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition() - 1, sensor.getYPosition());
        if(tile.isMonster()) {
            tile.setMonster(false);
            removeBadSmelling(tile);
        }
    }

    /**
     * Method that throws a rock right
     */
    public void throwRight() {
        Tile tile = sensor.getBoard().getTile(sensor.getXPosition() + 1, sensor.getYPosition());
        if(tile.isMonster()) {
            tile.setMonster(false);
            removeBadSmelling(tile);
        }
    }

    /**
     * Method that removes the bad smelling of neighbours of tile
     * @param tile to remove the bad smelling
     */
    private void removeBadSmelling(Tile tile) {
        List<Tile> neighbours = sensor.getBoard().getNeighbours(tile);
        neighbours.forEach(neighbour -> {
            neighbour.setBadSmelling(false);
            neighbour.draw();
        });

        List<Tile> allNeighbours = sensor.getBoard().getAllNeighbours(tile);
        allNeighbours.forEach(neighbour -> {
            if (neighbour.isMonster()) {
                List<Tile> neighboursOfNeighbour = sensor.getBoard().getNeighbours(neighbour);
                neighboursOfNeighbour.forEach(neighbourOfNeighbour -> {
                    neighbourOfNeighbour.setBadSmelling(true);
                    neighbourOfNeighbour.draw();
                });
            }
        });
    }
}


