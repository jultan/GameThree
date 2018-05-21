package uy.com.agm.gamethree.screens.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;

import uy.com.agm.gamethree.assets.Assets;
import uy.com.agm.gamethree.assets.scene2d.AssetStageFailed;
import uy.com.agm.gamethree.assets.scene2d.AssetVictory;
import uy.com.agm.gamethree.game.GameSettings;
import uy.com.agm.gamethree.screens.AbstractScreen;
import uy.com.agm.gamethree.screens.PlayScreen;
import uy.com.agm.gamethree.tools.AudioManager;
import uy.com.agm.gamethree.tools.DynamicHelpDef;
import uy.com.agm.gamethree.tools.LevelFactory;
import uy.com.agm.gamethree.widget.AnimatedActor;

/**
 * Created by AGM on 1/18/2018.
 *
 */

public class InfoScreen extends AbstractScreen {
    private static final String TAG = InfoScreen.class.getName();

    /*
     * Displays a message, image or animation in the center of the screen in a mutually exclusive manner.
     * Display types:
     *  - Normal: Always visible until hideInfo() is called
     *  - Temporary: Just for a few seconds
     *  - Modal: Force user interaction
     * A message can be normal or temporary.
     * An image can be normal, temporary or modal.
     * An animation can be normal, temporary or modal.
     */

    // Constants
    private static final float BUTTONS_PAD = 20.0f;
    private static final float BUTTON_WIDTH = 100.0f;
    private static final float RED_FLASH_TIME = 0.1f;

    private PlayScreen screen;
    private int level;

    // Track help screens depending on the object's class name
    private ObjectMap<String, DynamicHelpDef> dynamicHelp;

    private I18NBundle i18NGameThreeBundle;
    private Label.LabelStyle labelStyleBig;
    private Label.LabelStyle labelStyleSmall;

    // Animations
    private Animation emptySkullsAnimation;
    private Animation victoryAnimation;

    private Table centerTable;
    private Label messageLabel;
    private AnimatedActor animatedActor;
    private Image image;
    private float overlayTime;
    private float overlaySeconds;
    private boolean overlayTemporaryAnimation;
    private boolean overlayTemporaryImage;
    private boolean overlayTemporaryMessage;

    private Table buttonsTable;
    private Label gotItLabel;

    private Stack stack;
    private Cell stackCell;

    private TextureRegion redFlash;

