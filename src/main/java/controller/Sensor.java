package controller;

import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.Setter;
import model.Board;
import model.Tile;

import java.util.*;
import java.util.function.BiConsumer;

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

    public void nextLevel() {
        discoveredTiles.clear();
        discoveredTiles.add(tile);
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(3);
        boundaryTiles.clear();
        initBoundaryTiles();
    }

    public void update() {
        discoveredTiles.add(tile);
        tile.setStroke(Paint.valueOf("green"));
        tile.setStrokeWidth(3);
        boundaryTiles.firstEntry().getValue().remove(tile);
        boundaryTiles.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        initBoundaryTiles();
    }

    private void initBoundaryTiles() {
        board.getNeighbours(tile).forEach(neighbour -> {
            if (!discoveredTiles.contains(neighbour)) {
                if (tile.isWindy() || tile.isBadSmelling()) {
                    if (!boundaryTiles.containsKey(0.2))
                        boundaryTiles.put(0.2, new ArrayList<>());

                    // Add only if the neighbour is not already in the boundary tiles with a lower probability
                    if (tileNotExistsWithSameProbability(neighbour, 0.2) && tileNotExistsWithProbabilityZero(neighbour)) {
                        boundaryTiles.get(0.2).add(neighbour);
                        neighbour.setStroke(Paint.valueOf("blue"));
                        neighbour.setStrokeWidth(3);
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
                    }
                }
            }
        });

        computeProbabilities(boundaryTiles);
    }

    public void computeProbabilities(TreeMap<Double, List<Tile>> boundaryTiles) {
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
                            System.out.println("Cas 1 avec vent ou mauvaise odeur " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.2");
                            if (!boundaryTiles.containsKey(0.2))
                                boundaryTiles.put(0.2, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.2) && tileNotExistsWithProbabilityZero(tile))
                                boundaryTiles.get(0.2).add(tile);
                        } else {
                            System.out.println("Cas 1 sans vent ni mauvaise odeur " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.0");
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                                System.out.println("Suppression");
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                System.out.println("Ajout");
                            }
                        }
                    } else if (discoveredNeighbours.size() == 2) {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy()) {
                            System.out.println("Cas 2 avec vent sur les 2 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.6");
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling()) {
                            System.out.println("Cas 2 avec mauvaise odeur sur les 2 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.6");
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling())) {
                            System.out.println("Cas 2 avec vent ou mauvaise odeur sur un seul voisin " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.4");
                            if (!boundaryTiles.containsKey(0.4))
                                boundaryTiles.put(0.4, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.4) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.4).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.4);
                        } else {
                            System.out.println("Cas 2 sans vent ni mauvaise odeur " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.0");
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                                System.out.println("Suppression");
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                System.out.println("Ajout");
                            }
                        }
                    } else if (discoveredNeighbours.size() == 3) {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        Tile discoveredNeighbour3 = discoveredNeighbours.get(2);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy() && discoveredNeighbour3.isWindy()) {
                            System.out.println("Cas 3 avec vent sur les 3 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition()  + " avec proba 0.8");
                            if (!boundaryTiles.containsKey(0.8))
                                boundaryTiles.put(0.8, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.8) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.8).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.8);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling() && discoveredNeighbour3.isBadSmelling()) {
                            System.out.println("Cas 3 avec mauvaise odeur sur les 3 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition()  + " avec proba 0.8");
                            if (!boundaryTiles.containsKey(0.8))
                                boundaryTiles.put(0.8, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.8) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.8).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.8);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) &&
                                        (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling())) {
                            System.out.println("Cas 3 avec vent ou mauvaise odeur sur 2 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.6");
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling())) {
                            System.out.println("Cas 3 avec vent ou mauvaise odeur sur un seul voisin " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.5");
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.5);
                            if (!boundaryTiles.containsKey(0.5))
                                boundaryTiles.put(0.5, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.5) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.5).add(tile);
                                System.out.println("Ajout");
                            }
                        } else {
                            System.out.println("Cas 3 sans vent ni mauvaise odeur " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.0");
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                                System.out.println("Suppression");
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                System.out.println("Ajout");
                            }
                        }
                    } else {
                        Tile discoveredNeighbour1 = discoveredNeighbours.get(0);
                        Tile discoveredNeighbour2 = discoveredNeighbours.get(1);
                        Tile discoveredNeighbour3 = discoveredNeighbours.get(2);
                        Tile discoveredNeighbour4 = discoveredNeighbours.get(3);
                        if (discoveredNeighbour1.isWindy() && discoveredNeighbour2.isWindy() && discoveredNeighbour3.isWindy() && discoveredNeighbour4.isWindy()) {
                            System.out.println("Cas 4 avec vent sur les 4 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 1.0");
                            if (!boundaryTiles.containsKey(1.0))
                                boundaryTiles.put(1.0, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 1.0) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(1.0).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 1.0);
                        } else if (discoveredNeighbour1.isBadSmelling() && discoveredNeighbour2.isBadSmelling() && discoveredNeighbour3.isBadSmelling() && discoveredNeighbour4.isBadSmelling()) {
                            System.out.println("Cas 4 avec mauvaise odeur sur les 4 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 1.0");
                            if (!boundaryTiles.containsKey(1.0))
                                boundaryTiles.put(1.0, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 1.0) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(1.0).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 1.0);
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
                            System.out.println("Cas 4 avec vent ou mauvaise odeur sur 3 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.8");
                            if (!boundaryTiles.containsKey(0.8))
                                boundaryTiles.put(0.8, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.8) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.8).add(tile);
                                System.out.println("Ajout");
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
                            System.out.println("Cas 4 avec vent ou mauvaise odeur sur 2 voisins " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.6");
                            if (!boundaryTiles.containsKey(0.6))
                                boundaryTiles.put(0.6, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.6) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.6).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.6);
                        } else if ((discoveredNeighbour1.isWindy() || discoveredNeighbour1.isBadSmelling()) ||
                                (discoveredNeighbour2.isWindy() || discoveredNeighbour2.isBadSmelling()) ||
                                (discoveredNeighbour3.isWindy() || discoveredNeighbour3.isBadSmelling()) ||
                                (discoveredNeighbour4.isWindy() || discoveredNeighbour4.isBadSmelling())) {
                            System.out.println("Cas 4 avec vent ou mauvaise odeur sur 1 voisin " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.4");
                            if (!boundaryTiles.containsKey(0.4))
                                boundaryTiles.put(0.4, new ArrayList<>());
                            if (tileNotExistsWithSameProbability(tile, 0.4) && tileNotExistsWithProbabilityZero(tile)) {
                                boundaryTiles.get(0.4).add(tile);
                                System.out.println("Ajout");
                            }
                            removeTileAlreadyExistingWithLowerProbabilityButNotZero(tile, 0.4);
                        } else {
                            System.out.println("Cas 4 sans vent ni mauvaise odeur " + "Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec proba 0.0");
                            if (!boundaryTiles.containsKey(0.0))
                                boundaryTiles.put(0.0, new ArrayList<>());
                            if (tileAlreadyExistsWithHigherProbability(tile)) {
                                boundaryTiles.values().forEach(tiles1 -> tiles1.remove(tile));
                                System.out.println("Suppression");
                            }
                            if (tileNotExistsWithSameProbability(tile, 0.0)) {
                                boundaryTiles.get(0.0).add(tile);
                                System.out.println("Ajout");
                            }
                        }
                    }
                });
            }
        });
    }

    private void removeTileAlreadyExistingWithLowerProbabilityButNotZero(Tile tile, double probability) {
        boundaryTiles.forEach((key, value) -> {
            if (key < probability && key != 0.0) {
                value.remove(tile);
                System.out.println("Suppression Tile: " + tile.getXPosition() + " " + tile.getYPosition() + " avec probabilité: " + key);
            }
        });
        boundaryTiles.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private boolean tileNotExistsWithProbabilityZero(Tile tile) {
        if (boundaryTiles.containsKey(0.0)) {
            return !boundaryTiles.get(0.0).contains(tile);
        } else {
            return true;
        }
    }

    private boolean tileAlreadyExistsWithHigherProbability(Tile tile) {
        return boundaryTiles.entrySet().stream().anyMatch(entry -> entry.getKey() > 0.0 && entry.getValue().contains(tile));
    }

    private boolean tileNotExistsWithSameProbability(Tile tile, double probability) {
        return boundaryTiles.entrySet().stream().noneMatch(entry -> entry.getKey() == probability && entry.getValue().contains(tile));
    }
}
