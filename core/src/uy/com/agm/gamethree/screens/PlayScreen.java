package uy.com.agm.gamethree.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uy.com.agm.gamethree.assets.Assets;
import uy.com.agm.gamethree.game.GameController;
import uy.com.agm.gamethree.game.GameThree;
import uy.com.agm.gamethree.scenes.Hud;
import uy.com.agm.gamethree.sprites.enemies.Enemy;
import uy.com.agm.gamethree.sprites.player.Hero;
import uy.com.agm.gamethree.sprites.powerup.Items.Item;
import uy.com.agm.gamethree.sprites.powerup.boxes.PowerBox;
import uy.com.agm.gamethree.sprites.weapons.Weapon;
import uy.com.agm.gamethree.tools.AudioManager;
import uy.com.agm.gamethree.tools.B2WorldCreator;
import uy.com.agm.gamethree.tools.Constants;
import uy.com.agm.gamethree.tools.WorldContactListener;

/**
 * Created by AGM on 12/2/2017.
 */

public class PlayScreen implements Screen {
    private static final String TAG = PlayScreen.class.getName();

    // Reference to our Game, used to set Screens
    private GameThree game;

    // Basic playscreen variables
    public OrthographicCamera gameCam;
    public Viewport gameViewPort;
    private Hud hud;

    // TiledEditor map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    public B2WorldCreator creator;

    // Main character
    public Hero player;

    public PlayScreen(GameThree game) {
        this.game = game;

        // Create a cam used to move up through our world
        gameCam = new OrthographicCamera();

        // Create a FitViewport to maintain virtual aspect ratio despite screen size
        gameViewPort = new FitViewport(Constants.V_WIDTH / Constants.PPM, Constants.V_HEIGHT / Constants.PPM, gameCam);

        // Create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        // Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("levelOne/levelOne.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Constants.PPM);

        // Initially set our gamcam to be centered correctly at the start (bottom) of the map
        gameCam.position.set(gameViewPort.getWorldWidth() / 2, gameViewPort.getWorldHeight() / 2, 0);

        // Create our Box2D world, setting no gravity in x and no gravity in y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);

        // Allows for debug lines of our box2d world.
        if (Constants.DEBUG_BOUNDARIES) {
            b2dr = new Box2DDebugRenderer();
        }

        creator = new B2WorldCreator(this);

        // Create the hero in our game world
        player = new Hero(this, gameCam.position.x, gameCam.position.y / 2);

        world.setContactListener(new WorldContactListener());

        // Load preferences for audio settings and start playing music
        // GamePreferences.instance.load();
        AudioManager.instance.play(Assets.instance.music.songLevelOne);

        // User input handler
        Gdx.input.setInputProcessor(getInputProcessor(new GameController(player)));
    }

    private InputProcessor getInputProcessor(GameController gc) {
        /* GameController is an InputAdapter because it extends that class and
         * It's also a GestureListener because it implements that interface.
         * In GameController then I can recognize gestures (like fling) and I can
         * recognize events such as touchUp that doesn't exist within the interface
         * GestureListener but exists within an InputAdapter.
         * As the InputAdapter methods are too many, I decided to extend that
         * class (to implement within GameController only the method that I'm interested in) and
         * implemented the GestureListener interface because, after all, there are only few extra methods that I must declare.
         * To work with both InputProcessors at the same time, I must use a InputMultiplexer.
         * The fling and touchUp events, for example, always run at the same time.
         * First I registered GestureDetector so that fling is executed before touchUp and as they are related,
         * when I return true in the fling event the touchUp is canceled. If I return false both are executed.
         * */

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(gc));
        multiplexer.addProcessor(gc);
        return multiplexer;
    }

    @Override
    public void show() {
    }

    // Key control
    public void handleInput(float dt) {
        // We use GameController instead of input.isKeyPressed.
    }

    public void update(float dt) {
        // Handle user input first
        handleInput(dt);

        // Handle creation of game actors first
        creator.handleCreatingGameThreeActors();

        // Step in the physics simulation
        world.step(Constants.WORLD_TIME_STEP, Constants.WORLD_VELOCITY_ITERATIONS, Constants.WORLD_POSITION_ITERATIONS);

        // Hero
        player.update(dt);

        // Enemies
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
        }

        // PowerBoxes
        for (PowerBox powerBox : creator.getPowerBoxes()) {
            powerBox.update(dt);
        }

        // Items
        for (Item item : creator.getItems()) {
            item.update(dt);
        }

        // Weapons
        for (Weapon weapon : creator.getWeapons()) {
            weapon.update(dt);
        }

        // Head-up display
        hud.update(dt);

        // If Hero is dead, we freeze the camera
        if(player.currentHeroState != Hero.HeroState.DEAD) {
            // GameCam must be moved from 4 to 76
            if (gameCam.position.y < (Constants.V_HEIGHT * Constants.WORLD_SCREENS / Constants.PPM) - gameViewPort.getWorldHeight() / 2) {
                // Gamecam is moving up
                gameCam.position.y += Constants.GAMECAM_VELOCITY * dt;
            }
        }

        // Update our gamecam with correct coordinates after changes
        gameCam.update();

        // Tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        // Separate our update logic from render
        update(delta);

        // Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render our game map
        renderer.render();

        // Renderer our Box2DDebugLines
        if (Constants.DEBUG_BOUNDARIES) {
            b2dr.render(world, gameCam.combined);
        }

        // Set our batch to now draw what the gameCam camera sees.
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // Hero
        player.draw(game.batch);

        // Enemies
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }

        // PowerBoxes
        for (PowerBox powerBox : creator.getPowerBoxes()) {
            powerBox.draw(game.batch);
        }

        // Items
        for (Item item : creator.getItems()) {
            item.draw(game.batch);
        }

        // Weapons
        for (Weapon weapon : creator.getWeapons()) {
            weapon.draw(game.batch);
        }

        game.batch.end();

        // Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // Debug
        if (Constants.DEBUG_BOUNDARIES) {
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            // Set our batch to now draw what the gameCam camera sees.
            shapeRenderer.setProjectionMatrix(gameCam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);

            // Hero
            player.renderDebug(shapeRenderer);

            // Enemies
            for (Enemy enemy : creator.getEnemies()) {
                enemy.renderDebug(shapeRenderer);
            }

            // Power boxes
            for (PowerBox powerBox : creator.getPowerBoxes()) {
                powerBox.renderDebug(shapeRenderer);
            }

            // Items
            for (Item item : creator.getItems()) {
                item.renderDebug(shapeRenderer);
            }

            // Weapons
            for (Weapon weapon : creator.getWeapons()) {
                weapon.renderDebug(shapeRenderer);
            }
            shapeRenderer.end();
        }

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver() {
        boolean gameOver = false;
        if (player.currentHeroState == Hero.HeroState.DEAD && player.getStateTimer() > 3.0f) {
            gameOver = true;
        }
        return gameOver;
    }

    @Override
    public void resize(int width, int height) {
        // Updated our game viewport
        gameViewPort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Hud getHud() {
        return hud;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // Dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
