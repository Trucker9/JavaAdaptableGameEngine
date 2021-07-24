package Engine.Entites.Sprites;

import Engine.Tools.Rectangle;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Animation
{
    /**
     * List of pictures that make the animation.
     */
    public ArrayList<Texture> textureList;
    /**
     * duration for each frame to he displayed
     */
    public double frameDuration;
    /**
     * defines if animation is going to loop or Not.
     */
    public boolean loop;
    /**
     * storing how much time is passed.
     */
    public double elapsedTime;
    /**
     *
     */
    public boolean paused;

    /**
     * Used to copy or clone animations. initializes all to dfault values.
     */
    public Animation()
    {
        textureList = new ArrayList<Texture>();
        frameDuration = 1;
        loop = false;
        elapsedTime = 0;
        paused = false;
    }

    /**
     *
     * @param imageFileName name of the images file to be used in this animation
     * @param rows rows of the image spriteSheet.
     * @param columns columns of the image spriteSheet.
     * @param frameDuration duration for each frame to be displayed.
     * @param loop True if the animation is going to loop.
     */
    public Animation(String imageFileName,
                     int rows, int columns,
                     double frameDuration,
                     boolean loop)
    {
        textureList = new ArrayList<Texture>();

        Image image = new Image(imageFileName);

        // dividing the sprite sheet
        double frameWidth = image.getWidth()/columns;
        double frameHeight = image.getHeight()/rows;

        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < columns; x++)
            {
                Texture tex = new Texture();
                tex.image = image;

                int frameX = x * (int)frameWidth;
                int frameY = y * (int)frameHeight;

                //selecting tex region rectangle
                tex.region = new Rectangle(frameX, frameY, frameWidth, frameHeight);

                textureList.add(tex);

            }
        }

        this.frameDuration = frameDuration;
        this.loop = loop;
        this.elapsedTime = 0;
        this.paused = false;
    }

    /**
     * Returns the current frame Texture by calculating the index using passed time.
     * @return current frame Texture.
     */
    public Texture getCurrentTexture()
    {
        int textureIndex =
                (int)Math.floor(elapsedTime / frameDuration);

        // avoid out-of-bounds errors
        if (textureIndex >= textureList.size())
            textureIndex = textureList.size() - 1;

        return textureList.get(textureIndex);
    }

    /**
     * Updating the state of the animation in each frame passed.
     * @param dt time elapsed since the last frame.
     */
    public void update(double dt)
    {
        if (paused)
            return;
        //store time
        elapsedTime += dt;

        // Apply loop mechanic
        if (loop && (elapsedTime > frameDuration * textureList.size()))
            elapsedTime = 0;
    }

    /**
     * cloning all animation data except deltaTime.
     * @return cloned animation.
     */
    public Animation clone()
    {
        Animation a = new Animation();
        a.textureList = this.textureList;
        a.frameDuration = this.frameDuration;
        a.loop = this.loop;
        return a;
    }
}
