package uy.com.agm.gamethree.sprites.powerup.Items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import uy.com.agm.gamethree.screens.PlayScreen;
import uy.com.agm.gamethree.sprites.enemies.Enemy;
import uy.com.agm.gamethree.sprites.player.Hero;

/**
 * Created by AGM on 12/11/2017.
 */

public abstract class Item extends Sprite {
    private static final String TAG = Item.class.getName();

    protected World world;
    protected PlayScreen screen;
    protected Body b2body;

    protected Vector2 velocity;

    protected enum State {
        WAITING, FADING, TAKEN, FINISHED
    }

    protected State currentState;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();

        /* Set this Sprite's position on the lower left vertex of a Rectangle.
        * At this moment we don't have Item.width and Item.height because this is an abstract class.
        * Width and height will be determined in classes that inherit from this one.
        * This point will be used by defineItem() calling getX(), getY() to center its b2body.
        * SetPosition always receives world coordinates.
        */
        setPosition(x, y);
        defineItem();

        // By default this Item doesn't interact in our world
        b2body.setActive(false);
    }

    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }

    public boolean isDestroyed() {
        return currentState == State.TAKEN || currentState == State.FINISHED;
    }

    protected void controlBoundaries() {
        /* When an Item is on camera, it activates (it moves and can collide).
        * You have to be very careful because if the item is destroyed, its b2body does not exist and gives
        * random errors if you try to active it.
        */
        if (!isDestroyed()) {
            float edgeUp = screen.gameCam.position.y + screen.gameViewPort.getWorldHeight() / 2;
            float edgeBottom = screen.gameCam.position.y - screen.gameViewPort.getWorldHeight() / 2;

            if (edgeBottom <= getY() && getY() <= edgeUp) {
                b2body.setActive(true);
            } else {
                b2body.setActive(false);
            }
        }
    }

    protected abstract void defineItem();
    public abstract void update(float dt);
    public abstract void renderDebug(ShapeRenderer shapeRenderer);
    public abstract void use(Hero hero);
}