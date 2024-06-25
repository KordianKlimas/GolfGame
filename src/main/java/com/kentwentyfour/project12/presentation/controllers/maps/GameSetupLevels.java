package com.kentwentyfour.project12.presentation.controllers.maps;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.ObstacleArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.Tree;

import java.util.*;

public class GameSetupLevels {
    private static final Map<String, GameSetupVariables> predefinedSets = new HashMap<>();

    /**
     * initializes  predefined maps and condition for user to choose from
     */
    public static void initializePredefinedSets() {
        predefinedSets.put("TestMap_1 (flat)", new GameSetupVariables( -2,-4.0, 0, 0, 0.3, 0.550,0.2, 0.1, 0.1, 0.05, "1"));
        predefinedSets.put("TestMap_2 (lake in middle)", new GameSetupVariables( -3.0, -3.0, 3.0, 3.0, 0.1, 0.2, 0.3, 0.15, 0.15, 0.1, "0.4 * ( 0.9 -  2.718 ^ ( (  x ^ 2 + y ^ 2 ) / -8 ) )"));
        predefinedSets.put("TestMap_3 (beach)", new GameSetupVariables( 2.0, 2.0, 3.0, 3.0, 0.1, 0.2, 0.3, 0.15, 0.15, 0.1, "sin( ( x - y ) / 7 ) + 0.5 "));
        predefinedSets.put("TestMap_4 (desert oasis)", new GameSetupVariables(-4, -4, 4.0, 4.0, 0.15, 0.25, 0.4, 0.2, 0.2, 0.15, " 0.17 * sin( 0.1 * x ) * sin( 0.1 * y ) - 0.12 * cos( 0.5 * x ) * cos( 0.5 * y ) + .02"));

        predefinedSets.put("test_obstacleArea_1", new GameSetupVariables( 0.0, -3.0, 0.0, 3.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-1,1,3.4,.1),
                        new Water(-4,0,4,.1),
                        new Water(-1,-1,5,.1)
                )
        ));

        predefinedSets.put("test_obstacleArea_2", new GameSetupVariables( 0.0, -3.0, 0.0, 3.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-1,1,6,.1),
                        new Water(-5,0,6,.1),
                        new Water(-1,-1,6,.1)
                )
        ));
        predefinedSets.put("test_obstacleArea_3", new GameSetupVariables( 4.0, -3.5, 0.0, 3.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-1,1,6,.1),
                        new Water(-5,0,6,.1),
                        new Water(-1,-1,6,.1),
                        new Water(3,-3,2,.1),
                        new Water(3,-3,0.1,-1),
                        new Water(2,-5,0.1,2)
                )
        ));
        predefinedSets.put("tree_test_map", new GameSetupVariables(
                -2.0, -3.0, 3.0, 3.0, 0.1, 0.2, 0.3, 0.15, 0.15, 0.1, "sin( ( x - y ) / 7 ) + 0.5",
                List.of(

                ),
                List.of(
                        new Tree(-0.0, -1, 0.5),
                        new Tree(2.5, -1.0, 0.5),
                        new Tree(1.0, -3.5, 0.7),
                        new Tree(-3.5, -3.0, 0.5),
                        new Tree(-2.0, -2.0, 0.6),
                        new Tree(-2.8, -2.5, 0.5),
                        new Tree(-3.5, -3.8, 0.5)
                )

        ));
        predefinedSets.put("creative_maze_1", new GameSetupVariables(-4.0, 4.5, -2.5, 2.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-4.0, -4.0, 0.5, 8.0),
                        new Water(-3.0, -4.0, 0.5, 3.0),
                        new Water(-2.0, -1.0, 0.5, 5.0),
                        new Water(-1.0, 0.0, 2.0, 0.5),
                        new Water(1.0, -4.0, 0.5, 8.0),
                        new Water(2.0, -3.0, 0.5, 3.0),
                        new Water(3.0, 0.0, 0.5, 4.0),
                        new Water(-4.0, 3.0, 3.0, 0.5),
                        new Water(-2.0, 1.0, 2.0, 0.5),
                        new Water(0.0, 3.0, 0.5, 2.0),
                        new Water(1.0, 1.0, 2.0, 0.5),
                        new Water(3.0, 1.0, 0.5, 2.0),
                        new Water(-4.0, -2.0, 3.0, 0.5),
                        new Water(1.0, -1.0, 2.0, 0.5),
                        new Water(-1.0, -3.0, 0.5, 2.0)
                )
        ));
        predefinedSets.put("creative_maze_2", new GameSetupVariables(1.0, -3.5, 2.0, 2.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-4.0, -4.0, 1.0, 0.5),
                        new Water(-3.0, -4.0, 0.5, 3.0),
                        new Water(-4.0, -1.0, 2.0, 0.5),
                        new Water(-1.0, -4.0, 0.5, 2.0),
                        new Water(-1.5, -2.5, 1.5, 0.5),
                        new Water(0.0, -4.0, 0.5, 1.5),
                        new Water(0.5, -2.5, 1.5, 0.5),
                        new Water(1.0, -1.5, 0.5, 2.0),
                        new Water(2.0, -1.5, 0.5, 1.5),
                        new Water(2.0, -4.0, 0.5, 2.5),
                        new Water(2.5, -1.0, 1.5, 0.5),
                        new Water(3.0, 0.0, 0.5, 2.0),
                        new Water(-4.0, 2.0, 0.5, 1.5),
                        new Water(-3.0, 2.5, 0.5, 2.0),
                        new Water(-4.0, 3.5, 3.0, 0.5)
                )
        ));
        predefinedSets.put("creative_maze_3", new GameSetupVariables(-4.5, 4.0, 4.0, -3.5, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "1",
                List.of(
                        new Water(-3.9, -4.0, 0.5, 8.0),
                        new Water(-3.5, -4.0, 0.5, 2.0),
                        new Water(-3.0, -2.5, 0.5, 3.0),
                        new Water(-2.0, -4.0, 0.5, 3.0),
                        new Water(-1.5, -1.0, 2.0, 0.5),
                        new Water(-1.0, -1.5, 0.5, 4.0),
                        new Water(0.0, -4.0, 0.5, 4.0),
                        new Water(0.5, -1.0, 2.0, 0.5),
                        new Water(1.5, -0.5, 0.5, 2.0),
                        new Water(2.0, 0.5, 2.0, 0.5),
                        new Water(3.0, -4.0, 0.5, 8.0),
                        new Water(2.5, -3.0, 0.5, 2.0),
                        new Water(1.0, 2.0, 2.0, 0.5),
                        new Water(-1.0, 2.0, 2.0, 0.5),
                        new Water(-2.5, 3.0, 0.5, 2.0)
                )
        ));


    }
    /**
     * Returns predefined map in form of  GameSetupVariables object
     * @param selectedLevel
     * @return
     */
    public static GameSetupVariables getVariablesForLevel(String selectedLevel) {
        return predefinedSets.get(selectedLevel);
    }
    public  static String[] getLevelNames(){
        Set<String> keySet = predefinedSets.keySet();
        String[] levelNames = keySet.toArray(new String[0]);
        Arrays.sort(levelNames);
        return levelNames;
    }
}
