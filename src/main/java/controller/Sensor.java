package controller;

import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Class that represents the sensor of the character
 */
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
        tile.setStrokeWidth(3);
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

    /**
     * Method to initialize all variables when we change the level
     */
    public void nextLevel() {
        discoveredTiles.clear();
        discoveredTiles.add(tile);
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(3);
        boundaryTiles.clear();
        initBoundaryTiles();
    }

    /**
     * Method called to update variables when the character moves
     */
    public void update() {
        discoveredTiles.add(tile);
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(3);
        boundaryTiles.firstEntry().getValue().remove(tile);
        boundaryTiles.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        initBoundaryTiles();
    }

    /**
     * Method to initialize the boundary tiles
     */
    private void initBoundaryTiles() {
        board.getNeighbours(tile).forEach(neighbour -> {
            if (!discoveredTiles.contains(neighbour)) {
                if (tile.isWindy() || tile.isBadSmelling()) {
                    if (tile.isOnlyWindy()) {
                        if (!boundaryTiles.containsKey(0.3))
                            boundaryTiles.put(0.3, new ArrayList<>());

                        // Add only if the neighbour is not already in the boundary tiles with a lower probability
                        if (tileNotExistsWithSameProbability(neighbour, 0.3) && tileNotExistsWithProbabilityZero(neighbour)) {
                            boundaryTiles.get(0.3).add(neighbour);
                            neighbour.setStroke(Paint.valueOf("blue"));
                            neighbour.setStrokeWidth(3);
                            neighbour.setProbability(0.3);
                        }
                    } else {
                        if (!boundaryTiles.containsKey(0.2))
                            boundaryTiles.put(0.2, new ArrayList<>());

                        // Add only if the neighbour is not already in the boundary tiles with a lower probability
                        if (tileNotExistsWithSameProbability(neighbour, 0.2) && tileNotExistsWithProbabilityZero(neighbour)) {
                            boundaryTiles.get(0.2).add(neighbour);
                            neighbour.setStroke(Paint.valueOf("blue"));
                            neighbour.setStrokeWidth(3);
                            neighbour.setProbability(0.2);
                        }
                    }
                } else {
                    if (!boundaryTiles.containsKey(0.0))
                        boundaryTiles.put(0.0, new ArrayList<>());

                    // Remove the neighbour from the boundary tiles if it is already into with higher probability
                    if (tileAlreadyExistsWithHigherProbability(neighbour)) {
                        boundaryTiles.values().forEach(tiles -> tiles.remove(neighbour));
                    }

                    if (tileNotExistsWithSameProbability(neighbour, 0.0)) {
                        boundaryTiles.get(0.0).add(neighbour);
                        neighbour.setStroke(Paint.valueOf("blue"));
                        neighbour.setStrokeWidth(3);
                        neighbour.setProbability(0.0);
                    }
                }
            }
        });

        computeProbabilities(boundaryTiles);
    }

    /**
     * Method to compute the probabilities of the tiles in the boundary tiles
     * @param boundaryTiles boundary tiles
     */
    private void computeProbabilities(TreeMap<Double, List<Tile>> boundaryTiles) {
        TreeMap<Double, List<Tile>> newBoundaryTiles = new TreeMap<>();
        newBoundaryTiles.putAll(boundaryTiles);
        newBoundaryTiles.forEach(new BiConsumer<Double, List<Tile>>() {
            @Override
            public void accept(Double probability, List<Tile> tiles) {
                List<Tile> newTiles = new ArrayList<>(tiles);
                newTiles.forEach(tile -> {
                    List<Tile> discoveredNeighbours = board.getDiscoveredNeighbours(tile, discoveredTiles);
                    if (discoveredNeighbours.size() == 1) {
                        Tile discoveredNeighbour = discoveredNeighbours.get(0);
                        if (discoveredNeighbour.isWindy() || discoveredNeighbour.isBadSmelling()) {
                            if (discoveredNeighbour.isOnlyWindy()) {
                                if (!boundaryTiles.containsKey(0.3))
                                    boundaryTiles.put(0.3, new ArrayList<>());
                                if (tileNotExistsWithSameProbability(tile, 0.3) && tileNotExistsWithProbabilityZero(tile)) {
                                    boundaryTiles.get(0.3).add(tile);
                                    tile.setProbability(0.3);
                                }
                                removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.3);
                            } else {
                                if (!boundaryTiles.containsKey(0.2))
                                    boundaryTiles.put(0.2, new ArrayList<>());
                                if (tileNotExistsWithSameProbability(tile, 0.2) && tileNotExistsWithProbabilityZero(tile)) {
                                    boundaryTiles.get(0.2).add(tile);
                                    tile.setProbability(0.2);
                                }
                            }
                        } else {
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                tile.setProbability(0.0);
                            }
                        }
                    } else if (discoveredNeighbours.size() == 2) {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy()) {
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                tile.setProbability(0.6);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling()) {
                            if (!boundaryTiles.containsKey(0.5))
                                boundaryTiles.put(0.5, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.5) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.5).add(tile);
                                tile.setProbability(0.5);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.5);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling())) {
                            if (!boundaryTiles.containsKey(0.4))
                                boundaryTiles.put(0.4, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.4) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.4).add(tile);
                                tile.setProbability(0.4);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.4);
                        } else {
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                tile.setProbability(0.0);
                            }
                        }
                    } else if (discoveredNeighbours.size() == 3) {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        Tile discoveredNeighbour3 = discoveredNeighbours.get(2);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy() && discoveredNeighbour3.isWindy()) {
                            if (!boundaryTiles.containsKey(0.8))
                                boundaryTiles.put(0.8, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.8) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.8).add(tile);
                                tile.setProbability(0.8);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.8);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling() && discoveredNeighbour3.isBadSmelling()) {
                            if (!boundaryTiles.containsKey(0.7))
                                boundaryTiles.put(0.7, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.7) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.7).add(tile);
                                tile.setProbability(0.7);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.7);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling())) {
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                tile.setProbability(0.6);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling())) {
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.5);
                            if (!boundaryTiles.containsKey(0.5))
                                boundaryTiles.put(0.5, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.5) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.5).add(tile);
                                tile.setProbability(0.5);
                            }
                        } else {
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                tile.setProbability(0.0);
                            }
                        }
                    } else {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        Tile discoveredNeighbour3 = discoveredNeighbours.get(2);
                        Tile discoveredNeighbour4 = discoveredNeighbours.get(3);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy() && discoveredNeighbour3.isWindy() && discoveredNeighbour4.isWindy()) {
                            if (!boundaryTiles.containsKey(1.0))
                                boundaryTiles.put(1.0, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 1.0) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(1.0).add(tile);
                                tile.setProbability(1.0);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 1.0);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling() && discoveredNeighbour3.isBadSmelling() && discoveredNeighbour4.isBadSmelling()) {
                            if (!boundaryTiles.containsKey(0.9))
                                boundaryTiles.put(0.9, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.9) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.9).add(tile);
                                tile.setProbability(0.9);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.9);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling())) {
                            if (!boundaryTiles.containsKey(0.8))
                                boundaryTiles.put(0.8, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.8) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.8).add(tile);
                                tile.setProbability(0.8);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.8);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling()) ||
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) &&
                                        (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling())) {
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                tile.setProbability(0.6);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling())) {
                            if (!boundaryTiles.containsKey(0.4))
                                boundaryTiles.put(0.4, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.4) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.4).add(tile);
                                tile.setProbability(0.4);
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.4);
                        } else {
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                tile.setProbability(0.0);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * This method removes the tile from the boundaryTiles list if there is already a tile with the same coordinates and a lower probability.
     * @param tile The tile to be removed.
     * @param probability The probability of the tile to be removed.
     */
    private void removeTileAlreadyExistingWithLowerProbabilityButNotZero(Tile tile, double probability) {
        boundaryTiles.forEach((key, value) -> {
            if (key < probability && key != 0.0)
                value.remove(tile);
        });
        boundaryTiles.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * This method checks if the tile not exists in the boundaryTiles list with a probability of zero.
     * @param tile The tile to be checked.
     * @return True if the tile not exists in the boundaryTiles list, false otherwise.
     */
    private boolean tileNotExistsWithProbabilityZero(Tile tile) {
        if (boundaryTiles.containsKey(0.0)) {
            return !boundaryTiles.get(0.0).contains(tile);
        } else {
            return true;
        }
    }

    /**
     * This method checks if the tile already exists in the boundaryTiles list with higher probability.
     * @param tile The tile to be checked.
     * @return True if the tile already exists in the boundaryTiles list, false otherwise.
     */
    private boolean tileAlreadyExistsWithHigherProbability(Tile tile) {
        return boundaryTiles.entrySet().stream().anyMatch(entry -> entry.getKey() > 0.0 && entry.getValue().contains(tile));
    }

    /**
     * This method checks if the tile not exists in the boundaryTiles list with the same probability.
     * @param tile The tile to be checked.
     * @param probability The probability of the tile to be checked.
     * @return True if the tile not exists in the boundaryTiles list, false otherwise.
     */
    private boolean tileNotExistsWithSameProbability(Tile tile, double probability) {
        return boundaryTiles.entrySet().stream().noneMatch(entry -> entry.getKey() == probability && entry.getValue().contains(tile));
    }
}
