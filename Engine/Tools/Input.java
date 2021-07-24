package Engine.Tools;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * A structure for storing and updating keyboard state:
 *  which keys are currently pressed or just pressed/released.
 *  //TODO :  mouse events can be added here later
 */
public class Input
{
    public ArrayList<String> justPressedQueue;
    public ArrayList<String> justReleasedQueue;
    public ArrayList<String> justPressedList;
    public ArrayList<String> stillPressedList;
    public ArrayList<String> justReleasedList;
    private final LinkedList<Vector> clicked;

    /**
     * Initialize object and activate event listeners.
     * @param listeningScene the window Scene that has focus during the game
     */
    public Input(Scene listeningScene)
    {
        justPressedQueue  = new ArrayList<String>();
        justReleasedQueue = new ArrayList<String>();
        //data of the Queues above will be processed using below lists
        justPressedList   = new ArrayList<String>();
        stillPressedList  = new ArrayList<String>();
        justReleasedList  = new ArrayList<String>();

        clicked = new LinkedList<>();
        // We want to call a method onKeyPressed.
        // Example Strings: UP, LEFT, Q, DIGIT1, SPACE, SHIFT
        /*Here when a key is pressed, it takes the KeyEvent input and runs those lines of codes
        * actually because that onKeyPressed requires an object as input parameter, we do this kind of writing
        * later in compile time it wraps it in a class and then calls the method on that particular class. */

        //this methods are listeners, when they execute in constructor, they are on going till end.
        listeningScene.setOnKeyPressed(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString();
                    justPressedQueue.add(keyName); // We will process this later in update();
                }
        );

        listeningScene.setOnKeyReleased(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString();
                    justReleasedQueue.add(keyName);
                }
        );


        listeningScene.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        clicked.addLast(new Vector(mouseEvent.getSceneX(),mouseEvent.getSceneY()));
                    }
                }
        );


    }   //end of constructor



    /**
     *  Update input state information.
     *  Automatically called by {@link Game} class during the game loop.
     */
    public void update()
    //the listening methods in the constructor gathers data for this method.
    //also this method will be executed in each frame
    {
        // clear previous discrete event status
        justPressedList.clear();
        justReleasedList.clear();

        // update current event status
        for (String keyName : justPressedQueue)
        {
            // avoid multiple keypress events while holding key
            // avoid duplicate entries in key pressed list
            if ( !stillPressedList.contains(keyName) )
            {
                justPressedList.add(keyName);
                stillPressedList.add(keyName);
            }
        }

        for (String keyName : justReleasedQueue)
        {
            stillPressedList.remove(keyName);
            justReleasedList.add(keyName);
        }

        // clear the queues (not lists)  used to store events
        justPressedQueue.clear();
        justReleasedQueue.clear();
    }

    /**
     * Determine if key has been pressed / moved to down position (a discrete action).
     * @param keyName name of corresponding key (examples: "LEFT", "A", "DIGIT1", "SPACE", "SHIFT")
     * @return true if key was just pressed
     */
    public boolean isKeyJustPressed(String keyName)
    {  return justPressedList.contains(keyName);  }

    /**
     * Determine if key is currently being pressed / held down (a continuous action).
     * @param keyName name of corresponding key (examples: "LEFT", "A", "DIGIT1", "SPACE", "SHIFT")
     * @return true if key is currently pressed
     */
    public boolean isKeyStillPressed(String keyName)  //still was not here
    {  return stillPressedList.contains(keyName);  }

    /**
     * Determine if key has been released / returned to up position (a discrete action).
     * @param keyName name of corresponding key (examples: "LEFT", "A", "DIGIT1", "SPACE", "SHIFT")
     * @return true if key was just released
     */
    public boolean isKeyJustReleased(String keyName)
    {  return justReleasedList.contains(keyName);  }


    public Vector getNextClick(){
        if(clicked.isEmpty()) return null;
        else {
            Vector res = clicked.getFirst();
            clicked.removeFirst();
            return res;

        }
    }

}

