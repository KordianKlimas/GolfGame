package com.kentwentyfour.project12;


import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;

public class Player {
    private String name;
    private GolfBall golfBall;

    public Player(String name, GolfBall golfBall) {
        this.name = name;
        this.golfBall = golfBall;
    }

    public String getName() {
        return name;
    }

    public GolfBall getGolfBall() {
        return golfBall;
    }
}