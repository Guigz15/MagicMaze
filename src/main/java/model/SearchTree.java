package model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the structure of SearchTree which is used in bidirectionalSearch
 */
public class SearchTree {
    @Getter @Setter
    private Tile node;
    @Getter @Setter
    private List<SearchTree> sonTrees;

    /**
     * SearchTree constructor
     * @param tileArg which is used to create the root of the SearchTree
     */
    public SearchTree(Tile tileArg) {
        node = tileArg;
        sonTrees = new ArrayList<>();
    }

    /**
     * Add sons on the SearchTree
     * @param sonTiles to add on the SearchTree
     */
    public void addSons(List<Tile> sonTiles)
    {
        sonTiles.forEach(sonTile -> sonTrees.add(new SearchTree(sonTile)));
    }

    /**
     * Verify if the SearchTree is Leaf (he doesn't have child)
     * @return true if the SearchTree is Leaf, else return false
     */
    public boolean isLeaf() {
        return sonTrees == null || sonTrees.size() == 0;
    }

    /**
     * Get all leaf of the SearchTree
     * @return all leaf in a List of SearchTree
     */
    public List<SearchTree> getLeaf() {
        List<SearchTree> leafs = new ArrayList<>();
        return getLeaf(leafs);
    }

    /**
     * Get all leaf of the SearchTree
     * @param leafs contains all leafs of the SearchTree, must be initialized with an empty List
     * @return all leaf in a List of SearchTree
     */
    private List<SearchTree> getLeaf(List<SearchTree> leafs) {
        if (isLeaf()) {
            leafs.add(this);
            return leafs;
        } else {
            for (SearchTree son : sonTrees)
                son.getLeaf(leafs);
        }
        return leafs;
    }

    /**
     * Verify if element is in the SearchTree
     * @param element to verify if is in the SearchTree
     * @return true if the element is in the SearchTree, else return false
     */
    public boolean contains(Tile element)
    {
        return contains(element, false);
    }

    /**
     * Verify if element is in the SearchTree
     * @param element to verify if is in the SearchTree
     * @param isHere is true if the element is finded in the SearchTree, else it is false. Must be initialized to false
     * @return true if the element is in the SearchTree, else return false
     */
    private boolean contains(Tile element, boolean isHere) {
        if (isLeaf() && !node.equals(element))
            return false;
        else if (node.equals(element))
            return true;
        else if (!getSonTrees().isEmpty()) {
            for(SearchTree son : sonTrees)
                isHere = isHere||son.contains(element, isHere);
            return isHere;
        }
        return false;
    }

    /**
     * Get all the nodes of the SearchTree
     * @return all the nodes of the SearchTree in a List of SearchTree
     */
    public List<Tile> getAllNodes() {
        List<Tile> nodes = new ArrayList<>();
        return getAllNodes(nodes);
    }

    /**
     * Get all the nodes of the SearchTree
     * @param nodes contains nodes of the SearchTree. Must be initialized with empty List of Tile.
     * @return all the nodes of the SearchTree in a List of SearchTree
     */
    private List<Tile> getAllNodes(List<Tile> nodes)  {
        if (isLeaf()) {
            nodes.add(node);
            return nodes;
        } else {
            nodes.add(node);
            for (SearchTree son : sonTrees)
                son.getAllNodes(nodes);
            return nodes;
        }
    }

    /**
     * Verify if two SearchTree have a node in commun
     * @param treeToCompare SearchTree to verify for a commun node
     * @return true if the two SearchTree have a node in commun, else return false
     */
    public Tile hasCommunNode(SearchTree treeToCompare)  {
        List<Tile> nodes = getAllNodes();
        List<Tile> nodesToCompare = treeToCompare.getAllNodes();
        for (Tile node : nodes) {
            for (Tile nodeToCompare : nodesToCompare) {
                if (node.equals(nodeToCompare))
                    return node;
            }
        }
        return null;
    }

    /**
     * Get a way between the root and a goal
     * @param element goal, end point of the way
     * @return a way between the root and the goal in List of Tile
     */
    public List<Tile>getWayTo(Tile element)   {
        List<Tile> way = new ArrayList<>();
        List<Boolean> wayIsFind = new ArrayList<>();
        wayIsFind.add(false);
        return getWayTo(element, way, wayIsFind);
    }

    /**
     * Get a way between the root and a goal
     * @param element goal, end point of the way
     * @param way List of Tile that contains the way beetween the root and the goal. Must be initialized with an empty List
     * @param wayIsFind is true if the goal is finded, else it is false. Must be initialized to false
     * @return a way between the root and the goal in List of Tile
     */
    private List<Tile> getWayTo(Tile element, List<Tile> way, List<Boolean> wayIsFind)  {
        if(node.equals(element)) {
            way.add(node);
            wayIsFind.set(0, true);
            return way;
        }

        if (contains(element)) {
            way.add(node);
            for (SearchTree sonTree : sonTrees) {
                if (!wayIsFind.get(0) && sonTree.contains(element))
                    sonTree.getWayTo(element, way, wayIsFind);
            }
        }
        return way;
    }
}
