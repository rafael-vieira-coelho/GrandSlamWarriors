package com.grandslamwarriors.game.entities.enemies.flying;

import com.badlogic.gdx.graphics.Texture;

public class WIM_Flying extends FlyingEnemy {
    public WIM_Flying(float x, float y, Texture texture, Texture fireTexture) {
        super(x, y, texture, fireTexture);
        health = 2;
        setBoundsSize(32f, 32f);
        setDrawSize(120f, 120f);
        setDrawOffset(-16f, -16f);
        setPatrolRangeSymmetric(150f);
    }
}
