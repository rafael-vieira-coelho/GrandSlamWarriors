package com.grandslamwarriors.game.tiles;

public enum TileType {
    EMPTY(false, false, 0f, 1f),
    GROUND(true, false, 0f, 1f),
    PLATFORM(true, false, 0f, 1f),
    WALL(true, false, 0f, 1f),
    FIRE(false, true, 1f, 1f),
    SPIKES(false, true, 1f, 1f),
    GAS(false, true, 1.5f, 1f),
    SLOW_CLAY(true, false, 0f, 0.5f),
    SLIPPERY_GRASS(true, false, 0f, 1.3f),

    // Portas secretas (invisíveis)
    DOOR_ENTRY(false, false, 0f, 1f),
    DOOR_EXIT(false, false, 0f, 1f),

    SPAWN_KANGAROO(false, false, 0f, 1f),
    SPAWN_CROCODILE(false, false, 0f, 1f),
    SPAWN_GOBLIN(false, false, 0f, 1f),
    SPAWN_CLAY_MONSTER(false, false, 0f, 1f),
    SPAWN_GRASS_CUTTER(false, false, 0f, 1f),
    SPAWN_QUEEN_GUARD(false, false, 0f, 1f),
    SPAWN_RAT(false, false, 0f, 1f),
    SPAWN_SKYSCRAPER(false, false, 0f, 1f),
    SPAWN_TROPHY(false, false, 0f, 1f),
    SPAWN_FLYING(false, false, 0f, 1f),
    SPAWN_BOSS(false, false, 0f, 1f);

    public final boolean solid;
    public final boolean damageOverTime;
    public final float damagePerSecond;
    public final float movementModifier;

    TileType(boolean solid, boolean damageOverTime, float dps, float moveMod) {
        this.solid = solid;
        this.damageOverTime = damageOverTime;
        this.damagePerSecond = dps;
        this.movementModifier = moveMod;
    }
}
