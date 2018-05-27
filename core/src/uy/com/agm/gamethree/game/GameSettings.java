package uy.com.agm.gamethree.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

import uy.com.agm.gamethree.actors.player.Hero;
import uy.com.agm.gamethree.screens.PlayScreen;
import uy.com.agm.gamethree.tools.LevelFactory;

/**
 * Created by AGM on 1/18/2018.
 */

public class GameSettings {
    private static final String TAG = GameSettings.class.getName();

    // Constants
    private static final String SOUND = "sound";
    private static final String MUSIC = "music";
    private static final String VOLUME_SOUND = "volSound";
    private static final String VOLUME_MUSIC = "volMusic";
    private static final String MANUAL_SHOOTING = "manualShooting";
    private static final String LEVEL_STATE = "levelState_";
    private static final String SETTINGS = "uy.com.agm.gameThree.settings";
    private static final float DEFAULT_VOLUME = 0.5f;
    private static final float MIN_VOLUME = 0.0f;
    private static final float MAX_VOLUME = 1.0f;
    public static final int MAX_AVAILABLE_LEVEL = 4;

    // Singleton: unique instance
    private static GameSettings instance;

    private boolean sound;
    private boolean music;
    private float volSound;
    private float volMusic;
    private boolean manualShooting;
    private ArrayMap<Integer, LevelState> availableLevels;
    private Preferences prefs;
    private Json json;

    // Singleton: prevent instantiation from other classes
    private GameSettings() {
        availableLevels = new ArrayMap<Integer, LevelState>();
        prefs = Gdx.app.getPreferences(SETTINGS);
        json = new Json();
    }

    // Singleton: retrieve instance
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    private String getData(LevelState levelState) {
        return json.toJson(levelState);
    }

    private LevelState getGameState(String data) {
        return json.fromJson(LevelState.class, data);
    }

    public void load () {
        sound = prefs.getBoolean(SOUND, true);
        music = prefs.getBoolean(MUSIC, true);
        volSound = MathUtils.clamp(prefs.getFloat(VOLUME_SOUND, DEFAULT_VOLUME), MIN_VOLUME, MAX_VOLUME);
        volMusic = MathUtils.clamp(prefs.getFloat(VOLUME_MUSIC, DEFAULT_VOLUME), MIN_VOLUME, MAX_VOLUME);
        manualShooting = prefs.getBoolean(MANUAL_SHOOTING, true);

        int level = 1; // Default level
        LevelState levelState = new LevelState(level, Hero.LIVES_START, 0, LevelFactory.getLevelSkulls(level), true);
        availableLevels.put(level, levelState);

        // Other levels
        level++;
        String data;
        boolean availableLevel;
        do {
            data = prefs.getString(LEVEL_STATE + level, "");
            if (data.isEmpty()) {
                if (PlayScreen.DEBUG_LEVELS) {
                    if (level <= MAX_AVAILABLE_LEVEL) {
                        levelState = new LevelState(level, Hero.LIVES_START, 0, LevelFactory.getLevelSkulls(level), false);
                        availableLevel = true;
                    } else {
                        availableLevel = false;
                    }
                } else {
                    availableLevel = false;
                }
            } else {
                levelState = getGameState(data);
                if (levelState.isActive()) {
                    availableLevel = true;
                } else {
                    if (PlayScreen.DEBUG_LEVELS) {
                        availableLevel = true;
                    } else {
                        availableLevel = false;
                    }
                }
            }
            if (availableLevel) {
                availableLevels.put(level, levelState);
                level++;
            }
        } while (availableLevel);
   }

    public void save () {
        prefs.putBoolean(SOUND, sound);
        prefs.putBoolean(MUSIC, music);
        prefs.putFloat(VOLUME_SOUND, volSound);
        prefs.putFloat(VOLUME_MUSIC, volMusic);
        prefs.putBoolean(MANUAL_SHOOTING, manualShooting);
        for(LevelState levelState : availableLevels.values()) {
            prefs.putString(LEVEL_STATE + levelState.getLevel(), getData(levelState));
        }
        prefs.flush();
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public float getVolSound() {
        return volSound;
    }

    public void setVolSound(float volSound) {
        this.volSound = volSound;
    }

    public float getVolMusic() {
        return volMusic;
    }

    public void setVolMusic(float volMusic) {
        this.volMusic = volMusic;
    }

    public boolean isManualShooting() {
        return manualShooting;
    }

    public void setManualShooting(boolean manualShooting) {
        this.manualShooting = manualShooting;
    }

    public ArrayMap<Integer, LevelState> getAvailableLevels() {
        return availableLevels;
    }

    public void addActiveLevel(int level, int lives, int score, int skulls) {
        LevelState levelState = new LevelState(level, lives, score, skulls, true);
        availableLevels.put(level, levelState);
    }
}
