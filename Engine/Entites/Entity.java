package Engine.Entites;

import javafx.scene.canvas.GraphicsContext;

/**
 * Base class for all objects or collections of objects
 * that can be rendered to the screen.
 */
public abstract class Entity
{
    /**
     * Render this Entity to a canvas. 
     * @param context GraphicsContext object that handles drawing to the canvas
     */
    public abstract void draw(GraphicsContext context);

    /**
     * Used to update individual sprites or entities within the game.
     * so this method is actually individual update method for entities.
     * @param dt elapsed time (seconds) since previous iteration of game loop
     * (typically approximately 1/60 second)
     */
    public abstract void update(double dt);
}