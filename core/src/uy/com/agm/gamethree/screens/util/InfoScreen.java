package uy.com.agm.gamethree.screens.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
import uy.com.agm.gamethree.game.GameSettings;
import uy.com.agm.gamethree.screens.AbstractScreen;
import uy.com.agm.gamethree.screens.PlayScreen;
import uy.com.agm.gamethree.tools.AudioManager;
import uy.com.agm.gamethree.tools.DynamicHelpDef;
import uy.com.agm.gamethree.tools.LevelFactory;
import uy.com.agm.gamethree.widget.AnimatedActor;

/**
 * Created by AGM on 1/18/2018.
 */

public class InfoScreen extends AbstractScreen {
    private static final String TAG = InfoScreen.class.getName();

    // Constants
    private static final float BUTTONS_PAD = 20.0f;
    private static final float BUTTON_WIDTH = 100.0f;

    private PlayScreen screen;
    private int level;

    // Track help screens depending on the object's class name
    private ObjectMap<String, DynamicHelpDef> dynamicHelp;

    private I18NBundle i18NGameThreeBundle;
    private Label.LabelStyle labelStyleBig;
    private Label.LabelStyle labelStyleSmall;

    private Table centerTable;
    private Label messageLabel;
    private TextureRegionDrawable textureRegionDrawable;
    private AnimatedActor animatedActor;
    private Image image;
    private float overlayTime;
    private float overlaySeconds;
    private boolean overlayTemporaryScreen;
    private boolean overlayTemporaryMessage;

    private Table buttonsTable;
    private Label pauseLabel;
    private Label resumeLabel;
    private Label gotItLabel;
    private Label quitLabel;

    private Stack stack;

