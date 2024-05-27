## KEN_PROJECT24_2024: Golf Simulation

### Overview

KEN_PROJECT24_2024 is a JavaFX application designed to simulate putting in golf. It aims to replicate the excitement and challenges of real-life putting experiences, allowing players to enjoy the game from the comfort of their own space. The program incorporates realistic physics, terrain modeling, and gameplay mechanics to provide an immersive gaming experience.

### Features

1. **Putting Simulation**: Players can simulate putting strokes using realistic physics based on Newton's second law.
2. **Custom Putting Greens**: Users have the ability to create custom putting greens with various terrains, including hills, obstacles, and water bodies.
3. **Player Modes**: The application supports both single-player mode against an AI opponent (bot) and multiplayer mode for players to compete against each other.
4. **Scoring System**: Players aim to guide the ball into the target within the fewest possible strokes. Falling into water incurs a one-stroke penalty.
5. **Terrain Modeling**: The terrain is described by a height function, incorporating slopes, obstacles, and bodies of water.

### Modules

The project is divided into four main modules following a Domain-Driven Design (DDD) approach:

1. **Presentation**: Responsible for the user interface components, including JavaFX controllers and views.

2. **Domain**: Contains the core business logic and domain entities, such as the GolfBall and Terrain classes, defining the rules and behaviors of the game.

3. **Application**: Acts as an intermediary between the Presentation and Domain layers, handling user interactions and coordinating gameplay.

4. **Infrastructure**: Provides support for technical aspects, such as data access and external integrations, though not extensively used in this project.

### Physics

The motion of the golf ball is governed by Newton's second law and is influenced by gravity, normal force, and friction. The program approximates the physics of real-life putting, considering factors such as slope and surface friction.

### TerrainA

The terrain is described by a height function, and additional obstacles like water bodies, sand pits, and trees can be optionally included. The terrain's characteristics, including friction coefficients, affect the motion of the ball.

### Gameplay

Players aim to guide the ball to the target within the fewest strokes possible, avoiding obstacles and water hazards. Falling into water incurs a penalty stroke.

### Contribution

Contributions to the project are welcome. Please refer to the guidelines in the README for instructions on how to contribute.

### License

KEN_PROJECT24_2024 is licensed under the MIT License. See the LICENSE file for details.

### References

- [1, Rule 26]: Detailed regulations for golf gameplay.
