package Engine.Tools;

import java.util.Arrays;

/**
 *  A rectangle shape, defined by its position and size,
 *  particularly useful in collision detection.
 */

public class Rectangle
{
    /**
     * x-coordinate of left edge of rectangle
     */
    public double leftX;

    /**
     * y-coordinate of top edge of rectangle
     */
    public double topY;

    /**
     * width of rectangle
     */
    public double width;

    /**
     * height of rectangle
     */
    public double height;

    /**
     * x-coordinate of right edge of rectangle
     */
    public double rightX;

    /**
     * y-coordinate of bottom edge of rectangle
     */
    public double bottomY;

    /**
     * Initialize rectangle with all values set to 0.
     */
    public Rectangle()
    {
        setValues(0,0,0,0);
    }

    /**
     * Initialize rectangle data from coordinates of top-left corner and size.
     * @param leftX x-coordinate of top-left corner (left edge) of rectangle
     * @param topY y-coordinate of top-left corner (top edge) of rectangle
     * @param width width of rectangle
     * @param height height of rectangle
     */
    public Rectangle(double leftX, double topY, double width, double height)
    {
        setValues(leftX, topY, width, height);
    }

    /**
     * Set rectangle data.
     * Used to update game entities that move and/or change size.
     * @param left x-coordinate of top-left corner (left edge) of rectangle
     * @param top y-coordinate of top-left corner (top edge) of rectangle
     * @param width width of rectangle
     * @param height height of rectangle
     */
    public void setValues(double left, double top, double width, double height)
    {
        this.leftX = left;
        this.topY = top;
        this.width  = width;
        this.height = height;
        this.rightX = left + width;
        this.bottomY = top + height;
    }

    /**
     * Used to change the size of the rectangle.
     * @param width width of rectangle
     * @param height height of recangle
     */
    public void setSize(double width, double height)
    {
        setValues(this.leftX, this.topY, width, height);
    }

    /**
     * Update rectangle data.
     * Used for game entities that move.
     * @param left x-coordinate of top-left corner (left edge) of rectangle
     * @param top y-coordinate of top-left corner (top edge) of rectangle
     */
    public void setPosition(double left, double top)
    {
        setValues(left, top, this.width, this.height);
    }

    /**
     * Determine if this rectangle overlaps with other rectangle.
     * @param other rectangle to check for overlap
     * @return true if this rectangle overlaps with other rectangle
     */
    public boolean overlaps(Rectangle other)
    {
        boolean noOverlap = (other.rightX <= this.leftX)
                || (this.rightX  <= other.leftX )
                || (other.bottomY <= this.topY)
                || (this.bottomY <= other.topY);
        return !noOverlap;
    }

    /**
     * Calculates the minimum translation vector to translate when rectangles are overlapping by
     * calculating each vector and then use Arrays.sort(); method to sort it and then return the first element.
     * @param other Other rectangle that is overlapping
     * @return minimum vector to translate
     */
    public Vector getMinimumTranslationVector(Rectangle other)
    {
        Vector[] differences = {
                new Vector(other.rightX - this.leftX, 0), //shift this rectangle to right
                new Vector(other.leftX - this.rightX, 0), //shift this rectangle to left
                new Vector(0, other.bottomY - this.topY), //shift this rectangle to down
                new Vector(0, other.topY - this.bottomY)  //shift this rectangle to up
        };

        Arrays.sort( differences );

        return differences[0];
    }
}