    public InfoScreen(PlayScreen screen, Integer level) {
        super();

        // Define tracking variables
        this.screen = screen;
        this.level = level;
        textureRegionDrawable = new TextureRegionDrawable();
        overlayTime = 0;
        overlaySeconds = 0;
        overlayTemporaryScreen = false;
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
        animatedActor = new AnimatedActor();
        animatedActor.setAlign(Align.center);
        image = new Image();
        image.setAlign(Align.center);

        // Add values
        stack = new Stack();
        stack.add(messageLabel);
        stack.add(animatedActor);
        stack.add(image);
        centerTable.add(stack);

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
        pauseLabel = new Label(i18NGameThreeBundle.format("infoScreen.pause"), labelStyleSmall);
        quitLabel = new Label(i18NGameThreeBundle.format("infoScreen.quit"), labelStyleSmall);
        quitLabel.setAlignment(Align.right);
        resumeLabel = new Label(i18NGameThreeBundle.format("infoScreen.resume"), labelStyleSmall);
        gotItLabel = new Label(i18NGameThreeBundle.format("infoScreen.gotIt"), labelStyleSmall);

        // Add values
        stack = new Stack();
        stack.add(pauseLabel);
        stack.add(resumeLabel);
        stack.add(gotItLabel);
        buttonsTable.add(stack).width(BUTTON_WIDTH).left().expandX(); // Pause, Resume and Got it texts overlapped
        buttonsTable.add(quitLabel).width(BUTTON_WIDTH).right().expandX();

        // Events
        pauseLabel.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        // Audio FX
                        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getClick());
                        setGameStatePaused();
                        return true;
                    }
                });

        resumeLabel.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        // Audio FX
                        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getClick());
                        setGameStateRunning();
                        return true;
                    }
                });

        gotItLabel.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        // Audio FX
                        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getClick());
                        setGameStateRunning();
                        return true;
                    }
                });

        quitLabel.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        // Audio FX
                        AudioManager.getInstance().playSound(Assets.getInstance().getSounds().getClick());
                        quit();
                        return true;
                    }
                });

        // Add the table to the stage
        addActor(buttonsTable);

        // Initially hidden
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(false);
        quitLabel.setVisible(false);
    }

    public void setGameStatePaused() {
        pauseLabel.setVisible(false);
        resumeLabel.setVisible(true);
        gotItLabel.setVisible(false);
        quitLabel.setVisible(true);
        showMessage(i18NGameThreeBundle.format("infoScreen.pauseMessage"));
        screen.setPlayScreenStatePaused(true);
    }

    public void setGameStateRunning() {
        pauseLabel.setVisible(true);
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(false);
        quitLabel.setVisible(false);
        hideMessage();
        screen.setPlayScreenStateRunning();
    }

    private void quit() {
        if (getMessage().equals(i18NGameThreeBundle.format("infoScreen.confirm"))) {
            ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
        } else {
            showMessage(i18NGameThreeBundle.format("infoScreen.confirm"));
        }
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
        showMessage(message);
    }

    public String getMessage() {
        String message = "";
        if (messageLabel.isVisible()) {
            message = messageLabel.getText().toString();
        }
        return message;
    }

    public void showHurryUpMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.hurryUp"));
    }

    public void showTimeIsUpMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.timeIsUp"));
    }

    public void showFightMessage() {
        showMessage(i18NGameThreeBundle.format("infoScreen.fight"));
    }

    public void hideMessage() {
        messageLabel.setVisible(false);
        centerTable.setVisible(false);
    }

    public boolean isMessageVisible() {
        return messageLabel.isVisible();
    }

    public void showImage(TextureRegion textureRegion) {
        textureRegionDrawable.setRegion(textureRegion);
        image.setDrawable(textureRegionDrawable);
        image.setScaling(Scaling.fit); // Default is Scaling.stretch.
        image.setVisible(true);
        messageLabel.setVisible(false);
        animatedActor.setVisible(false);
        centerTable.setVisible(true);
    }

    public void showImage(TextureRegion textureRegion, float seconds) {
        overlayTime = 0;
        overlaySeconds = seconds;
        overlayTemporaryScreen = true;
        showImage(textureRegion);
    }

    public void showModalImage(TextureRegion textureRegion) {
        pauseLabel.setVisible(false);
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(true);
        quitLabel.setVisible(false);
        showImage(textureRegion);
        screen.setPlayScreenStatePaused(false);
    }

    public void hideModalImage() {
        pauseLabel.setVisible(true);
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(false);
        quitLabel.setVisible(false);
        hideImage();
        screen.setPlayScreenStateRunning();
    }

    public void hideImage() {
        image.setVisible(false);
        centerTable.setVisible(false);
    }

    public boolean isImageVisible() {
        return image.isVisible();
    }

    public void showAnimation(Animation animation) {
        animatedActor.setAnimation(animation);
        animatedActor.setVisible(true);
        image.setVisible(false);
        messageLabel.setVisible(false);
        centerTable.setVisible(true);
    }

    public void showAnimation(Animation animation, float seconds) {
        overlayTime = 0;
        overlaySeconds = seconds;
        overlayTemporaryScreen = true;
        showAnimation(animation);
    }

    public void showModalAnimation(Animation animation) {
        pauseLabel.setVisible(false);
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(true);
        quitLabel.setVisible(false);
        showAnimation(animation);
        screen.setPlayScreenStatePaused(false);
    }

    public void hideModalAnimation() {
        pauseLabel.setVisible(true);
        resumeLabel.setVisible(false);
        gotItLabel.setVisible(false);
        quitLabel.setVisible(false);
        hideAnimation();
        screen.setPlayScreenStateRunning();
    }

    public void hideAnimation() {
        animatedActor.setVisible(false);
        centerTable.setVisible(false);
    }

    public boolean isAnimationVisible() {
        return animatedActor.isVisible();
    }

    public void update(float dt) {
        overlayTemporaryScreen(dt);
        overlayTemporaryMessage(dt);
    }

    private void overlayTemporaryScreen(float dt) {
        if (overlayTemporaryScreen) {
            overlayTime += dt;
            if (overlayTime >= overlaySeconds) {
                overlayTemporaryScreen = false;
                overlayTime = 0;
                hideImage();
            }
        }
    }

    private void overlayTemporaryMessage(float dt) {
        if (overlayTemporaryMessage) {
            overlayTime += dt;
            if (overlayTime >= overlaySeconds) {
                overlayTemporaryMessage = false;
                overlayTime = 0;
                hideMessage();
            }
        }
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
