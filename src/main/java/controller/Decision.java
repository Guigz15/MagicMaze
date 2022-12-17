package controller;

import lombok.Getter;
import lombok.Setter;
import model.Action;
import model.SearchTree;
import model.Tile;

import java.util.*;
import java.util.stream.Collectors;

public class Decision {

    @Getter @Setter
    private Sensor sensor;
    @Getter @Setter
    private int evaluation;

    public Decision(Sensor sensor) {
        this.sensor = sensor;
        evaluation = 0;
    }

    public void updateEvaluation(int bonus) {
        evaluation += bonus;
    }

    /**
     * This method implements the bidirectional search algorithm
     * @param goal the goal tile
     * @return TreeMap with the evaluation and the list of actions
     */
    public List<Action> bidirectionnalSearch(Tile goal) {
        Tile communTile = null;
        LinkedHashSet<Tile> alreadySeenStart = new LinkedHashSet<>();
        LinkedHashSet<Tile> alreadySeenGoal = new LinkedHashSet<>();
        LinkedHashSet<SearchTree> leafStart = new LinkedHashSet<>();
        LinkedHashSet<SearchTree> leafGoal = new LinkedHashSet<>();
        SearchTree wayStart = new SearchTree(sensor.getTile());
        SearchTree wayGoal = new SearchTree(goal);
        alreadySeenStart.add(sensor.getTile());
        alreadySeenGoal.add(goal);
        boolean wayFinded = false;
        while (!wayFinded) {
            // Propagation of start and final tree
            propagate(wayStart);
            propagate(wayGoal);

            // Update of the forbiddenStart list and wayStart tree
            leafStart.clear();
            leafStart.addAll(wayStart.getLeaf());
            leafGoal.clear();
            leafGoal.addAll(wayGoal.getLeaf());
            for (SearchTree leaf : leafStart)
                alreadySeenStart.add(leaf.getNode());
            for (SearchTree leaf : leafGoal) {
                alreadySeenGoal.add(leaf.getNode());
            }
            communTile = wayStart.hasCommunNode(wayGoal);
            if (communTile != null)
                wayFinded = true;
        }

        // Get the way to the commun tile to link the two trees
        List<Tile> firstPart = wayStart.getWayTo(communTile);
        List<Tile> secondPart = wayGoal.getWayTo(communTile);
        Collections.reverse(secondPart);
        secondPart.remove(0);
        firstPart.addAll(secondPart);
        return convertPathToActions(firstPart);
    }

    /**
     * This method is used to propagate the search tree
     * @param tree the search tree
     */
    public void propagate(SearchTree tree) {
        List<SearchTree> leafs = tree.getLeaf();
        leafs.forEach(leaf -> {
            List<Tile> neighbours = sensor.getBoard().getNeighbours(leaf.getNode()).stream()
                    .filter((neighbour -> sensor.getDiscoveredTiles().contains(neighbour))).collect(Collectors.toList());
            leaf.addSons(neighbours);
        });
    }

    /**
     * This method is used to convert a path to a list of actions
     * @param path list of tiles
     * @return TreeMap with the evaluation and the list of actions
     */
    public List<Action> convertPathToActions(List<Tile> path) {
        List<Double> monsterProbabilities = new ArrayList<>(Arrays.asList(0.2, 0.5, 0.7, 0.9));
        List<Action> actionsList = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            double probability = path.get(i + 1).getProbability();
            if (path.get(i).getX() < path.get(i + 1).getX()) {
                if (monsterProbabilities.contains(probability) && i == path.size() - 2) {
                    actionsList.add(Action.THROW_RIGHT);
                }
                actionsList.add(Action.MOVE_RIGHT);
            } else if (path.get(i).getX() > path.get(i + 1).getX()) {
                if (monsterProbabilities.contains(probability) && i == path.size() - 2) {
                    actionsList.add(Action.THROW_LEFT);
                }
                actionsList.add(Action.MOVE_LEFT);
            } else if (path.get(i).getY() < path.get(i + 1).getY()) {
                if (monsterProbabilities.contains(probability) && i == path.size() - 2) {
                    actionsList.add(Action.THROW_DOWN);
                }
                actionsList.add(Action.MOVE_DOWN);
            } else if (path.get(i).getY() > path.get(i + 1).getY()) {
                if (monsterProbabilities.contains(probability) && i == path.size() - 2) {
                    actionsList.add(Action.THROW_UP);
                }
                actionsList.add(Action.MOVE_UP);
            }
        }

        return actionsList;
    }

    public List<Action> makeRule() {
        TreeMap<Double, List<Tile>> boundaryTiles = sensor.getBoundaryTiles();
        List<Tile> tiles = boundaryTiles.firstEntry().getValue();
        Random rand = new Random();
        return bidirectionnalSearch(tiles.get(rand.nextInt(tiles.size())));
    }

    public void nextLevel() {
        updateEvaluation(10 * sensor.getBoard().getHeight() * sensor.getBoard().getWidth());
    }
}
