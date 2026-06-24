package com.grandslamwarriors.game.data;

public class CheckpointManager {
    private static float checkpointX = 100f;
    private static float checkpointY = 320f;

    public static void setCheckpoint(float x, float y) {
        checkpointX = x;
        checkpointY = y;
    }

    public static float getCheckpointX() {
        return checkpointX;
    }

    public static float getCheckpointY() {
        return checkpointY;
    }

    public static void reset() {
        checkpointX = 100f;
        checkpointY = 320f;
    }
}
