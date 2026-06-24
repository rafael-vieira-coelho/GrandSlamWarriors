package com.grandslamwarriors.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private static final String PREFS_NAME = "grandslam_settings";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_SFX_VOLUME = "sfx_volume";
    private static final String KEY_DIFFICULTY = "difficulty"; // 0=easy, 1=hard
    private static final String KEY_UNLOCKED_AGASSI = "unlocked_agassi";

    private static Boolean soundEnabledCache = null;
    private static Integer difficultyCache = null;

    private static Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public static boolean isSoundEnabled() {
        if (soundEnabledCache == null) {
            soundEnabledCache = getPrefs().getBoolean(KEY_SOUND_ENABLED, true);
        }
        return soundEnabledCache;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabledCache = enabled;
        getPrefs().putBoolean(KEY_SOUND_ENABLED, enabled);
        getPrefs().flush();
    }

    public static float getMusicVolume() {
        if (!isSoundEnabled()) return 0f;
        return getPrefs().getFloat(KEY_MUSIC_VOLUME, 0.5f);
    }

    public static void setMusicVolume(float volume) {
        getPrefs().putFloat(KEY_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    public static float getSfxVolume() {
        if (!isSoundEnabled()) return 0f;
        return getPrefs().getFloat(KEY_SFX_VOLUME, 0.8f);
    }

    public static void setSfxVolume(float volume) {
        getPrefs().putFloat(KEY_SFX_VOLUME, volume);
        getPrefs().flush();
    }

    // --- DIFICULDADE ---
    public static int getDifficulty() {
        if (difficultyCache == null) {
            difficultyCache = getPrefs().getInteger(KEY_DIFFICULTY, 0); // default easy
        }
        return difficultyCache;
    }

    public static void setDifficulty(int difficulty) {
        difficultyCache = difficulty;
        getPrefs().putInteger(KEY_DIFFICULTY, difficulty);
        getPrefs().flush();
    }

    // --- DESBLOQUEIO AGASSI ---
    public static boolean isAgassiUnlocked() {
        //return true;
        return getPrefs().getBoolean(KEY_UNLOCKED_AGASSI, false);
    }

    public static void setAgassiUnlocked(boolean unlocked) {
        getPrefs().putBoolean(KEY_UNLOCKED_AGASSI, unlocked);
        getPrefs().flush();
    }

    public static float getDifficultyFactor() {
        return getDifficulty() == 1 ? 1.4f : 1.0f;
    }
}
