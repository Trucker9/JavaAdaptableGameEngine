package Engine;

import Engine.Entites.Entity;
import Engine.Entites.EntityGroup;

import Engine.Tools.Input;
import javafx.animation.AnimationTimer;
import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 *  Main class to be extended for game projects.
 *  Creates the window and {@link EntityGroup} objects,
 *    and manages the life cycle of the game (initialization and game loop).
 */
public abstract class Game
        extends Application
        implements Screen
{
    /**
     * area where game graphics are displayed
     */
    public Canvas canvas;

    /**
     * object with methods to draw game entities on canvas
     */
    public GraphicsContext context;

    /**
     * The root collection for all {@link Entity} objects in this game.
     */
    public EntityGroup toRenderGroup;

    public EntityGroup solidSpritesToRenderGroup;

    /**
     * The window containing the game.
     */
    public Stage stage;

    /**
     * Input class to interact with user
     */
    public Input input;
    /**
     *  Initializes the window and game objects,
     *  and manages the life cycle of the game (initialization and game loop).
     */
    public  void start(Stage mainStage)
    {
       // login(mainStage);



        mainStage.setTitle("Clash Royal");
        mainStage.setResizable(false);

        Pane root = new Pane();
        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
        mainStage.sizeToScene();

        canvas = new Canvas(438, 750);
        context = canvas.getGraphicsContext2D();
        root.getChildren().add( canvas );

        toRenderGroup = new EntityGroup();
        solidSpritesToRenderGroup = new EntityGroup();
        //scene is gonna listen for key events
        input = new Input(mainScene);

        // to clarify class containing update method
        Game self = this;

        AnimationTimer gameLoop = new AnimationTimer()
        {
            public void handle(long nanoTime)
            {

                // Update game state (interaction between game objects)
                self.update();

                // Update each object internal data
                toRenderGroup.update(1 / 60.0);
                solidSpritesToRenderGroup.update(1 / 60.0);


                //update user input
                input.update();

                // clear the canvas
                self.context.setFill(Color.BROWN);
                self.context.fillRect( 0,0,
                        self.canvas.getWidth(),
                        self.canvas.getHeight() );

                // render game objects
                self.solidSpritesToRenderGroup.draw(self.context);
                self.toRenderGroup.draw( self.context );

            }
        };

        mainStage.show();

        // reference required for set methods
        stage = mainStage;

        initialize();

        gameLoop.start();
    }

    /**
     * Set the text that appears in the window title bar
     * @param title window title
     */
    public void setTitle(String title)
    {
        stage.setTitle(title);
    }

    /**
     * set the size of the window/canvas that displays game graphics
     * @param width window/canvas width
     * @param height window/canvas height
     */
    public void setWindowSize(int width, int height)
    {
        canvas.setWidth(width);
        canvas.setHeight(height);
        stage.sizeToScene();
    }

//    private void login(Stage primaryStage)   {
//        Parent root = null;
//        try {
//            root = FXMLLoader.load(getClass().getResource("/Game.Menu.LoginScreen/LoginScene.fxml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        primaryStage.setTitle("Clash royal");
//        primaryStage.setScene(new Scene(root, 335, 600));
//        primaryStage.show();
//    }
//    public static void main(String[] args) {
//        launch(args);
//    }
}
