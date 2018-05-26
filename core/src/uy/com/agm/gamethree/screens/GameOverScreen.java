package uy.com.agm.gamethree.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import uy.com.agm.gamethree.assets.Assets;
import uy.com.agm.gamethree.assets.scene2d.AssetGameOver;
import uy.com.agm.gamethree.screens.util.ScreenEnum;
import uy.com.agm.gamethree.screens.util.UIFactory;
import uy.com.agm.gamethree.tools.AudioManager;
import uy.com.agm.gamethree.widget.AnimatedActor;

/**
 * Created by AGM on 12/23/2017.
 */

public class GameOverScreen extends AbstractScreen {
    private static final String TAG = GameOverScreen.class.getName();

    // Constants
    public static final float GAME_OVER_WIDTH = 420.0f;

    public GameOverScreen() {
        super();

        // Audio FX
        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getGameOver());
    }

    @Override
    public void buildStage() {
        // I18n
        I18NBundle i18NGameThreeBundle = Assets.getInstance().getI18NGameThree().getI18NGameThreeBundle();

        // Set table structure
        Table table = new Table();

        // Design
        table.setBackground(new TextureRegionDrawable(Assets.getInstance().getScene2d().getTable()));

        // Debug lines
        table.setDebug(PlayScreen.DEBUG_MODE);

        // Center-Align table
        table.center();

        // Make the table fill the entire stage
        table.setFillParent(true);

        // Personal fonts
        Label.LabelStyle labelStyleBig = new Label.LabelStyle();
        labelStyleBig.font = Assets.getInstance().getFonts().getDefaultBig();

        Label.LabelStyle labelStyleNormal = new Label.LabelStyle();
        labelStyleNormal.font = Assets.getInstance().getFonts().getDefaultNormal();

        // Animation
        AnimatedActor animatedActor = new AnimatedActor();
        animatedActor.setAnimation(Assets.getInstance().getScene2d().getGameOver().getGameOverAnimation());
        animatedActor.setAlign(Align.center);

        // Add values
        table.add(animatedActor).size(AssetGameOver.WIDTH_PIXELS, AssetGameOver.HEIGHT_PIXELS);

        // Set table structure
        Table navigation = new Table();

        // Debug lines
        navigation.setDebug(PlayScreen.DEBUG_MODE);

        // Bottom-Align table
        navigation.bottom();

        // Make the table fill the entire stage
        navigation.setFillParent(true);

        // Define images
        Image back = new Image(Assets.getInstance().getScene2d().getBack());

        // Add values
        navigation.add(back).padBottom(AbstractScreen.PAD * 2);

        // Events
        back.addListener(UIFactory.createListener(ScreenEnum.MAIN_MENU));

        // Adds created tables to stage
        addActor(table);
        addActor(navigation);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
