package Engine.Entites.Sprites;

import Engine.Tools.Vector;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * How will action be applied to a sprite ? By adding the Action object to a sprite, the Action object
 * will run the method that is stored in Function interface!
 */
public class Action
{
    public ArrayList<Action> actionQueue;
    /**
     * we cant pass code to an object directly, so we use an interface, this interface is called Function and has a method,
     * by creating instances of this interface and passing them to the Action, we actually passed some lines of code
     * to the Action object
     */
    public interface Function
    {
        /**
         * By implementing an action here, we can apply it to different sprites.
         * @param target Target sprite to apply the action.
         * @param deltaTime elapsed time since last frame.
         * @param totalTime duration of action ( I think ).
         * @return True if the action is done.
         */
        boolean run(Sprite target, double deltaTime, double totalTime);
    }

    /**
     * each Action is going to store bunch of code in function.
     */
    public Function function;
    /**
     * stores the total time that's elapsed since the action started.
     */
    public double totalTime;

    /**
     * default constructor.
     * We can use this and override the apply(); if we wish not to use Function interface.
     */
    public Action()
    {
        actionQueue = new ArrayList<>();
        totalTime = 0;
    }

    public void addActionToQueue(Action a){
        actionQueue.add(a);
    }
    /**
     * constructor to supply the function.
     * @param f Function object to pass to function field of the action.
     */
    public Action(Function f)
    {
        function = f;
        totalTime = 0;
    }

    /**
     * activating the run(); that is stored in Function.
     * Action is going to apply itself by running function.run(Sprite,double,double); on the "target" Sprite.
     * @param target Sprite to apply the action to
     * @param deltaTime used to increment to total time.
     * @return True if action is completed.
     */
    public boolean apply(Sprite target, double deltaTime)
    {
        totalTime += deltaTime;

        return function.run(target, deltaTime, totalTime);
    }

    /**
     * Used to reset an action. (used if want to repeat an action).
     */
    public void reset()
    {
        totalTime = 0;
    }
    //-------------------------------------------------------------------------- Normal Actions ::
    // static methods to create Action object :
    //They create an action by returning an Action object that defines the Function which
    //is going to be applied to our target sprite.

    /**
     * Static method to move a Sprite.
     * @param deltaX amount of movement.
     * @param deltaY amount of movement.
     * @param duration duration of movement.
     * @return Action object that its Function interface is set to move the sprite as we wished.
     */
    public static Action moveByAct(
            double deltaX, double deltaY, double duration)
    {
        return new Action(
                (Sprite target, double dt, double tt) ->
                {

                    target.moveBy(deltaX / duration * dt, //speed * elapsedTime(since lastFrame) = how much we need to move in this frame.
                            deltaY / duration * dt);
                    //System.out.println("tt = " + tt + "    duration " + duration);
                    return (tt >= duration); // if the run method is running for longer than the duration of it,
                                             // we now that its done and will return true
                }
        );
    }

    /**
     * Static method to apply rotating action.
     * @param deltaA Rotation degree.
     * @param duration Time that rotation takes.
     * @return an Action object that implemented the rotation.
     */
    public static Action rotateByAct(double deltaA, double duration)
    {

        return new Action(
                (Sprite target, double dt, double tt) ->
                {
                    target.rotateBy(deltaA / duration * dt); // rotationSpeed * elapsedTime(since lastFrame) =
                                                                 // how much to rotate in this new frame

                    return (tt >= duration); // if the run method is running for longer than the duration of it,
                                             // we now that its done and will return true
                }
        );
    }

    /**
     * Face out action.
     * @param duration fading duration.
     * @return an Action that has implemented the fading.
     */
    public static Action fadeOut(double duration)
    {
        return new Action(
                (Sprite target, double dt, double tt) ->
                {
                    target.opacity -= (1 / duration * dt); //how much to decrement opacity in each frame
                    //avoiding errors
                    if (target.opacity < 0)
                        target.opacity = 0;
                    return (tt >= duration);
                }
        );
    }

    /**
     * Used to add a delay in action sequence.( sequenceMetaAction method )
     * @param duration amount of time to wait.
     * @return true when the time is passed.
     */
    public static Action delay(double duration)
    {
        return new Action(
                (Sprite target, double dt, double tt) ->
                {
                    return (tt >= duration);
                }
        );
    }

    // -------------------------------------------------------------------------Meta Actions :

    /**
     *  Static method to repeat an action serveral times.
     * @param action Action that want to be repeated.
     * @param totalTimes How many times to repeat.
     * @return an Action that its apply method is overridden (the apply(); will executes what we wrote here ,instead of executing run(); of the Function interface ).
     */
    public static Action repeatMetaAct(Action action, int totalTimes)
    {
        return new Action()
        {
            int finishedTimes = 0;

            @Override
            public boolean apply(Sprite target, double dt)
            {
                //instead of creating Function, we do this
                boolean finished = action.apply(target, dt);
                if (finished)
                {
                    finishedTimes += 1;
                    action.reset();
                }
                return (finishedTimes == totalTimes); // When this be true, the Action above with overridden apply method will be
                                                      // removed from the actionList. so if the total times be reached, action will end.
            }
        };
    }


    /**
     * Like repeatAction but wih no cap limit
     * @param action Action to be repeated.
     * @return an Action object that its apply method is overridden and will not leave the actionList of the sprites.
     */
    public static Action foreverMetaAct(Action action)
    {
        return new Action()
        {
            @Override
            public boolean apply(Sprite target, double dt)
            {
                boolean finished = action.apply(target, dt);

                if (finished)
                    action.reset();

                return false; //no removing then.
            }
        };
    }



    /**
     *
     * @param actions bunch of Actions to be executed in a queue, once one is finished, other one will be deployed.
     *                this actions will be passed to the method in an Array.
     * @return an Action object that its apply method is some how overridden to apply all the actions passed to it in a sequence.
     */
    public static Action sequenceMetaAct(Action... actions)
    {
        return new Action()
        {
            //converting the Array to the ArrayList.
            final ArrayList<Action> actionList =
                    new ArrayList<Action>(Arrays.asList(actions));
            int currentIndex;
            //overriding the apply method to execute actions in the list one by one.
            @Override
            public boolean apply(Sprite target, double dt)
            {
                // selecting one action from what we passed in arguments
                Action currentAction = actionList.get(currentIndex);
                //applying the selected action and waiting for result
                boolean finished = currentAction.apply(target, dt);
                if (finished)
                {
                    currentIndex += 1;
                }
                return ( currentIndex == actionList.size() ); //it will be return true if all the actions are done and
                                                              // and once that happens, this meta Action will be removed
                                                              // of the sprite's actionList.
            }
            /*In case we want to reset a sequence of Actions, we should override the reset(); .
            because normally reset was to reset one action, so if we call reset(); in what sequenceMetaAction()
            returns, it will not work because we have a sequence not just one. so instead of that old version, we use this*/
            @Override
            public void reset()
            {
                for (Action action : actionList)
                    action.reset();

                currentIndex = 0; //resetting the queue
            }
        };
    }


    //--------------------------------------------------------------------------------Screen based Actions :
    public static Action boundToScreen(
            int screenWidth, int screenHeight)
    {
        return new Action(
                (Sprite target, double dt, double tt) ->
                {
                    target.boundToScreen(screenWidth, screenHeight);
                    return false; // this should never end !
                }
        );
    }

    public static Action wrapToScreen(
            int screenWidth, int screenHeight)
    {
        return new Action(
                (Sprite target, double dt, double tt) ->
                {
                    target.wrapToScreen(screenWidth, screenHeight);
                    return false;
                }
        );
    }









}
