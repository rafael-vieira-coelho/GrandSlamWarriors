package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AUS_Enemy2 extends Enemy {

    public AUS_Enemy2(float x, float y, Texture texture) {
        super(x, y, texture);

        if (texture != null && texture.getWidth() > texture.getHeight()) {
            int frameWidth = texture.getWidth() / 6;
            int frameHeight = texture.getHeight();
            this.regions = TextureRegion.split(texture, frameWidth, frameHeight)[0];
        }

        health = 3;
        setBoundsSize(70f, 50f);
        setDrawSize(90f, 50f);
        setDrawOffset(-20f, -1f);
        setPatrolRangeSymmetric(72f);
        animationSpeed = 5f; // mais lento que o canguru
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        if (isPlayerWithinX(112f)) {
            chasePlayerX(30f, delta);
        } else {
            patrol(16f, delta);
        }

        updateBounds();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
