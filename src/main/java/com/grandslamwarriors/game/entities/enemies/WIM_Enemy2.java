package com.grandslamwarriors.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;

public class WIM_Enemy2 extends Enemy {

    public WIM_Enemy2(float x, float y, Texture texture) {
        super(x, y, texture);
        if (texture != null) {
            this.regions = com.badlogic.gdx.graphics.g2d.TextureRegion.split(texture, texture.getWidth() / 6, texture.getHeight())[0];
        }

        health = 3;
        setBoundsSize(60f, 50f);
        setDrawSize(60f, 50f);
        setDrawOffset(-30f, -2f);
        setPatrolRangeSymmetric(105f);
    }

    @Override
    public void update(float delta) {
        if (dead) return;

        if (isPlayerWithinX(200f)) {
            chasePlayerX(40f, delta);
        } else {
            patrol(20f, delta);
        }

        updateBounds();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
