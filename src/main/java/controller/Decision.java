package controller;

import lombok.Getter;
import lombok.Setter;
import model.Tile;

import java.util.ArrayList;
import java.util.List;

public class Decision {
    @Getter @Setter
    private List<Tile> discoveredTiles;
    @Getter @Setter
    private List<Tile> unexploredTiles;

    public Decision() {
        this.discoveredTiles = new ArrayList<>();
        this.unexploredTiles = new ArrayList<>();
    }

    public void clear() {
        discoveredTiles.clear();
        unexploredTiles.clear();
    }
}
