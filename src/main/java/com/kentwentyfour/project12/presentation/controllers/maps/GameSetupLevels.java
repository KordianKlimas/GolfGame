package com.kentwentyfour.project12.presentation.controllers.maps;

import java.util.HashMap;
import java.util.Map;

public class GameSetupLevels {
    private static final Map<String, GameSetupVariables> predefinedSets = new HashMap<>();

    /**
     * initializes  predefined maps and condition for user to choose from
     */
    public static void initializePredefinedSets() {
        predefinedSets.put("TestMap_1", new GameSetupVariables(
                1.0, 1.0, 2.0, 2.0, 0.05, 0.15, 0.2, 0.1, 0.1, 0.05, "sin( ( x - y ) / 7 ) + 0.5 "));
        predefinedSets.put("TestMap_2", new GameSetupVariables(
                2.0, 2.0, 3.0, 3.0, 0.1, 0.2, 0.3, 0.15, 0.15, 0.1, "cos( x + y ) - 0.3 "));
        predefinedSets.put("TestMap_3", new GameSetupVariables(
                3.0, 3.0, 4.0, 4.0, 0.15, 0.25, 0.4, 0.2, 0.2, 0.15, "tan( x * y ) + 0.2 "));
    }

    /**
     * Returns predefined map in form of  GameSetupVariables object
     * @param selectedLevel
     * @return
     */
    public static GameSetupVariables getVariablesForLevel(String selectedLevel) {
        return predefinedSets.get(selectedLevel);
    }
}
