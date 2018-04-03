package uy.com.agm.gamethree.sprites.tileobjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import uy.com.agm.gamethree.screens.PlayScreen;

/**
 * Created by AGM on 12/4/2017.
 */

public abstract class InteractiveTileObject {
    private static final String TAG = InteractiveTileObject.class.getName();

    // Constants
    private static final float RESTITUTION = 1.0f; // Perfectly elastic collision

    protected PlayScreen screen;
    protected World world;
    protected MapObject object;
    protected TiledMap map;
    protected Body b2body;
    protected Fixture fixture;

    private Rectangle boundsMeters;

    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.object = object;
        this.map = screen.getMap();
        Rectangle bounds = ((RectangleMapObject) object).getRectangle();
        this.boundsMeters = new Rectangle(bounds.getX() / PlayScreen.PPM, bounds.getY() / PlayScreen.PPM, bounds.getWidth() / PlayScreen.PPM, bounds.getHeight() / PlayScreen.PPM);
        defineInteractiveTileObject();
    }

    private void defineInteractiveTileObject() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(boundsMeters.getX() + boundsMeters.getWidth() / 2, boundsMeters.getY() + boundsMeters.getHeight() / 2);

        b2body = world.createBody(bdef);
        shape.setAsBox(boundsMeters.getWidth() / 2, boundsMeters.getHeight() / 2);
        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);
        fixture.setRestitution(RESTITUTION); // Avoid contact after a collision
    }

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit; // Depicts what this fixture is
        fixture.setFilterData(filter);
    }

    public Rectangle getBoundsMeters() {
        return boundsMeters;
    }

    public abstract void onBump();
}
