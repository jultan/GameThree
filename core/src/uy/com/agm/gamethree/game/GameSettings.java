package uy.com.agm.gamethree.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

import uy.com.agm.gamethree.actors.player.Hero;

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
    private static final String GOLD_HIGH_SCORE = "goldHighScore";
    private static final String GOLD_HIGH_SCORE_MILLIS = "goldHighScoreMillis";
    private static final int DEFAULT_GOLD_HIGH_SCORE = 21365;
    private static final long DEFAULT_GOLD_HIGH_SCORE_MILLIS = 257565600000L; // 15/01/1977
    private static final String SILVER_HIGH_SCORE = "silverHighScore";
    private static final String SILVER_HIGH_SCORE_MILLIS = "silverHighScoreMillis";
    private static final int DEFAULT_SILVER_HIGH_SCORE = 18491;
    private static final long DEFAULT_SILVER_HIGH_SCORE_MILLIS = 1547262000000L; // 25/12/2017
    private static final String BRONZE_HIGH_SCORE = "bronzeHighScore";
    private static final String BRONZE_HIGH_SCORE_MILLIS = "bronzeHighScoreMillis";
    private static final int DEFAULT_BRONZE_HIGH_SCORE = 75273;
    private static final long DEFAULT_BRONZE_HIGH_SCORE_MILLIS = 1531794009758L; // 16/07/2018
    private static final String LEVEL_STATE = "levelState_";
    private static final String SETTINGS = "uy.com.agm.gameThree.settings";
    public static final float DEFAULT_VOLUME = 0.5f;
    private static final float MIN_VOLUME = 0.0f;
    private static final float MAX_VOLUME = 1.0f;
    public static final int MAX_LEVEL = 4;

    // Singleton: unique instance
    private static GameSettings instance;

    private boolean sound;
    private boolean music;
    private float volSound;
    private float volMusic;
    private boolean manualShooting;
    private int goldHighScore;
    private long goldHighScoreMillis;
    private int silverHighScore;
    private long silverHighScoreMillis;
    private int bronzeHighScore;
    private long bronzeHighScoreMillis;
    private ArrayMap<Integer, LevelState> levels;
    private Preferences prefs;
    private Json json;

    // Singleton: prevent instantiation from other classes
    private GameSettings() {
        levels = new ArrayMap<Integer, LevelState>();
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

    private LevelState getLevelState(String data) {
        return json.fromJson(LevelState.class, data);
    }

    public void load() {
        sound = prefs.getBoolean(SOUND, true);
        music = prefs.getBoolean(MUSIC, true);
        volSound = MathUtils.clamp(prefs.getFloat(VOLUME_SOUND, DEFAULT_VOLUME), MIN_VOLUME, MAX_VOLUME);
        volMusic = MathUtils.clamp(prefs.getFloat(VOLUME_MUSIC, DEFAULT_VOLUME), MIN_VOLUME, MAX_VOLUME);
        manualShooting = prefs.getBoolean(MANUAL_SHOOTING, true);
        goldHighScore = prefs.getInteger(GOLD_HIGH_SCORE, DEFAULT_GOLD_HIGH_SCORE);
        goldHighScoreMillis = prefs.getLong(GOLD_HIGH_SCORE_MILLIS, DEFAULT_GOLD_HIGH_SCORE_MILLIS);
        silverHighScore = prefs.getInteger(SILVER_HIGH_SCORE, DEFAULT_SILVER_HIGH_SCORE);
        silverHighScoreMillis = prefs.getLong(SILVER_HIGH_SCORE_MILLIS, DEFAULT_SILVER_HIGH_SCORE_MILLIS);
        bronzeHighScore = prefs.getInteger(BRONZE_HIGH_SCORE, DEFAULT_BRONZE_HIGH_SCORE);
        bronzeHighScoreMillis = prefs.getLong(BRONZE_HIGH_SCORE_MILLIS, DEFAULT_BRONZE_HIGH_SCORE_MILLIS);

        // Other levels
        int level = 1;
        String data;
        boolean availableLevel;
        boolean levelDefault;
        LevelState levelState = null;
        do {
            data = prefs.getString(LEVEL_STATE + level, "");
            if (data.isEmpty()) {
                levelDefault = level == 1;
                availableLevel = (DebugConstants.DEBUG_LEVELS && level <= MAX_LEVEL) || levelDefault;
                if (availableLevel) {
                    levelState = new LevelState(level, Hero.LIVES_START, 0, 0, levelDefault);
                }
            } else {
                levelState = getLevelState(data);
                availableLevel = levelState.isActive() || DebugConstants.DEBUG_LEVELS;
            }
            if (availableLevel) {
                levels.put(level, levelState);
                level++;
            }
        } while (availableLevel);
   }

    public void save() {
        prefs.putBoolean(SOUND, sound);
        prefs.putBoolean(MUSIC, music);
        prefs.putFloat(VOLUME_SOUND, volSound);
        prefs.putFloat(VOLUME_MUSIC, volMusic);
        prefs.putBoolean(MANUAL_SHOOTING, manualShooting);
        prefs.putInteger(GOLD_HIGH_SCORE, goldHighScore);
        prefs.putLong(GOLD_HIGH_SCORE_MILLIS, goldHighScoreMillis);
        prefs.putInteger(SILVER_HIGH_SCORE, silverHighScore);
        prefs.putLong(SILVER_HIGH_SCORE_MILLIS, silverHighScoreMillis);
        prefs.putInteger(BRONZE_HIGH_SCORE, bronzeHighScore);
        prefs.putLong(BRONZE_HIGH_SCORE_MILLIS, bronzeHighScoreMillis);
        for(LevelState levelState : levels.values()) {
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

    public int getGoldHighScore() {
        return goldHighScore;
    }

    public void setGoldHighScore(int goldHighScore) {
        this.goldHighScore = goldHighScore;
    }

    public long getGoldHighScoreMillis() {
        return goldHighScoreMillis;
    }

    public void setGoldHighScoreMillis(long goldHighScoreMillis) {
        this.goldHighScoreMillis = goldHighScoreMillis;
    }

    public int getSilverHighScore() {
        return silverHighScore;
    }

    public void setSilverHighScore(int silverHighScore) {
        this.silverHighScore = silverHighScore;
    }

    public long getSilverHighScoreMillis() {
        return silverHighScoreMillis;
    }

    public void setSilverHighScoreMillis(long silverHighScoreMillis) {
        this.silverHighScoreMillis = silverHighScoreMillis;
    }

    public int getBronzeHighScore() {
        return bronzeHighScore;
    }

    public void setBronzeHighScore(int bronzeHighScore) {
        this.bronzeHighScore = bronzeHighScore;
    }

    public long getBronzeHighScoreMillis() {
        return bronzeHighScoreMillis;
    }

    public void setBronzeHighScoreMillis(long bronzeHighScoreMillis) {
        this.bronzeHighScoreMillis = bronzeHighScoreMillis;
    }

    public void setStars(int level, int stars) {
        levels.get(level).setFinalStars(stars);
    }

    public ArrayMap<Integer, LevelState> getLevels() {
        return levels;
    }

    public void addActiveLevel(int level, int lives, int score) {
        levels.put(level, new LevelState(level, lives, score, 0, true));
    }

    public void removeLevel(int level) {
        levels.removeKey(level);
    }

    public boolean isNewGoldHighScore(int score) {
        return score > goldHighScore;
    }

    public boolean isNewSilverHighScore(int score) {
        return score > silverHighScore;
    }

    public boolean isNewBronzeHighScore(int score) {
        return score > bronzeHighScore;
    }
}
