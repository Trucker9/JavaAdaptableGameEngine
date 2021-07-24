package Engine.Entites.Sprites;

import Engine.Entites.Entity;
import Engine.Tools.Rectangle;
import Engine.Tools.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Sprite extends Entity
{

    /**
     * sprite location in game world
     */
    public Vector position;

    public Vector desiredLocation;

    /**
     * angle of rotation (in degrees) of the texture
     */
    public double angle;

    /** reflect along x direction*/
    public boolean mirrored;

    /** reflect along y direction */
    public boolean flipped;
    /**
     * image displayed when rendering this sprite
     */

    public Texture texture;
    /**
     * amount of transparency; value from 0.0 (fully transparent) to 1.0 (fully opaque)
     */
    public double opacity;
    /**
     * shape used for collision
     */
    public Rectangle boundary;

    /**
     * width of sprite
     */
    public double width;

    /**
     * height of sprite
     */
    public double height;

    /**
     * determines if sprite will be visible
     */
    public boolean visible;

    /**
     * Used to add physics to the sprite.
     */
    public Physics physics;

    /**
     * Animation object of the sprite.
     */
    public Animation animation;

    public ArrayList<Action> actionList;

    

    public boolean noOverLap;
    public Sprite()
    {

        position = new Vector();
        angle = 0;
        mirrored = false;
        flipped = false;
        opacity = 1;
        texture = new Texture();
        boundary = new Rectangle();
        visible = true;
        physics = null;
        animation = null;
        actionList = new ArrayList<>();
    }

    /**
     * set the sprite position in the game world
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     */
    public void setPosition(double x, double y)
    {
        position.setValues(x, y);
        boundary.setPosition(x , y);
    }
    /**
     * Move this sprite by the specified amounts.
     * @param dx amount to move sprite along x direction
     * @param dy amount to move sprite along y direction
     */
    public void moveBy(double dx, double dy)
    {
        Vector v = new Vector(dx,dy);
        position.addToCoordinates(dx, dy);



       setAngle(Math.atan((dy/dx)));

    }

    /**
     * Sents the angle parameter of the sprite
     * @param a angle to set for the sprite
     *   angle = 0  -> point to Right
     *   angle = 90 -> point to Down
     */
    public void setAngle(double a)
    {
        angle = a;
    }

    /**
     * Rotate sprite by the specified angle PER FRAME.
     * @param da the angle (in degrees) to rotate this sprite
     */
    public void rotateBy(double da)
    {
        angle += da;
    }

    /**
     * Move sprite by the specified distance at the specified angle.
     * @param dist the distance to move this sprite
     * @param a the angle (in degrees) along which to move this sprite
     */
    public void moveAtAngle(double dist, double a)
    {
        double A = Math.toRadians(a);
        double dx = dist * Math.cos(A);
        double dy = dist * Math.sin(A);
        moveBy( dx, dy );
    }
    /**
     * Move sprite forward by the specified distance at current angle.
     * @param dist the distance to move this sprite
     */
    public void moveForward(double dist)
    {
        moveAtAngle(dist, angle);
    }
    /**
     * set the texture data used when drawing this sprite;
     * also sets width and height of sprite
     * @param tex texture data
     */
    public void setTexture(Texture tex)
    {
        texture = tex;
        width = texture.region.width;
        height = texture.region.height;
        // If we change the size of the sprite, we want to change the boundary rectangle as well.
        boundary.setSize(width , height );
    }

    /**
     * set the width and height of this sprite; (if it was larger than it should be)
     * used for drawing texture and collision rectangle
     * @param width sprite width
     * @param height sprite height
     */
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        // If we change the size of the sprite, we want to change the boundary rectangle as well.
        boundary.setSize(width, height);
    }
    /**
     * Since the physics object is null by default, this method will add up the physics to the sprite.
     */
    public void setPhysics(Physics phys)
    {
        physics = phys;
    }

    /**
     * Initializes animation object of the class.
     * @param anim animation to set.
     */
    public void setAnimation(Animation anim)
    {
        animation = anim;
        width = anim.getCurrentTexture().region.width;
        height = anim.getCurrentTexture().region.height;
        boundary.setSize(width, height);
    }
    public void setOpacity(double o){
        if(o > 1 || o < 0 ) return;
        this.opacity = o ;
    }
    /**
     * Checks of two sprites are overlapping by checking the boundaries overlapping
     * @param other Sprite that we want to check overlapping with.
     * @return True if they overlap, False if not.
     */
    public boolean overlaps(Sprite other)
    {
        return this.getBoundary().overlaps( other.getBoundary() );
    }

    /**
     * If we call this method in update function,  it Prevents over lapping and creates solid object,
     * does this by changing the position of the sprite in the time of overlapping.
     * @param other the sprite that we wish to prevent from overlapping.
     */
    public void preventOverlap(Sprite other)
    {
        if(noOverLap) return;
        if (this.overlaps(other))
        {
            Vector mtv = this.getBoundary()
                    .getMinimumTranslationVector( other.getBoundary() );
            this.position.addVector(mtv);
        }
    }


    public void boundToScreen(int screenWidth, int screenHeight)
    {

        if (position.x < width/2) position.x = width/2; //passing left edge (position.x -> x-coordinate of the center point of the image)
                                                        // so  "centerX  -  width/2  = leftEdgeX

        if (position.y < height/2) position.y = height/2; //passing top
        if (position.x + width/2 > screenWidth) position.x = screenWidth - width/2; //passing right
        if (position.y + height/2 > screenHeight) position.y = screenHeight - height/2; //passing bottom
    }
    public void wrapToScreen(int screenWidth, int screenHeight)
    {
        if (position.x + width/2 < 0)
            position.x = screenWidth + width/2;
        if (position.x - width/2 > screenWidth)
            position.x = -width/2;
        if (position.y + height/2 < 0)
            position.y = screenHeight + height/2;
        if (position.y - height/2 > screenHeight)
            position.y = -height/2;
    }


    public void addAction(Action a)
    {
        actionList.add(a);
    }

    public void update(double dt)
    {

        updatePhysics(dt);
        updateAnimation(dt);
        updateAction(dt);

    }



    /**
     * draw this sprite on the canvas
     * @param context GraphicsContext object that handles drawing to the canvas
     */
    public void draw(GraphicsContext context)
    {


        // if sprite is not visible, exit method
        if (!this.visible)
            return;

        // apply rotation and translation to image
        double A = Math.toRadians(angle);
        double cosA = Math.cos(A);
        double sinA = Math.sin(A);

        //flipping or mirroring
        double scaleX = 1;
        if (mirrored)
            scaleX = -1;

        double scaleY = 1;
        if (flipped)
            scaleY = -1;



        // apply rotation and translation to image
        context.setTransform(
                scaleX * cosA,    scaleX * sinA,
                scaleY * (-sinA), scaleY * cosA,
                position.x, position.y );

        // Setting opacity
        context.setGlobalAlpha( opacity );


        // and destination rectangle region of canvas
        context.drawImage( texture.image, //selecting the image

                //part of the image that we want to be drawn (a rectangle).
                texture.region.leftX, texture.region.topY,   // Start point of the rectangle.
                texture.region.width, texture.region.height, // Into this size.

                //maps what we selected above to this rectangle on the screen
                -this.width/2, -this.height/2,    // Start point ( in this case is middle of screen).
                this.width, this.height );               // Into this size.
                                                         // Now the center of any sprite is aligned with origin (0,0) of the canvas

        context.setStroke(Color.BLACK);
        context.stroke();

    }

    /**
     * getter for boundary rectangle
     * @return boundary rectangle of the sprite
     */
    public Rectangle getBoundary()
    {   //just to avoid any errors.
        boundary.setPosition( position.x, position.y );
        return boundary;
    }

    /**
     * Updating Physics class status in each frame.
     * @param dt elapsed time since last frame.
     */
    private void updatePhysics(double dt){
        if (physics != null)
        {
            // Passing current data to physics class
            physics.position.setValues(
                    this.position.x, this.position.y );

            // Update values within the physics class
            physics.update(dt);

            //setting back values calculated in the physics class
            this.position.setValues(
                    physics.position.x, physics.position.y );



        }
    }
    /**
     * Updating Animation class status in each frame.
     * @param dt elapsed time since last frame.
     */
    private void updateAnimation(double dt){
        if (animation != null){
            animation.update(dt);

            //setting the texture of this object to be shown in each frame.
            texture = animation.getCurrentTexture();
        }
    }

    boolean finished;
    /**
     * Updating Action class status in each frame.
     * @param dt elapsed time since last frame.
     */
    private void updateAction(double dt){





        if(action != null && !finished ){
            finished = action.apply(this,dt);
        }



    }





}


