package uy.com.agm.gamethree.widget;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

/**
 * Created by AGM on 5/13/2018.
 */

public class AnimatedActor extends Image {
    private static final String TAG = AnimatedActor.class.getName();

    private TextureRegionDrawable textureRegionDrawable;
    private float stateTime;
    Animation animation;

    public AnimatedActor() {
        textureRegionDrawable = new TextureRegionDrawable();
    }

    public void setAnimation(Animation animation, int width, int height) {
        TextureRegion region = (TextureRegion) animation.getKeyFrame(0);
        region.setRegionWidth(width);
        region.setRegionHeight(height);
        textureRegionDrawable.setRegion(region);
        setDrawable(textureRegionDrawable);
        setScaling(Scaling.none);
        stateTime = 0;
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        if (animation != null) {
            stateTime += delta;
            ((TextureRegionDrawable) getDrawable()).setRegion((TextureRegion) animation.getKeyFrame(stateTime, false));
            super.act(delta);
        }
    }
}