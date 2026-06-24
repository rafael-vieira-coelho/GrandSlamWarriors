package com.grandslamwarriors.game.entities.enemies.flying;

import com.badlogic.gdx.graphics.Texture;

public class US_Flying extends FlyingEnemy {
    public US_Flying(float x, float y, Texture texture, Texture fireTexture) {
        super(x, y, texture, fireTexture);
        health = 3;
        setBoundsSize(32f, 32f);
        setDrawSize(64f, 64f);
        setDrawOffset(-16f, -16f);
        setPatrolRangeSymmetric(150f);
    }
}