    public InfoScreen(PlayScreen screen, Integer level) {
        super();

        // Define tracking variables
        this.screen = screen;
        this.level = level;
        overlayTime = 0;
        overlaySeconds = 0;
        overlayTemporaryAnimation = false;
        overlayTemporaryImage = false;
        overlayTemporaryMessage = false;

        // I18n
        i18NGameThreeBundle = Assets.getInstance().getI18NGameThree().getI18NGameThreeBundle();

        // Track help screens
        dynamicHelp = LevelFactory.getDynamicHelp(level);

        // Personal fonts
        labelStyleBig = new Label.LabelStyle();
        labelStyleBig.font = Assets.getInstance().getFonts().getDefaultBig();

        labelStyleSmall = new Label.LabelStyle();
        labelStyleSmall.font = Assets.getInstance().getFonts().getDefaultSmall();

        // Animations
        emptySkullsAnimation = Assets.getInstance().getScene2d().getStageFailed().getStageFailedAnimation();
        victoryAnimation = Assets.getInstance().getScene2d().getVictory().getVictoryAnimation();

        // Red flash Texture
        Pixmap pixmap = new Pixmap(V_WIDTH, V_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        redFlash = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
    }

    private void defineCenterTable() {
        // Define a new table used to display a message
        centerTable = new Table();

        // Debug lines
        centerTable.setDebug(PlayScreen.DEBUG_MODE);

        // Center-Align table
        centerTable.center();

        // Make the table fill the entire stage
        centerTable.setFillParent(true);

        // Define a label based on labelStyle
        messageLabel = new Label("MESSAGE", labelStyleBig);
        messageLabel.setAlignment(Align.center);

        // Define animatedActor and image
        animatedActor = new AnimatedActor();
        animatedActor.setAlign(Align.center);
        image = new Image();
        image.setAlign(Align.center);

        // Add values
        stack = new Stack();
        stack.add(animatedActor);
        stack.add(image);
        stack.add(messageLabel);
        stackCell = centerTable.add(stack);

        // Add our table to the stage
        addActor(centerTable);

        // Initially hidden
        messageLabel.setVisible(false);
        animatedActor.setVisible(false);
        image.setVisible(false);
        centerTable.setVisible(false);
    }

    private void defineButtonsTable() {
        // Define a new table used to display pause, resume and quit buttons
        buttonsTable = new Table();

        // Debug lines
        buttonsTable.setDebug(PlayScreen.DEBUG_MODE);

        // Bottom-Align table
        buttonsTable.bottom().padLeft(BUTTONS_PAD).padRight(BUTTONS_PAD);

        // Make the container fill the entire stage
        buttonsTable.setFillParent(true);

        // Define labels based on labelStyle
        gotItLabel = new Label(i18NGameThreeBundle.format("infoScreen.gotIt"), labelStyleSmall);

        // Add values
        buttonsTable.add(gotItLabel).width(BUTTON_WIDTH).right().expandX();

        gotItLabel.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        // Audio FX
                        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getClick());
                        gotIt();
                        return true;
                    }
                });

        // Add the table to the stage
        addActor(buttonsTable);

        // Initially hidden
        gotItLabel.setVisible(false);
    }

    private void gotIt() {
        gotItLabel.setVisible(false);
        hideInfo();
        screen.getDimScreen().showButtons();
        screen.setPlayScreenStateRunning();
    }

    public void showInitialHelp() {
        if (level == 1) {
            if (GameSettings.getInstance().isManualShooting()) {
                showImage(Assets.getInstance().getScene2d().getHelpInitialManual(), DynamicHelpDef.DEFAULT_HELP_SECONDS);
            } else {
                showImage(Assets.getInstance().getScene2d().getHelpInitialAutomatic(), DynamicHelpDef.DEFAULT_HELP_SECONDS);
            }
        }
    }

    @Override
    public void buildStage() {
        defineCenterTable();
        defineButtonsTable();
    }

    // ----------- Message functions

    public void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        animatedActor.setVisible(false);
        image.setVisible(false);
        centerTable.setVisible(true);
    }

    public void showMessage(String message, float seconds) {
        overlayTime = 0;
        overlaySeconds = seconds;
        overlayTemporaryMessage = true;
        overlayTemporaryImage = false;
        overlayTemporaryAnimation = false;
        showMessage(message);
    }

    public boolean isMessageVisible() {
        return messageLabel.isVisible();
    }

    // ----------- Image functions

    public void  showImage(TextureRegion textureRegion) {
        showImage(textureRegion, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public void showImage(TextureRegion textureRegion, float width, float height) {
        stackCell.size(width, height);
        centerTable.pack();
        image.setDrawable(new TextureRegionDrawable(textureRegion));
        image.setScaling(Scaling.fit);

        image.setVisible(true);
        messageLabel.setVisible(false);
        animatedActor.setVisible(false);
        centerTable.setVisible(true);
    }

    public void showImage(TextureRegion textureRegion, float seconds) {
        showImage(textureRegion, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), seconds);
    }

    public void showImage(TextureRegion textureRegion, float width, float height, float seconds) {
        overlayTime = 0;
        overlaySeconds = seconds;
        overlayTemporaryImage = true;
        overlayTemporaryAnimation = false;
        overlayTemporaryMessage = false;
        showImage(textureRegion, width, height);
    }

    public void showModalImage(TextureRegion textureRegion) {
        showModalImage(textureRegion, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public void showModalImage(TextureRegion textureRegion, float width, float height) {
        gotItLabel.setVisible(true);
        showImage(textureRegion, width, height);
        screen.getDimScreen().hideButtons();
        screen.setPlayScreenStatePaused(false);
    }

    public boolean isImageVisible() {
        return image.isVisible();
    }

    // ----------- Animation functions

    public void showAnimation(Animation animation) {
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(0, false);
        showAnimation(animation, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public void showAnimation(Animation animation, float width, float height) {
        stackCell.size(width, height);
        centerTable.pack();
        animatedActor.setAnimation(animation);

        animatedActor.setVisible(true);
        image.setVisible(false);
        messageLabel.setVisible(false);
        centerTable.setVisible(true);
    }

    public void showAnimation(Animation animation, float seconds) {
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(0, false);
        showAnimation(animation, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), seconds);
    }

    public void showAnimation(Animation animation,  float width, float height, float seconds) {
        overlayTime = 0;
        overlaySeconds = seconds;
        overlayTemporaryAnimation = true;
        overlayTemporaryImage = false;
        overlayTemporaryMessage = false;
        showAnimation(animation, width, height);
    }

    public void showModalAnimation(Animation animation) {
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(0, false);
        showModalAnimation(animation, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    public void showModalAnimation(Animation animation, float width, float height) {
        gotItLabel.setVisible(true);
        showAnimation(animation, width, height);
        screen.getDimScreen().hideButtons();
        screen.setPlayScreenStatePaused(false);
    }

    public boolean isAnimationVisible() {
        return animatedActor.isVisible();
    }

    // ----------- Hide functions

    public void hideInfo() {
        messageLabel.setVisible(false);
        image.setVisible(false);
        animatedActor.setVisible(false);
        centerTable.setVisible(false);
    }

    public void hideModalInfo() {
        gotItLabel.setVisible(false);
        hideInfo();
        screen.getDimScreen().showButtons();
        screen.setPlayScreenStateRunning();
    }

    // ----------- Specialized functions

    public boolean isModalVisible() {
        return gotItLabel.isVisible();
    }

    public void showHurryUpMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.hurryUp"));
    }

    public void showTimeIsUpMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.timeIsUp"));
    }

    public void showEmptySkullsAnimation() {
        showAnimation(emptySkullsAnimation, AssetStageFailed.WIDTH_PIXELS, AssetStageFailed.HEIGHT_PIXELS);
    }

    public void showVictoryAnimation() {
        showAnimation(victoryAnimation, AssetVictory.WIDTH_PIXELS, AssetVictory.HEIGHT_PIXELS);
    }

    public void showFightMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.fight"));
    }

    public void showRedFlash() {
        showImage(redFlash, RED_FLASH_TIME);
    }

    public boolean isRedFlashVisible() {
        return isImageVisible() && ((TextureRegionDrawable) image.getDrawable()).getRegion() == redFlash;
    }

    // Show help screens depending on the object's class name
    public void showDynamicHelp(String className, TextureRegion helpImage) {
        if (dynamicHelp.containsKey(className)){
            DynamicHelpDef dynamicHelpDef = dynamicHelp.get(className);
            if (dynamicHelpDef.isModal()) {
                showModalImage(helpImage);
            } else {
                showImage(helpImage, dynamicHelpDef.getSeconds());
            }
            dynamicHelp.remove(className);
        }
    }

    private String getMessage() {
        String message = "";
        if (messageLabel.isVisible()) {
            message = messageLabel.getText().toString();
        }
        return message;
    }

    // ----------- InfoScreen logic functions

    public void update(float dt) {
        overlayTemporaryAnimation(dt);
        overlayTemporaryImage(dt);
        overlayTemporaryMessage(dt);
    }

    private void overlayTemporaryAnimation(float dt) {
        if (overlayTemporaryAnimation) {
            overlayTime += dt;
            if (overlayTime >= overlaySeconds) {
                overlayTemporaryAnimation = false;
                overlayTime = 0;
                hideInfo();
            }
        }
    }

    private void overlayTemporaryImage(float dt) {
        if (overlayTemporaryImage) {
            overlayTime += dt;
            if (overlayTime >= overlaySeconds) {
                overlayTemporaryImage = false;
                overlayTime = 0;
                hideInfo();
            }
        }
    }

    private void overlayTemporaryMessage(float dt) {
        if (overlayTemporaryMessage) {
            overlayTime += dt;
            if (overlayTime >= overlaySeconds) {
                overlayTemporaryMessage = false;
                overlayTime = 0;
                hideInfo();
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render(float delta) {
        // Calling to Stage methods
        super.act(delta);
        super.draw();
    }
}
