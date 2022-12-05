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

    public Decision(Sensor sensor)
    {
        this.sensor = sensor;
        evaluation = 0;
    }

    public void updateEvaluation(int bonus)
    {
        evaluation += bonus;
    }
    public List<Action> makeRule ()
    {
        TreeMap<Integer,List<List<Action>>> ways = new TreeMap<>();
        System.out.println("unexplored tiles : "+ sensor.getUnexploredTiles());
        sensor.getUnexploredTiles().forEach( unexploredTile->
        {
            TreeMap<Integer,List<Action>> temp = discoverTile(unexploredTile);
            System.out.println(temp);
            int keyTemp = temp.keySet().iterator().next();
            if (ways.containsKey(keyTemp))
            {
                ways.get(keyTemp).add(temp.get(keyTemp));
            } else {
                ways.put(keyTemp, new ArrayList<>());
                ways.get(keyTemp).add(temp.get(keyTemp));
            }
        });
        Random rand = new Random();
        System.out.println(ways);
        int key = ways.lastKey();
        System.out.println("chemin selectionne : " + ways.get(key).get(rand.nextInt(ways.get(key).size())));
        return ways.get(key).get(rand.nextInt(ways.get(key).size()));

    }
    public TreeMap<Integer,List<Action>> discoverTile(Tile goal)
    {
        int score = 0;
        List<Tile> neighborGoal = sensor.getBoard().getNeighbors(goal);
        List<Tile> subgoal = new ArrayList<>();
        for (Tile tile : sensor.getDiscoveredTiles())
        {
            if(neighborGoal.contains(tile))
            {
                if (tile.isWindy() || tile.isBadSmelling())
                {
                    subgoal.add(tile);
                }
                else
                {
                    subgoal.add(tile);
                    break;
                }

            }
        }
        int lastIndex = subgoal.size()-1;
        if (subgoal.get(lastIndex).isWindy() || subgoal.get(lastIndex).isBadSmelling())
        {
            score -= 5;
        }
        List<Action> actions = bidirectionnalSearch(subgoal.get(lastIndex));
        if(subgoal.get(lastIndex).getX() < goal.getX())
        {
            actions.add(Action.MOVE_RIGHT);
        }
        else if(subgoal.get(lastIndex).getX() > goal.getX())
        {
            actions.add(Action.MOVE_LEFT);
        }
        else if(subgoal.get(lastIndex).getY() < goal.getY())
        {
            actions.add(Action.MOVE_DOWN);
        }
        else
        {
            actions.add(Action.MOVE_UP);
        }
        score += actions.size();
        TreeMap<Integer, List<Action>> way= new TreeMap<>();
        way.put(score, actions);
        return way;
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
            propagate(wayStart, alreadySeenStart);
            propagate(wayGoal, alreadySeenGoal);

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
     * @param forbiddenTiles the forbidden tiles
     */
    public void propagate(SearchTree tree, LinkedHashSet<Tile> forbiddenTiles) {
        List<SearchTree> leafs = tree.getLeaf();
        leafs.forEach(leaf -> {
            List<Tile> neighbours = sensor.getBoard().getNeighbors(leaf.getNode()).stream()
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
        List<Action> actionsList = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).getX() < path.get(i + 1).getX())
                actionsList.add(Action.MOVE_RIGHT);
            else if (path.get(i).getX() > path.get(i + 1).getX())
                actionsList.add(Action.MOVE_LEFT);
            else if (path.get(i).getY() < path.get(i + 1).getY())
                actionsList.add(Action.MOVE_DOWN);
            else if (path.get(i).getY() > path.get(i + 1).getY())
                actionsList.add(Action.MOVE_UP);
        }
        return actionsList;
    }

}
